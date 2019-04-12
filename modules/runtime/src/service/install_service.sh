#!/bin/bash

### Setup

# Override if running script outside distro
XP_DISTRO_PATH="";

# App info
VERSION="@version@"
APP_NAME="xp"
DISTRO_NAME="enonic-xp-${VERSION}"

# User to run as service
USER="xp"
USER_ID=1337
USER_HOME="/home/${USER}"

# Files location<
XP_INSTALL_BASE="/opt/enonic"
LINK_NAME="xp"
XP_INSTALL_PATH=${XP_INSTALL_BASE}/${LINK_NAME}
XP_HOME="${USER_HOME}/enonic/${APP_NAME}"
XP_INSTALL_FULL_PATH="$XP_INSTALL_BASE/${DISTRO_NAME}"

# Service config
SERVICE_SCRIPT_LOCATION="/etc/init.d/${APP_NAME}"
CONFIG_LOCATION="/etc/${APP_NAME}.conf"
SERVICE_LOG="/var/log/${APP_NAME}";

LINUX_DISTRO=""

### Helpers

CYAN="\033[36m"
BLUE="\033[34m"
GRAY="\033[33m"
RED="\033[31m"
END="\033[0m\033[27m"
INVERSE="\033[7m"

# Functions

function br() {
    printf "\n"
}

function message() {
    printf "$INVERSE$BLUE$1$END\n"
}


function info() {
    printf "* $BLUE$1$END\n"
}

function details() {
    printf " - $GRAY$1$END\n"
}

function error() {
    printf "$RED$1$END\n"
}

function die() {
    error "$1"
    exit 1
}

_cp()
{
    sudo cp -R $1 $2

    if (($? > 0)); then
        printf '%s\n' 'cannot copy directory ${1} to ${2}' >&2
        exit 1
    fi
}

_mkdir()
{
    if [ ! -d $1 ]; then
        sudo mkdir -p $1

        if (($? > 0)); then
            printf '%s\n' 'cannot create directory $1' >&2
            exit 1
        fi
    fi
}

_chown()
{
    sudo chown -R ${USER}:${USER} $1

     if (($? > 0)); then
            printf '%s\n' 'cannot chown to ${USER}:${USER} on $1' >&2
            exit 1
     fi
}

### Main

getLinuxDistro()
{
    info "Detecting Linux distribution"

    DISTRO_STRING=`sudo cat /proc/version`
    if [[ ${DISTRO_STRING} == *"Ubuntu"* ]];
    then
        LINUX_DISTRO="Ubuntu";
        details "Ubuntu detected";
	elif [[ ${DISTRO_STRING} == *"Red Hat"* ]]; then
        LINUX_DISTRO="RedHat";
        details "RedHat detected";
	else
		LINUX_DISTRO="Generic";
	    error "Unknown distrubtion detected";
    fi
}

createUser()
{
    message "Creating user ${USER}"

    ret=false
    sudo getent passwd ${USER} >/dev/null 2>&1 && ret=true

    if ${ret}; then
        details "User ${USER} exists already"
    else
        doCreateUser
        info "User ${USER} created successfully"
    fi
}

doCreateUser()
{
	if [ "$LINUX_DISTRO" = "Ubuntu" ]; then
		sudo adduser --home ${USER_HOME} --gecos "" --UID ${USER_ID} --disabled-password ${USER}
	elif [ "$LINUX_DISTRO" = "RedHat" ]; then
		sudo adduser -d ${USER_HOME} -m -r -u ${USER_ID} ${USER}
	else
		sudo adduser --home ${USER_HOME} --gecos "" --UID ${USER_ID} --disabled-password ${USER}
	fi
}


installFiles()
{
    message "Installing files"

    setXPDistroPath
    info "Installing xp distro from path: ${XP_DISTRO_PATH}"
    copyDistroToInstallPath
    createXPHome
}

setXPDistroPath()
{
    if [ -z ${XP_DISTRO_PATH} ]; then
        details "No explicit xp distro path set, using relative from script"
        setScriptPath
        XP_DISTRO_PATH="`( cd \"${SCRIPT_PATH}/..\" && pwd )`"
    fi
}

setScriptPath()
{
    SCRIPT_PATH="`dirname \"$0\"`"              # relative
    SCRIPT_PATH="`( cd \"${SCRIPT_PATH}\" && pwd )`"  # absolutized and normalized
    if [ -z "${SCRIPT_PATH}" ] ; then
        exit 1  # fail
    fi

    details "Install base: ${SCRIPT_PATH}"
}

copyDistroToInstallPath()
{
    _mkdir ${XP_INSTALL_FULL_PATH}
    cd ${XP_INSTALL_FULL_PATH}

    _cp ${XP_DISTRO_PATH}/jdk ${XP_INSTALL_FULL_PATH}
    _cp ${XP_DISTRO_PATH}/bin ${XP_INSTALL_FULL_PATH}
    _cp ${XP_DISTRO_PATH}/lib ${XP_INSTALL_FULL_PATH}
    _cp ${XP_DISTRO_PATH}/system ${XP_INSTALL_FULL_PATH}
    _cp ${XP_DISTRO_PATH}/NOTICE.txt ${XP_INSTALL_FULL_PATH}
    _cp ${XP_DISTRO_PATH}/LICENSE.txt ${XP_INSTALL_FULL_PATH}
    createLink
}

createXPHome()
{
    if [ ! -d ${XP_HOME} ]; then
        info "Create XP_HOME at ${XP_HOME}"
        _mkdir ${XP_HOME}
        _cp "${XP_DISTRO_PATH}/home/*" ${XP_HOME}
        _chown ${XP_HOME}
    else
        details "XP_HOME exists at ${XP_HOME}"
    fi
}

createLink()
{
    if [ -h ${XP_INSTALL_BASE}/${APP_NAME} ]; then
        sudo unlink ${XP_INSTALL_BASE}/${APP_NAME}
    fi

    info "Creating symbolic link ${XP_INSTALL_PATH} to ${XP_INSTALL_FULL_PATH}"
    sudo ln -s ${XP_INSTALL_FULL_PATH} ${XP_INSTALL_PATH}
}

configureService()
{
    message "Configure service"
    installServiceScript
    installConfig
}

installServiceScript()
{
  info "Installing service script..."
    _cp "${XP_DISTRO_PATH}/service/init.d/xp" ${SERVICE_SCRIPT_LOCATION}
}

installConfig()
{
    if [ ! -f ${CONFIG_LOCATION} ]; then
        _cp "${XP_DISTRO_PATH}/service/xp.conf" ${CONFIG_LOCATION}
    else
        details "Config exists at ${CONFIG_LOCATION}"
    fi
}

initLogDir()
{
    _mkdir ${SERVICE_LOG}
    _chown ${SERVICE_LOG}
}

showSummary()
{
    message "Enonic XP ${VERSION} installed successfully"
    info " Run-user: ${USER}"
    info " Installation path: $XP_INSTALL_PATH"
    info " Service definition: $SERVICE_SCRIPT_LOCATION"
    info " Service config: $CONFIG_LOCATION"
    info " Service logs: $SERVICE_LOG/"
    info " XP home: $XP_HOME"
    info " Application logs: $XP_HOME/logs"
    br
    info " Remember to update ${CONFIG_LOCATION} with settings matching your environment"
}

main() {
    getLinuxDistro
    createUser
    installFiles
    configureService
    initLogDir
    showSummary
}

main