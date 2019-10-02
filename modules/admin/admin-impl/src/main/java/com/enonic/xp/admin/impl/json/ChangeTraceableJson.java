package com.enonic.xp.admin.impl.json;


import java.time.Instant;

public interface ChangeTraceableJson
{
    String getCreator();

    String getModifier();

    Instant getModifiedTime();

    Instant getCreatedTime();
}
