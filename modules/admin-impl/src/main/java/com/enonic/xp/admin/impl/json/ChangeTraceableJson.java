package com.enonic.xp.admin.impl.json;


import java.time.Instant;

public interface ChangeTraceableJson
{
    public String getCreator();

    public String getModifier();

    public Instant getModifiedTime();

    public Instant getCreatedTime();
}
