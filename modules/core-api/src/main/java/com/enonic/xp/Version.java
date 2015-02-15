package com.enonic.xp;

import com.google.common.base.Strings;

public final class Version
{
    private final static Version INSTANCE = new Version();

    private String version;

    private Version()
    {
        final String value = getClass().getPackage().getImplementationVersion();
        final String overrideValue = System.getProperty( "xp.version" );
        this.version = Strings.isNullOrEmpty( overrideValue ) ? value : overrideValue;
    }

    public String getName()
    {
        return "Enonic XP";
    }

    public String getCopyright()
    {
        return "Copyright (c) 2000-2015 Enonic AS";
    }

    public String getVersion()
    {
        return this.version;
    }

    public void setVersion( final String version )
    {
        this.version = version;
    }

    public String getNameAndVersion()
    {
        return getName() + " " + getVersion();
    }

    @Override
    public String toString()
    {
        return getNameAndVersion();
    }

    public static Version get()
    {
        return INSTANCE;
    }

    public static void main( final String... args )
    {
        System.out.println( Version.get().getNameAndVersion() );
    }
}
