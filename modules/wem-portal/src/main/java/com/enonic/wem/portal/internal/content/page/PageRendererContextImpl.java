package com.enonic.wem.portal.internal.content.page;


import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;

public class PageRendererContextImpl
    extends PortalContextImpl
    implements PageRendererContext
{
    private PageDescriptor pageDescriptor;

    @Override
    public PageDescriptor getPageDescriptor()
    {
        return pageDescriptor;
    }

    public void setPageDescriptor( final PageDescriptor pageDescriptor )
    {
        this.pageDescriptor = pageDescriptor;
    }
}
