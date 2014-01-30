package com.enonic.wem.portal.content.page;


import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;

public class PageComponentResolver
{
    public static PageComponent resolve( final ComponentPath componentPath, final PageRegions pageRegions )
    {
        final PageComponent component = pageRegions.getComponent( componentPath );
        if ( component == null )
        {
            throw new PageComponentNotFoundException( componentPath );
        }

        return component;
    }
}
