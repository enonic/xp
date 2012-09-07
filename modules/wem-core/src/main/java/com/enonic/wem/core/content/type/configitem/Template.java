package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.module.Module;

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

    public abstract TemplateType getType();

    public abstract DirectAccessibleFormItem create( final TemplateReference templateReference );
}
