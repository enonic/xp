package com.enonic.wem.api.content.type.formitem;


import com.enonic.wem.api.module.Module;

public abstract class Template
{
    private Module module;

    Template()
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

    public TemplateQualifiedName getQualifiedName()
    {
        return new TemplateQualifiedName( module.getName(), getName() );
    }

    public abstract Class getType();

    public abstract HierarchicalFormItem create( final TemplateReference templateReference );
}
