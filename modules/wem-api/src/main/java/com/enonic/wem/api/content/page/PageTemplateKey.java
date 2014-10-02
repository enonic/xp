package com.enonic.wem.api.content.page;


import static com.google.common.base.Preconditions.checkNotNull;

public final class PageTemplateKey
{
    private final PageTemplateName name;

    private final String refString;

    private PageTemplateKey( final PageTemplateName templateName )
    {
        checkNotNull( templateName, "PageTemplateName name cannot be null" );
        this.name = templateName;
        this.refString = name.toString();
    }

    public PageTemplateName getTemplateName()
    {
        return name;
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

    public static PageTemplateKey from( final PageTemplateName templateName )
    {
        return new PageTemplateKey( templateName );
    }

    public static PageTemplateKey from( final String templateKey )
    {
        checkNotNull( templateKey, "templateKey cannot be null" );
        final PageTemplateName templateName = new PageTemplateName( templateKey );
        return new PageTemplateKey( templateName );
    }
}
