#!/bin/sh


INSTALL_DIR={{installpath}}
SERVICE_NAME={{servicename}}
INSTALL_SCRIPT={{installscript}}

# Display banner
BANNER="$(cat <<-EOF
{{banner}}
EOF
)"
printf "$BANNER\n"

# Permission check
touch /etc/systemd/system
if [ $? -ne 0 ]; then  
	printf "Unable to access to /etc/systemd/system/ directory. sudo permissions required.\n"
  	SUDO="sudo"
    sudo echo "" # Always prompt for password
	if [ $? -ne 0 ]; then 
        exit 1
    fi
fi

# Already exist check
$SUDO systemctl is-enabled $SERVICE_NAME.service
if [ $? -ne 0 ]; then
	printf "\nService not found. Did you run install script before running this one ?\n"
	printf "Run ./$INSTALL_SCRIPT AS ROOT (sudo or su -) if you want to re-install this service\n";
	exit 1
fi  

# Start service
$SUDO systemctl status $SERVICE_NAME.service
printf "\nDone!\n"

