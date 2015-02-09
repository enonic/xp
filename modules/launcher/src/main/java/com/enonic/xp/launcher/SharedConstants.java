package com.enonic.xp.launcher;

import org.apache.felix.framework.util.FelixConstants;

public interface SharedConstants
    extends FelixConstants
{
    public static final String XP_HOME_DIR = "xp.home";

    public static final String XP_INSTALL_DIR = "xp.install";

    public static final String XP_HOME_DIR_ENV = "XP_HOME";

    public static final String DEV_MODE = "xp.dev.mode";

    public static final String DEV_BUNDLE_REFRESH = "xp.dev.bundleRefresh";

    public static final String DEV_GROUP_ID = "xp.dev.groupId";

    public static final String DEV_PROJECT_DIR = "xp.dev.projectDir";

    public static final String INTERNAL_OSGI_BOOT_DELEGATION = "internal.osgi.bootdelegation";

    public static final String INTERNAL_OSGI_SYSTEM_PACKAGES = "internal.osgi.system.packages";

    public static final String XP_OSGI_STARTLEVEL = "xp.osgi.startlevel";

    public static final String XP_OSGI_STARTLEVEL_BUNDLE = "xp.osgi.startlevel.bundle";
}
