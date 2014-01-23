package com.enonic.wem.portal.rendering;


import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;

public class PageComponentResolver
{
    public static PageComponent resolve( final ComponentName componentName, final PageRegions pageRegions )
    {
        final PageComponent component = pageRegions.getComponent( componentName );
        if ( component == null )
        {
            throw new PageComponentNotFoundException( componentName );
        }

        return component;
    }
}
