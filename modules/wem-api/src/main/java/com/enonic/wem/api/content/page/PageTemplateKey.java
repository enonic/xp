package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterators.toArray;

public final class PageTemplateKey
{
    protected static final String SEPARATOR = "|";

    private final PageTemplateName name;

    private final ModuleName module;

    private final String refString;

    private PageTemplateKey( final ModuleName moduleName, final PageTemplateName templateName )
    {
        checkNotNull( templateName, "PageTemplateName name cannot be null" );
        checkNotNull( moduleName, "ModuleName name cannot be null" );
        this.name = templateName;
        this.module = moduleName;
        this.refString = module.toString() + SEPARATOR + name.toString();
    }

    public PageTemplateName getTemplateName()
    {
        return name;
    }

    public ModuleName getModuleName()
    {
        return module;
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

        final PageTemplateKey that = (PageTemplateKey) o;
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

    public static PageTemplateKey from( final ModuleName moduleKey, final PageTemplateName templateName )
    {
        return new PageTemplateKey( moduleKey, templateName );
    }

    public static PageTemplateKey from( final String templateKey )
    {
        Preconditions.checkNotNull( templateKey, "templateKey cannot be null" );
        final String[] templateKeyParts = toArray( on( SEPARATOR ).split( templateKey ).iterator(), String.class );
        Preconditions.checkArgument( templateKeyParts.length == 2, "Invalid PageTemplateKey" );

        final ModuleName moduleName = ModuleName.from( templateKeyParts[0] );
        final PageTemplateName templateName = new PageTemplateName( templateKeyParts[1] );

        return new PageTemplateKey( moduleName, templateName );
    }
}
