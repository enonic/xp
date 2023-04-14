package com.enonic.xp.portal.impl.handler.render;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;

public final class PageResolverResult
{
    private final Page effectivePage;

    private final DescriptorKey controller;

    private final PageDescriptor pageDescriptor;

    public PageResolverResult( final Page effectivePage, final DescriptorKey controller, final PageDescriptor pageDescriptor )
    {
        this.effectivePage = effectivePage;
        this.controller = controller;
        this.pageDescriptor = pageDescriptor;
    }

    public Page getEffectivePage()
    {
        return effectivePage;
    }

    public DescriptorKey getController()
    {
        return controller;
    }

    public ApplicationKey getApplicationKey()
    {
        return this.pageDescriptor == null ? null : pageDescriptor.getApplicationKey();
    }

    public PageDescriptor getPageDescriptor()
    {
        return pageDescriptor;
    }
}
