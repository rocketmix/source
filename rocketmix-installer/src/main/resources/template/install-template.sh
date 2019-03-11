#!/bin/sh


INSTALL_DIR={{installpath}}
SERVICE_NAME={{servicename}}
UNINSTALL_SCRIPT={{uninstallscript}}

# Display banner
BANNER="$(cat <<-EOF
{{banner}}
EOF
)"
printf "$BANNER\n"

# Permission check
touch /etc/systemd/system
if [ $? -ne 0 ]; then  
	printf "Unable to access to /etc/systemd/system/ directory. Be carefull to run this script AS ROOT (sudo or su -c)\n"
	exit 1
fi


# Already exist check
systemctl is-enabled $SERVICE_NAME.service
if [ $? -eq 0 ]; then
	printf "Service already enabled.\n"
	printf "You can use :\n"
	printf "* (sudo or su -c) systemctl start $filename.service to start your service\n"
	printf "* (sudo or su -c) systemctl status $filename.service to see if your service is running\n"
	printf "* (sudo or su -c) systemctl stop $filename.service to stop your service\n"
	printf "* (sudo or su -c) journalctl -f to view and follow all logs\n"
	printf "Run ./$UNINSTALL_SCRIPT AS ROOT (sudo or su -c) to uninstall this service\n";
	exit 1
fi  


# Systemd configuration file check
filename=$SERVICE_NAME
file0="$INSTALL_DIR/$filename.war"
file1="$INSTALL_DIR/$filename.conf"
file2="$INSTALL_DIR/$filename.service"
allfilesfound=true
for index in $(seq 0 2)
do
	aFile=$(eval printf \$file$index)
	if [ ! -f "$aFile" ]
	then
		printf "File $aFile not found.\n"
		allfilesfound=false
	fi
done
if ! $allfilesfound; then
	printf "Unable to find all files needed to install $filename as a Linux service. Please run ./$filename.war --install again to regenerate all these files\n"
	exit 1
fi

# Update permissions 
executablefilename=$SERVICE_NAME
executablefilename0="$INSTALL_DIR/$filename.war"
executablefilename1="$INSTALL_DIR/$filename.conf"
executablefilename2="$INSTALL_DIR/$filename.service"
chmod 766 $executablefilename0
chmod 766 $executablefilename1
chmod 666 $executablefilename2


# Declare service
filename=$SERVICE_NAME
servicefile="$INSTALL_DIR/$filename.service"
systemctl enable $servicefile
if [ $? -ne 0 ]; then  
	printf "Service registration failed when trying to run : sudo systemctl enable $servicefile\n"
	exit 1
fi
systemctl start $filename.service
systemctl status $filename.service
printf "\nDone! Service is registered to start automatically on reboot.\n"
printf "You can use :\n"
printf "* (sudo or su -c) systemctl start $filename.service to start your service\n"
printf "* (sudo or su -c) systemctl status $filename.service to see if your service is running\n"
printf "* (sudo or su -c) systemctl stop $filename.service to stop your service\n"
printf "* (sudo or su -c) journalctl -f to view and follow all logs\n"
printf "Run ./$UNINSTALL_SCRIPT AS ROOT (sudo or su -) to uninstall this service\n";
printf "Enjoy :)\n" 
