package com.enonic.wem.admin.json;


import org.joda.time.Instant;

public interface ChangeTraceableJson
{
    public String getCreator();

    public String getModifier();

    public Instant getModifiedTime();

    public Instant getCreatedTime();
}
