#!/bin/bash
#
#██████╗  ██████╗  ██████╗██╗  ██╗███████╗████████╗███╗   ███╗██╗██╗  ██╗
#██╔══██╗██╔═══██╗██╔════╝██║ ██╔╝██╔════╝╚══██╔══╝████╗ ████║██║╚██╗██╔╝
#██████╔╝██║   ██║██║     █████╔╝ █████╗     ██║   ██╔████╔██║██║ ╚███╔╝ 
#██╔══██╗██║   ██║██║     ██╔═██╗ ██╔══╝     ██║   ██║╚██╔╝██║██║ ██╔██╗ 
#██║  ██║╚██████╔╝╚██████╗██║  ██╗███████╗   ██║   ██║ ╚═╝ ██║██║██╔╝ ██╗
#╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝   ╚═╝   ╚═╝     ╚═╝╚═╝╚═╝  ╚═╝
#   :: RocketMiX Startup Script ::
#

### BEGIN INIT INFO
# Provides:          rocketmix
# Required-Start:    $remote_fs $syslog $network
# Required-Stop:     $remote_fs $syslog $network
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: RocketMiX packaged service
# Description:       RocketMiX is a microservice plateform base on Spring Boot Zuul, Eureka, Ribbon and Swagger UI parts
# chkconfig:         2345 99 01
### END INIT INFO

[[ -n "$DEBUG" ]] && set -x

# Initialize variables that cannot be provided by a .conf file
WORKING_DIR="$(pwd)"
# shellcheck disable=SC2153
[[ -n "$JARFILE" ]] && jarfile="$JARFILE"
[[ -n "$APP_NAME" ]] && identity="$APP_NAME"

# Follow symlinks to find the real jar and detect init.d script
cd "$(dirname "$0")" || exit 1
[[ -z "$jarfile" ]] && jarfile=$(pwd)/$(basename "$0")
while [[ -L "$jarfile" ]]; do
  if [[ "$jarfile" =~ init\.d ]]; then
    init_script=$(basename "$jarfile")
  else
    configfile="${jarfile%.*}.conf"
    # shellcheck source=/dev/null
    [[ -r ${configfile} ]] && source "${configfile}"
  fi
  jarfile=$(readlink "$jarfile")
  cd "$(dirname "$jarfile")" || exit 1
  jarfile=$(pwd)/$(basename "$jarfile")
done
jarfolder="$( (cd "$(dirname "$jarfile")" && pwd -P) )"
cd "$WORKING_DIR" || exit 1

# Inline script specified in build properties


# Source any config file
configfile="$(basename "${jarfile%.*}.conf")"

# Initialize CONF_FOLDER location defaulting to jarfolder
[[ -z "$CONF_FOLDER" ]] && CONF_FOLDER="${jarfolder}"
# shellcheck source=/dev/null
[[ -r "${CONF_FOLDER}/${configfile}" ]] && source "${CONF_FOLDER}/${configfile}"

# Initialize PID/LOG locations if they weren't provided by the config file
[[ -z "$PID_FOLDER" ]] && PID_FOLDER="/var/run"
[[ -z "$LOG_FOLDER" ]] && LOG_FOLDER="/var/log"
! [[ "$PID_FOLDER" == /* ]] && PID_FOLDER="$(dirname "$jarfile")"/"$PID_FOLDER"
! [[ "$LOG_FOLDER" == /* ]] && LOG_FOLDER="$(dirname "$jarfile")"/"$LOG_FOLDER"
! [[ -x "$PID_FOLDER" ]] && PID_FOLDER="/tmp"
! [[ -x "$LOG_FOLDER" ]] && LOG_FOLDER="/tmp"

# Set up defaults
[[ -z "$MODE" ]] && MODE="auto" # modes are "auto", "service" or "run"
[[ -z "$USE_START_STOP_DAEMON" ]] && USE_START_STOP_DAEMON="true"

# Create an identity for log/pid files
if [[ -z "$identity" ]]; then
  if [[ -n "$init_script" ]]; then
    identity="${init_script}"
  else
    identity=$(basename "${jarfile%.*}")_${jarfolder//\//}
  fi
fi

# Initialize log file name if not provided by the config file
[[ -z "$LOG_FILENAME" ]] && LOG_FILENAME="${identity}.log"

# Initialize stop wait time if not provided by the config file
[[ -z "$STOP_WAIT_TIME" ]] && STOP_WAIT_TIME="60"

# ANSI Colors
echoRed() { echo $'\e[0;31m'"$1"$'\e[0m'; }
echoGreen() { echo $'\e[0;32m'"$1"$'\e[0m'; }
echoYellow() { echo $'\e[0;33m'"$1"$'\e[0m'; }

# Utility functions
checkPermissions() {
  touch "$pid_file" &> /dev/null || { echoRed "Operation not permitted (cannot access pid file)"; return 4; }
  touch "$log_file" &> /dev/null || { echoRed "Operation not permitted (cannot access log file)"; return 4; }
}

isRunning() {
  ps -p "$1" &> /dev/null
}

await_file() {
  end=$(date +%s)
  let "end+=10"
  while [[ ! -s "$1" ]]
  do
    now=$(date +%s)
    if [[ $now -ge $end ]]; then
      break
    fi
    sleep 1
  done
}

# Determine the script mode
# CUSTOM CHANGE -> disable default action : action="run"
# CUSTOM CHANGE -> Force running mode
MODE="service" 
if [[ "$MODE" == "auto" && -n "$init_script" ]] || [[ "$MODE" == "service" ]]; then
  action="$1"
  shift
fi

# Build the pid and log filenames
PID_FOLDER="$PID_FOLDER/${identity}"
pid_file="$PID_FOLDER/${identity}.pid"
log_file="$LOG_FOLDER/$LOG_FILENAME"

# Determine the user to run as if we are root
# shellcheck disable=SC2012
[[ $(id -u) == "0" ]] && run_user=$(ls -ld "$jarfile" | awk '{print $3}')

# Find Java
if [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
    javaexe="$JAVA_HOME/bin/java"
elif type -p java > /dev/null 2>&1; then
    javaexe=$(type -p java)
elif [[ -x "/usr/bin/java" ]];  then
    javaexe="/usr/bin/java"
else
    echo "Unable to find Java"
    exit 1
fi

arguments=(-Dsun.misc.URLClassPath.disableJarChecking=true $JAVA_OPTS -jar "$jarfile" $RUN_ARGS "$@")

# Action functions
start() {
  # Custom line to get service status : begin
  if is_installed; then { do_service_start; return 0; }; fi; 
  # Custom line to get service status : end
  if [[ -f "$pid_file" ]]; then
    pid=$(cat "$pid_file")
    isRunning "$pid" && { echoYellow "Already running [$pid]"; return 0; }
  fi
  do_start "$@"
}
do_start() {
  working_dir=$(dirname "$jarfile")
  pushd "$working_dir" > /dev/null
  if [[ ! -e "$PID_FOLDER" ]]; then
    mkdir -p "$PID_FOLDER" &> /dev/null
    if [[ -n "$run_user" ]]; then
      chown "$run_user" "$PID_FOLDER"
    fi
  fi
  if [[ ! -e "$log_file" ]]; then
    touch "$log_file" &> /dev/null
    if [[ -n "$run_user" ]]; then
      chown "$run_user" "$log_file"
    fi
  fi
  if [[ -n "$run_user" ]]; then
    checkPermissions || return $?
    if [ $USE_START_STOP_DAEMON = true ] && type start-stop-daemon > /dev/null 2>&1; then
      start-stop-daemon --start --quiet \
        --chuid "$run_user" \
        --name "$identity" \
        --make-pidfile --pidfile "$pid_file" \
        --background --no-close \
        --startas "$javaexe" \
        --chdir "$working_dir" \
        -- "${arguments[@]}" \
        >> "$log_file" 2>&1
      await_file "$pid_file"
    else
      su -s /bin/sh -c "$javaexe $(printf "\"%s\" " "${arguments[@]}") >> \"$log_file\" 2>&1 & echo \$!" "$run_user" > "$pid_file"
    fi
    pid=$(cat "$pid_file")
  else
    checkPermissions || return $?
    "$javaexe" "${arguments[@]}" >> "$log_file" 2>&1 &
    pid=$!
    disown $pid
    echo "$pid" > "$pid_file"
  fi
  [[ -z $pid ]] && { echoRed "Failed to start"; return 1; }
  echoGreen "Started [$pid]"
}

stop() {
  # Custom line to get service status : begin
  if is_installed; then { do_service_stop; return 0; }; fi; 
  # Custom line to get service status : end
  working_dir=$(dirname "$jarfile")
  pushd "$working_dir" > /dev/null
  [[ -f $pid_file ]] || { echoYellow "Not running (pidfile not found)"; return 0; }
  pid=$(cat "$pid_file")
  isRunning "$pid" || { echoYellow "Not running (process ${pid}). Removing stale pid file."; rm -f "$pid_file"; return 0; }
  do_stop "$pid" "$pid_file"
}

do_stop() {
  kill "$1" &> /dev/null || { echoRed "Unable to kill process $1"; return 1; }
  for i in $(seq 1 $STOP_WAIT_TIME); do
    isRunning "$1" || { echoGreen "Stopped [$1]"; rm -f "$2"; return 0; }
    [[ $i -eq STOP_WAIT_TIME/2 ]] && kill "$1" &> /dev/null
    sleep 1
  done
  echoRed "Unable to kill process $1";
  return 1;
}

force_stop() {
  # Custom line to get service status : begin
  if is_installed; then { do_service_force_stop; return 0; }; fi; 
  # Custom line to get service status : end
  [[ -f $pid_file ]] || { echoYellow "Not running (pidfile not found)"; return 0; }
  pid=$(cat "$pid_file")
  isRunning "$pid" || { echoYellow "Not running (process ${pid}). Removing stale pid file."; rm -f "$pid_file"; return 0; }
  do_force_stop "$pid" "$pid_file"
}

do_force_stop() {
  kill -9 "$1" &> /dev/null || { echoRed "Unable to kill process $1"; return 1; }
  for i in $(seq 1 $STOP_WAIT_TIME); do
    isRunning "$1" || { echoGreen "Stopped [$1]"; rm -f "$2"; return 0; }
    [[ $i -eq STOP_WAIT_TIME/2 ]] && kill -9 "$1" &> /dev/null
    sleep 1
  done
  echoRed "Unable to kill process $1";
  return 1;
}

restart() {
  # Custom line to get service status : begin
  if is_installed; then { do_service_restart; return 0; }; fi; 
  # Custom line to get service status : end
  stop && start
}

force_reload() {
  # Custom line to get service status : begin
  if is_installed; then { do_service_force_reload; return 0; }; fi; 
  # Custom line to get service status : end
  working_dir=$(dirname "$jarfile")
  pushd "$working_dir" > /dev/null
  [[ -f $pid_file ]] || { echoRed "Not running (pidfile not found)"; return 7; }
  pid=$(cat "$pid_file")
  rm -f "$pid_file"
  isRunning "$pid" || { echoRed "Not running (process ${pid} not found)"; return 7; }
  do_stop "$pid" "$pid_file"
  do_start
}

status() {
  # Custom line to get service status : begin
  if is_installed; then { do_service_status; return 0; }; fi; 
  # Custom line to get service status : end
  working_dir=$(dirname "$jarfile")
  pushd "$working_dir" > /dev/null
  [[ -f "$pid_file" ]] || { echoRed "Not running"; return 3; }
  pid=$(cat "$pid_file")
  isRunning "$pid" || { echoRed "Not running (process ${pid} not found)"; return 1; }
  echoGreen "Running [$pid]"
  return 0
}

run() {
  pushd "$(dirname "$jarfile")" > /dev/null
  "$javaexe" "${arguments[@]}"
  result=$?
  popd > /dev/null
  return "$result"
}

# CUSTOM MODIFICATION TO SPRING STANDARD SCRIPT
service_template="$(cat <<-EOF
[Unit]
Description={{service_name}}
Requires=network.target
After=network.target

# If needed, enable this option to reboot immediately in process failure
# OnFailure=systemd-reboot.service

[Service]
Type=simple
User={{username}}
Group={{groupname}}
ExecStart={{jarfile}} run
SuccessExitStatus=143
RemainAfterExit=no
Restart=on-failure
RestartSec=10s
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
EOF
)"

service_config_template="$(cat <<-EOF
JAVA_OPTS="-server -Dspring.config.additional-location=file:./{{service_name}}.properties-XX:+AlwaysPreTouch -XX:+UseG1GC -XX:+ScavengeBeforeFullGC -XX:+DisableExplicitGC -Xms1024M -Xmx1024M -XX:+ExitOnOutOfMemoryError"
EOF
)"

service_additional_properties_template="$(cat <<-EOF
port=8085
managementServerURL=http://localhost:8761
EOF
)"


install() {
  working_dir=$(dirname "$jarfile")
  service_shortname=$(basename $jarfile | sed 's/-[0-9]\+.*//' | sed 's/\.jar//I' | sed 's/\.war//I')
  service_name="$(basename "${jarfile%.*}")"
  service_template=${service_template//\{\{service_name\}\}/$service_name}
  service_template=${service_template//\{\{jarfile\}\}/$jarfile}
  username=$(ls -ld "$jarfile" | awk '{print $3}')
  groupname=$(ls -ld "$jarfile" | awk '{print $4}')
  service_template=${service_template//\{\{username\}\}/$username}
  service_template=${service_template//\{\{groupname\}\}/$groupname}
	# Make sure only root can run this script
  [[ $EUID -ne 0 ]] && { echoRed "You must be root to run this command"; return 1; }
  # Check if service already installed
  [[ $(systemctl is-enabled $service_name 2>&1) && $? -eq 0 ]] && { echoGreen "Service $service_name already installed"; return 0; } 
  # Generate service file
  service_file="$working_dir/$service_name.service"
  [[ -f $service_file ]] || { printf "$service_template" > "$service_file"; }
  [[ -f $service_file ]] && { chown $username:$groupname $service_file; }
  # Generate spring boot config file if needed
  service_config_template=${service_config_template//\{\{service_name\}\}/$service_name}
  service_config_file_shortname="$working_dir/$service_shortname.conf"
  service_config_file_fullname="$working_dir/$service_name.conf"
  [[ -f $service_config_file_shortname ]] || { printf "$service_config_template" > "$service_config_file_shortname"; echoGreen "Spring Boot config file $service_config_file_shortname generated"; }
  [[ -f $service_config_file_fullname ]] || { ln -s $service_config_file_shortname $service_config_file_fullname; echoGreen "Symboloc link $service_config_file_fullname generated"; }
  [[ -f $service_config_file_shortname ]] && { chown $username:$groupname $service_config_file_shortname; }
  [[ -h $service_config_file_fullname ]] && { chown -h $username:$groupname $service_config_file_fullname; }
  # Generate spring boot additional properties file
  service_additional_properties_file_shortname="$working_dir/$service_shortname.properties"
  service_additional_properties_file_fullname="$working_dir/$service_name.properties"
  [[ -f $service_additional_properties_file_shortname ]] || { printf "$service_additional_properties_template" > "$service_additional_properties_file_shortname"; echoGreen "Spring Boot properties file $service_additional_properties_file_shortname generated"; }
  [[ -f $service_additional_properties_file_fullname ]] || { ln -s $service_additional_properties_file_shortname $service_additional_properties_file_fullname; echoGreen "Symboloc link $service_additional_properties_file_fullname generated"; }
  [[ -f $service_additional_properties_file_shortname ]] && { chown $username:$groupname $service_additional_properties_file_shortname; }
  [[ -h $service_additional_properties_file_fullname ]] && { chown -h $username:$groupname $service_additional_properties_file_fullname; }
  # Install service
  systemctl enable $service_file 2>&1
  [[ $? -ne 0 ]]  && { echoRed "Installation failed"; return 1; }
  # Start service
  systemctl start $service_name 2>&1
  [[ $? -ne 0 ]]  && { echoYellow "Service successfully installed but failed to start service"; return 1; }
  systemctl status $service_name
  echoGreen "Service successfully installed"
	return 0
}

uninstall() {
  working_dir=$(dirname "$jarfile")
  service_shortname=$(basename $jarfile | sed 's/-[0-9]\+.*//' | sed 's/\.jar//I' | sed 's/\.war//I')
  service_name="$(basename "${jarfile%.*}")"
  service_template=${service_template//\{\{service_name\}\}/$service_name}
  service_template=${service_template//\{\{jarfile\}\}/$jarfile}
  username=$(ls -ld "$jarfile" | awk '{print $3}')
  groupname=$(ls -ld "$jarfile" | awk '{print $4}')
  service_template=${service_template//\{\{username\}\}/$username}
  service_template=${service_template//\{\{groupname\}\}/$groupname}
	# Make sure only root can run this script
  [[ $EUID -ne 0 ]] && { echoRed "You must be root to run this command"; return 1; }
  # Check if service already installed
  [[ $(systemctl is-enabled $service_name 2>&1) && $? -ne 0 ]] && { echoYellow "Service $service_name not found"; return 1; } 
  # Stop service
  systemctl stop $service_name 2>&1
  [[ $? -ne 0 ]]  && { echoYellow "Failed to stop service. Maybe already stopped ?"; }
  # Uninstall service
  systemctl disable $service_name 2>&1
  [[ $? -ne 0 ]]  && { echoRed "Uninstallation failed"; return 1; }
  echoGreen "Service successfully uninstalled"
	return 0
}

is_installed() {
  # Check is deployed as Linux service
  working_dir=$(dirname "$jarfile")
  service_shortname=$(basename $jarfile | sed 's/-[0-9]\+.*//' | sed 's/\.jar//I' | sed 's/\.war//I')
  service_name="$(basename "${jarfile%.*}")"
  [[ $(systemctl is-enabled $service_name 2>&1) && $? -eq 0 ]] && { return 0;} # 0 = true 
  return 1; # 1 = false
}

do_service_status() {
  # Retreive Linux service status
  working_dir=$(dirname "$jarfile")
  service_shortname=$(basename $jarfile | sed 's/-[0-9]\+.*//' | sed 's/\.jar//I' | sed 's/\.war//I')
  service_name="$(basename "${jarfile%.*}")"
  systemctl status $service_name
}

do_service_start() {
  # Start Linux service 
  working_dir=$(dirname "$jarfile")
  service_shortname=$(basename $jarfile | sed 's/-[0-9]\+.*//' | sed 's/\.jar//I' | sed 's/\.war//I')
  service_name="$(basename "${jarfile%.*}")"
  # Make sure only root can run this script
  [[ $EUID -ne 0 ]] && { echoRed "You must be root to run this command"; return 1; }
  systemctl start $service_name
  [[ $? -ne 0 ]]  && { echoRed "Start failed"; return 1; }
  echoGreen "Service successfully started"
  return 0;
}

do_service_stop() {
  # Stop Linux service 
  working_dir=$(dirname "$jarfile")
  service_shortname=$(basename $jarfile | sed 's/-[0-9]\+.*//' | sed 's/\.jar//I' | sed 's/\.war//I')
  service_name="$(basename "${jarfile%.*}")"
  # Make sure only root can run this script
  [[ $EUID -ne 0 ]] && { echoRed "You must be root to run this command"; return 1; }
  systemctl stop $service_name
  [[ $? -ne 0 ]]  && { echoRed "Stop failed"; return 1; }
  echoGreen "Service successfully stopped"
  return 0;
}

do_service_restart() {
  # Restart Linux service 
  working_dir=$(dirname "$jarfile")
  service_shortname=$(basename $jarfile | sed 's/-[0-9]\+.*//' | sed 's/\.jar//I' | sed 's/\.war//I')
  service_name="$(basename "${jarfile%.*}")"
  # Make sure only root can run this script
  [[ $EUID -ne 0 ]] && { echoRed "You must be root to run this command"; return 1; }
  systemctl restart $service_name
  [[ $? -ne 0 ]]  && { echoRed "Restart failed"; return 1; }
  echoGreen "Service successfully restarted"
  return 0;
}

do_service_force_stop() {
   # Kill Linux service 
  working_dir=$(dirname "$jarfile")
  service_shortname=$(basename $jarfile | sed 's/-[0-9]\+.*//' | sed 's/\.jar//I' | sed 's/\.war//I')
  service_name="$(basename "${jarfile%.*}")"
  # Make sure only root can run this script
  [[ $EUID -ne 0 ]] && { echoRed "You must be root to run this command"; return 1; }
  systemctl kill $service_name
  [[ $? -ne 0 ]]  && { echoRed "Stop failed"; return 1; }
  echoGreen "Service successfully stopped"
  return 0;
}

do_service_force_reload() {
  do_service_force_stop && do_service_restart
}


# Call the appropriate action function
case "$action" in
start)
  start "$@"; exit $?;;
stop)
  stop "$@"; exit $?;;
force-stop)
  force_stop "$@"; exit $?;;
restart)
  restart "$@"; exit $?;;
force-reload)
  force_reload "$@"; exit $?;;
status)
  status "$@"; exit $?;;
run)
  run "$@"; exit $?;;
install)
  install "$@"; exit $?;;
uninstall)
  uninstall "$@"; exit $?;;
*)
  echo "Usage: $0 {start|stop|force-stop|restart|force-reload|status|run|install|uninstall}"; exit 1;
esac

exit 0

