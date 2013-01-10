package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

public abstract class Mixin
{
    private final ModuleName moduleName;

    private final String displayName;

    Mixin( final String displayName, final ModuleName moduleName )
    {
        Preconditions.checkNotNull( moduleName, "moduleName is required" );
        this.displayName = displayName;
        this.moduleName = moduleName;
    }

    public abstract String getName();

    public ModuleName getModuleName()
    {
        return moduleName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public QualifiedMixinName getQualifiedName()
    {
        return new QualifiedMixinName( moduleName, getName() );
    }

    public abstract Class getType();

    public abstract FormItem toFormItem( final MixinReference mixinReference );
}
