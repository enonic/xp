package com.enonic.wem.portal.internal.content.page;


import com.enonic.wem.api.content.page.ComponentPath;

public class PageComponentNotFoundException
    extends RuntimeException
{
    public PageComponentNotFoundException( final ComponentPath componentPath )
    {
        super( "PageComponent not found: " + componentPath );
    }
}
