package com.enonic.gradle;

public final class BuildInfo
{
    private final static String NA = "N/A";

    private String branch = NA;

    private String hash = NA;

    private String shortHash = NA;

    private String timestamp = NA;

    public String getBranch()
    {
        return branch;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash( final String hash )
    {
        this.hash = hash;
    }

    public String getShortHash()
    {
        return shortHash;
    }

    public void setShortHash( final String shortHash )
    {
        this.shortHash = shortHash;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( final String timestamp )
    {
        this.timestamp = timestamp;
    }
}
