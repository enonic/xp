package com.enonic.wem.admin.json;


public interface ChangeTraceableJson
{
    public String getCreator();

    public String getModifier();

    public String getModifiedTime();

    public String getCreatedTime();
}
