package com.enonic.wem.portal.internal.content.page;


import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.portal.internal.controller.JsContext;

public class JsPageRendererContext
    extends JsContext
    implements PageRendererContext
{
    private PageDescriptor pageDescriptor;

    @Override
    public PageDescriptor getPageDesriptor()
    {
        return pageDescriptor;
    }

    public void setPageDescriptor( final PageDescriptor pageDescriptor )
    {
        this.pageDescriptor = pageDescriptor;
    }
}
