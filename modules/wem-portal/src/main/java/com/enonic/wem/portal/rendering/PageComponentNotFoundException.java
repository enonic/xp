package com.enonic.wem.portal.rendering;


import com.enonic.wem.api.content.page.ComponentName;

public class PageComponentNotFoundException
    extends RuntimeException
{
    public PageComponentNotFoundException( final ComponentName componentName )
    {
        super( "PageComponent not found: " + componentName );
    }
}
