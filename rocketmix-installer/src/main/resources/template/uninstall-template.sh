#!/bin/sh


INSTALL_DIR={{installpath}}
SERVICE_NAME={{servicename}}

# Display banner
BANNER="$(cat <<-EOF
{{banner}}
EOF
)"
echo "$BANNER"

# Already exist check
systemctl is-enabled $SERVICE_NAME.service
if [ $? -ne 0 ]; then
	echo "Service not found. Did you run install script before running this one ?"
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
	echo "Unable to find $servicefile needed to uninstall $filename Linux service. You can try to re-run install script to regenerate this file."
	exit 1
fi


# Remove service
filename=$SERVICE_NAME
servicefile="$INSTALL_DIR/$filename.service"
systemctl kill $servicefile
systemctl disable $servicefile
if [ $? -ne 0 ]; then  
	echo "Service uninstall failed when trying to run : systemctl disable $servicefile"
	exit 1
fi
echo "Done! Service is killed and uninstalled."
