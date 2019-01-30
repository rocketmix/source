#!/bin/sh

INSTALL_DIR=/home/depellegrin/git/rocketmix.source/rocketmix-routing-server/target/classes/
SERVICE_NAME="rocketmix-routing-server"

# Already exist check
systemctl is-enabled $SERVICE_NAME.service
if [ $? -eq 0 ]; then
	echo "Service already enabled. Please use uninstall script before running this one"
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
	echo "Unable to find all files needed to install $filename as a Linux service. Please run '$filename.war --install' again to regenerate all these files"
	exit 1
fi

# Update permissions 
executablefilename=$SERVICE_NAME
executablefilename0="$INSTALL_DIR/$filename.war"
executablefilename1="$INSTALL_DIR/$filename.conf"
chmod +x $executablefilename0
chmod +x $executablefilename1


# Declare service
filename=$SERVICE_NAME
servicefile="$INSTALL_DIR/$filename.service"
systemctl enable $servicefile
echo "Done! Service will start automatically on next reboot. You can use systemctl start|stop|status to control your service"