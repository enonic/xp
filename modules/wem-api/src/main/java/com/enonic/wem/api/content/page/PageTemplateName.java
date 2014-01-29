package com.enonic.wem.api.content.page;


public class PageTemplateName
    extends TemplateName
{
    public PageTemplateName( final String name )
    {
        super( name );
    }

    public static PageTemplateName from( final String name )
    {
        return new PageTemplateName( name );
    }
}
