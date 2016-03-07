package com.enonic.xp.core.impl.security;

import com.enonic.xp.data.PropertyPath;

public class PathGuardPropertyPaths
{
    public static final PropertyPath DISPLAY_NAME_PATH = PropertyPath.from( "displayName" );

    public static final PropertyPath DESCRIPTION_PATH = PropertyPath.from( "description" );

    public static final PropertyPath AUTH_CONFIG_PATH = PropertyPath.from( "authConfig" );

    public static final PropertyPath AUTH_CONFIG_APPLICATION_PATH = PropertyPath.from( AUTH_CONFIG_PATH, "applicationKey" );

    public static final PropertyPath AUTH_CONFIG_FORM_PATH = PropertyPath.from( AUTH_CONFIG_PATH, "config" );

    public static final PropertyPath PATHS_PATH = PropertyPath.from( "paths" );
}
