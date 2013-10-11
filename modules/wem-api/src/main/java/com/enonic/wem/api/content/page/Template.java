package com.enonic.wem.api.content.page;

import com.enonic.wem.api.module.ModuleResourceKey;

public abstract class Template<T extends Template, ID extends TemplateId>
{
    ID id;

    String displayName;

    ModuleResourceKey descriptor;

    public ModuleResourceKey getDescriptor()
    {
        return descriptor;
    }
}
