package com.enonic.wem.portal.rendering;


import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;

public class PageRegionsResolver
{
    public static PageRegions resolve( final Page page, final PageTemplate template )
    {
        if ( page.hasRegions() )
        {
            return page.getRegions();
        }
        else
        {
            return template.getRegions();
        }
    }
}
