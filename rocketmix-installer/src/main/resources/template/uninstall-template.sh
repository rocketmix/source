#!/bin/sh


INSTALL_DIR={{installpath}}
SERVICE_NAME={{servicename}}
INSTALL_SCRIPT={{installscript}}

# Display banner
BANNER="$(cat <<-EOF
{{banner}}
EOF
)"
echo "$BANNER"

# Already exist check
systemctl is-enabled $SERVICE_NAME.service
if [ $? -ne 0 ]; then
	echo "\nService not found. Did you run install script before running this one ?\n"
	echo "Run sudo ./$INSTALL_SCRIPT if you want to re-install this service\n";
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
servicefile="$INSTALL_DIR/$filename.service"
if [ ! -f "$servicefile" ]
then
	echo "Unable to find $servicefile needed to uninstall $filename Linux service.\n"
	exit 1
fi


# Remove service
filename=$SERVICE_NAME
systemctl kill $filename.service
systemctl disable $filename.service
if [ $? -ne 0 ]; then  
	echo "\nService uninstall failed when trying to run : sudo systemctl disable $filename.service\n"
	exit 1
fi
echo "\nDone! Service is killed and uninstalled.\n"
echo "Run sudo ./$INSTALL_SCRIPT if you want to re-install this service\n";

