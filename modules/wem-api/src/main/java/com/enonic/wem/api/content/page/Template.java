package com.enonic.wem.api.content.page;

import com.enonic.wem.api.module.ModuleResourceKey;

public abstract class Template<ID extends TemplateId>
{
    private final ID id;

    private final String displayName;

    private final ModuleResourceKey descriptor;

    protected Template( final ID id, final String displayName, final ModuleResourceKey descriptor )
    {
        this.id = id;
        this.displayName = displayName;
        this.descriptor = descriptor;
    }

    public ModuleResourceKey getDescriptor()
    {
        return descriptor;
    }

    public ID getId()
    {
        return id;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
