package com.enonic.xp.launcher.impl;

import org.apache.felix.framework.util.FelixConstants;

public interface SharedConstants
    extends FelixConstants
{
    String XP_HOME_DIR = "xp.home";

    String XP_INSTALL_DIR = "xp.install";

    String XP_HOME_DIR_ENV = "XP_HOME";

    String XP_RUN_MODE = "xp.runMode";

    String INTERNAL_OSGI_BOOT_DELEGATION = "internal.osgi.bootdelegation";

    String INTERNAL_OSGI_SYSTEM_PACKAGES = "internal.osgi.system.packages";

    String XP_OSGI_STARTLEVEL = "xp.osgi.startlevel";

    String XP_OSGI_STARTLEVEL_BUNDLE = "xp.osgi.startlevel.bundle";
}
