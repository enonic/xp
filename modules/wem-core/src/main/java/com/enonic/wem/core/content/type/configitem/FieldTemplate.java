package com.enonic.wem.core.content.type.configitem;

import com.enonic.wem.core.module.Module;

public class FieldTemplate
    implements Template
{
    private String name;

    private Module module;

    private Field field;

    FieldTemplate()
    {
    }

    public String getName()
    {
        return name;
    }

    void setName( final String name )
    {
        this.name = name;
    }

    public Module getModule()
    {
        return module;
    }

    void setModule( final Module module )
    {
        this.module = module;

    }

    public TemplateQualifiedName getTemplateQualifiedName()
    {
        return new TemplateQualifiedName( module.getName(), name );
    }

    public Field getField()
    {
        return field;
    }

    public void setField( final Field value )
    {
        this.field = value;
    }

    public ConfigItem create( final TemplateReference templateReference )
    {
        return field.copy( templateReference.getName() );
    }
}
