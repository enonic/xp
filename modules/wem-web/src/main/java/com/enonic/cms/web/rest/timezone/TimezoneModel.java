package com.enonic.cms.web.rest.timezone;

public final class TimezoneModel
{
    private String id;
    private String humanizedId;
    private String shortName;
    private String name;
    private String offset;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getHumanizedId()
    {
        return humanizedId;
    }

    public void setHumanizedId( String humanizedId )
    {
        this.humanizedId = humanizedId;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getOffset()
    {
        return offset;
    }

    public void setOffset( String offset )
    {
        this.offset = offset;
    }
}
