package com.enonic.wem.api;

public final class Version
{
    private final static String BANNER = "" +
        " _____ _____ _____ _____ _____ _____    _ _ _ _____ _____ \n" +
        "|   __|   | |     |   | |     |     |  | | | |   __|     |\n" +
        "|   __| | | |  |  | | | |-   -|   --|  | | | |   __| | | |\n" +
        "|_____|_|___|_____|_|___|_____|_____|  |_____|_____|_|_|_|\n";

    private final static Version INSTANCE = new Version();

    private final String version;

    private Version()
    {
        final String value = getClass().getPackage().getImplementationVersion();
        this.version = value != null ? value : "x.x.x";
    }

    public String getName()
    {
        return "Enonic WEM";
    }

    public String getCopyright()
    {
        return "Copyright (c) 2000-2013 Enonic AS";
    }

    public String getVersion()
    {
        return this.version;
    }

    public String getBanner()
    {
        return BANNER;
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
}
