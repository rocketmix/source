#!/bin/sh


INSTALL_DIR={{installpath}}
SERVICE_NAME={{servicename}}
UNINSTALL_SCRIPT={{uninstallscript}}

# Display banner
BANNER="$(cat <<-EOF
{{banner}}
EOF
)"
echo "$BANNER"

# Already exist check
systemctl is-enabled $SERVICE_NAME.service
if [ $? -eq 0 ]; then
	echo "Service already enabled.\n"
	echo "You can use :"
	echo "* sudo systemctl start $filename.service to start your service"
	echo "* sudo systemctl status $filename.service to see if your service is running"
	echo "* sudo systemctl stop $filename.service to stop your service"
	echo "* sudo journalctl -f to view and follow all logs\n"
	echo "Run sudo ./$UNINSTALL_SCRIPT to uninstall this service\n";
	exit 1
fi  


# Permission check
touch /etc/systemd/system
if [ $? -ne 0 ]; then  
	echo "Unable to access to /etc/systemd/system/ directory. Be carefull to run this script as root"
	#exit 1
fi


# Systemd configuration file check
filename=$SERVICE_NAME
file0="$INSTALL_DIR/$filename.war"
file1="$INSTALL_DIR/$filename.conf"
file2="$INSTALL_DIR/$filename.service"
allfilesfound=true
for index in $(seq 0 2)
do
	aFile=$(eval echo \$file$index)
	if [ ! -f "$aFile" ]
	then
		echo "File $aFile not found."
		allfilesfound=false
	fi
done
if ! $allfilesfound; then
	echo "Unable to find all files needed to install $filename as a Linux service. Please run sudo ./$filename.war --install again to regenerate all these files"
	exit 1
fi

# Update permissions 
executablefilename=$SERVICE_NAME
executablefilename0="$INSTALL_DIR/$filename.war"
executablefilename1="$INSTALL_DIR/$filename.conf"
executablefilename2="$INSTALL_DIR/$filename.service"
chmod 744 $executablefilename0
chmod 744 $executablefilename1
chmod 444 $executablefilename2


# Declare service
filename=$SERVICE_NAME
servicefile="$INSTALL_DIR/$filename.service"
systemctl enable $servicefile
if [ $? -ne 0 ]; then  
	echo "Service registration failed when trying to run : sudo systemctl enable $servicefile"
	exit 1
fi
systemctl start $filename.service
systemctl status $filename.service
echo "\nDone! Service is registered to start automatically on reboot.\n"
echo "You can use :"
echo "* sudo systemctl start $filename.service to start your service"
echo "* sudo systemctl status $filename.service to see if your service is running"
echo "* sudo systemctl stop $filename.service to stop your service"
echo "* sudo journalctl -f to view and follow all logs\n"
echo "Run sudo ./$UNINSTALL_SCRIPT to uninstall this service\n";
echo "Enjoy :)\n" 
