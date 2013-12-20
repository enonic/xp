package com.enonic.wem.admin.json;


import org.joda.time.DateTime;

public interface ChangeTraceableJson
{
    public String getCreator();

    public String getModifier();

    public DateTime getModifiedTime();

    public DateTime getCreatedTime();
}
