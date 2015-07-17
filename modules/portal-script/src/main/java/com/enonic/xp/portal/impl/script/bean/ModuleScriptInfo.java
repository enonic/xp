package com.enonic.xp.portal.impl.script.bean;

import com.enonic.xp.resource.ResourceKey;

public final class ModuleScriptInfo
{
    private final ResourceKey key;

    private Object exports;

    public ModuleScriptInfo( final ResourceKey key )
    {
        this.key = key;
    }

    public String getId()
    {
        return this.key.toString();
    }

    public String getName()
    {
        return this.key.getApplicationKey().toString();
    }

    public Object getExports()
    {
        return exports;
    }

    public void setExports( final Object exports )
    {
        this.exports = exports;
    }
}
