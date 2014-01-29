package com.enonic.wem.api.content.page;

import com.enonic.wem.api.module.ModuleName;

import static com.google.common.base.Preconditions.checkNotNull;


public abstract class TemplateKey<NAME extends TemplateName>
{
    public enum TemplateType
    {
        IMAGE,
        LAYOUT,
        PAGE,
        PART
    }

    protected static final String SEPARATOR = "|";

    private final NAME name;

    private final ModuleName module;

    private final String refString;

    private final TemplateType templateType;

    protected TemplateKey( final ModuleName module, final NAME name, final TemplateType templateType )
    {
        checkNotNull( name, "Template name cannot be null" );
        checkNotNull( module, "ModuleKey name cannot be null" );
        this.name = name;
        this.module = module;
        this.templateType = templateType;
        this.refString = module.toString() + SEPARATOR + name.toString();
    }

    public NAME getTemplateName()
    {
        return name;
    }

    public ModuleName getModuleName()
    {
        return module;
    }

    public TemplateType getTemplateType()
    {
        return templateType;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final TemplateKey that = (TemplateKey) o;
        if ( !refString.equals( that.refString ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }
}
