#!/bin/bash

if [ -z $1 ]; then
     echo "Missing version-argument"
     exit 0
fi

VERSION=$1


XP_INSTALL="/opt/enonic"
LINK_NAME="xp"
INSTALL_HOME=${XP_INSTALL}/${LINK_NAME}
DISTRO="distro-${VERSION}.zip"
REPO_URL="http://repo.enonic.com/public/com/enonic/xp/distro/${VERSION}/${DISTRO}"

USER="xp"
USER_ID=1337
USER_HOME="/home/${USER}"

APP_NAME="xp"
XP_HOME="${USER_HOME}/enonic/${APP_NAME}"

SERVICE_SCRIPT_LOCATION="/etc/init.d/${APP_NAME}"
CONFIG_LOCATION="/etc/${APP_NAME}.conf"

LINUX_DISTRO=""

checkDistro()
{
    DISTRO_STRING=`sudo cat /proc/version`

    if [[ ${DISTRO_STRING} == *"Ubuntu"* ]]
    then
        LINUX_DISTRO="Ubuntu";
        echo "Ubuntu detected";
    else
        echo "This script is for Ubuntu distrubutions only"
        exit 1;
    fi
}

addUser()
{
    ret=false
    sudo getent passwd ${USER} >/dev/null 2>&1 && ret=true

    if ${ret}; then
        echo "User ${USER} exists already"
    else
        echo "Create user ${USER}"
        sudo adduser --home ${USER_HOME} --gecos "" --UID ${USER_ID} --disabled-password ${USER}
    fi
}

install()
{
    echo "Installing..."

    _mkdir "${XP_INSTALL}"
    cd ${XP_INSTALL}

    do_download
    unpack
    create_link

    sudo rm ${XP_INSTALL}/${DISTRO}
}

do_download()
{
    if [ ! -f ${XP_INSTALL}/${DISTRO} ]; then
        echo "Downloading from ${REPO_URL} "
        sudo curl --fail -o ${XP_INSTALL}/${DISTRO} ${REPO_URL}

        if (($? > 0)); then
            printf '%s\n' 'not able to download distribution' >&2
            exit 1
        fi
    else
        echo "Distro ${DISTRO} found"
    fi
}

unpack()
{
    echo "Unpack and setup..."
    sudo unzip -qq ${DISTRO}

    if (($? > 0)); then
        printf '%s\n' 'cannot unzip distro' >&2
        exit 1
    fi

}

create_link()
{
    if [ -h ${XP_INSTALL}/xp ]; then
        sudo unlink ${XP_INSTALL}/xp
    fi

    echo "Creating symbolic link ${XP_INSTALL}/xp to ${XP_INSTALL}/enonic-xp-${VERSION}"
    sudo ln -s ${XP_INSTALL}/enonic-xp-${VERSION} ${XP_INSTALL}/xp
}

installService()
{
    echo "Installing service script..."
    _cp "${INSTALL_HOME}/service/init.d/xp" ${SERVICE_SCRIPT_LOCATION}
}

createXPHome()
{
    if [ ! -d ${XP_HOME} ]; then
        echo "Create XP_HOME at ${XP_HOME}"
        _mkdir ${XP_HOME}
        _cp "${INSTALL_HOME}/home/*" ${XP_HOME}
        _chown ${XP_HOME}
    else
        echo "XP_HOME exists at ${XP_HOME}"
    fi
}

installConfig()
{
    if [ ! -f ${CONFIG_LOCATION} ]; then
        _cp "${INSTALL_HOME}/service/xp.conf" ${CONFIG_LOCATION}
    else
        echo "Config exists at ${CONFIG_LOCATION}"
    fi
}

initLogDir()
{
    LOGDIR="/var/log/${APP_NAME}";

    _mkdir ${LOGDIR}
    _chown ${LOGDIR}
}

_cp()
{
    sudo cp -R $1 $2

    if (($? > 0)); then
        printf '%s\n' 'cannot copy directory $1 to $2' >&2
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

main() {
    checkDistro;
    addUser
    install
    installService
    createXPHome
    installConfig
    initLogDir
}

main