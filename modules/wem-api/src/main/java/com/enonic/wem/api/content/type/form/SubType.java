package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

public abstract class SubType
{
    private final ModuleName moduleName;

    SubType( final ModuleName moduleName )
    {
        Preconditions.checkNotNull( moduleName, "moduleName is required" );
        this.moduleName = moduleName;
    }

    public abstract String getName();

    public ModuleName getModuleName()
    {
        return moduleName;
    }

    public QualifiedSubTypeName getQualifiedName()
    {
        return new QualifiedSubTypeName( moduleName, getName() );
    }

    public abstract Class getType();

    public abstract FormItem create( final SubTypeReference subTypeReference );
}
