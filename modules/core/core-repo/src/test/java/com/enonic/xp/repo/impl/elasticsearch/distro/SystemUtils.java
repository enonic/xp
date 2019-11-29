package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.util.Objects;

public class SystemUtils
{
    private static final String OS_NAME = Objects.requireNonNullElse( System.getProperty( "os.name" ), "" );

    public static final boolean IS_OS_WINDOWS = OS_NAME.startsWith( "Windows" );

    public static final boolean IS_OS_MAC = OS_NAME.startsWith( "Mac" );

    private SystemUtils()
    {
    }
}
