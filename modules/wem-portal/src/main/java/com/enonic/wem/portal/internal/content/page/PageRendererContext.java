package com.enonic.wem.portal.internal.content.page;


import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.portal.PortalContext;

public interface PageRendererContext
    extends PortalContext
{
    PageDescriptor getPageDesriptor();
}
