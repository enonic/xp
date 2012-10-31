package com.enonic.wem.api.content.type.component;


import com.enonic.wem.api.module.Module;

public abstract class SubType
{
    private Module module;

    SubType()
    {

    }

    public abstract String getName();

    public Module getModule()
    {
        return module;
    }

    public void setModule( final Module module )
    {
        this.module = module;
    }

    public SubTypeQualifiedName getQualifiedName()
    {
        return new SubTypeQualifiedName( module.getName(), getName() );
    }

    public abstract Class getType();

    public abstract HierarchicalComponent create( final SubTypeReference subTypeReference );
}
