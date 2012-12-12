package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

public abstract class SubType
{
    private final ModuleName moduleName;

    private final String displayName;

    SubType( final String displayName, final ModuleName moduleName )
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

    public QualifiedSubTypeName getQualifiedName()
    {
        return new QualifiedSubTypeName( moduleName, getName() );
    }

    public abstract Class getType();

    public abstract FormItem toFormItem( final SubTypeReference subTypeReference );
}
