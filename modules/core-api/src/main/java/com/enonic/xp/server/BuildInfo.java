package com.enonic.xp.server;

public interface BuildInfo
{
    public String getHash();

    public String getShortHash();

    public String getTimestamp();

    public String getBranch();
}
