package com.enonic.xp.portal.impl.handler.render;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;

public final class PageResolverResult
{
    private final Page effectivePage;

    private final DescriptorKey controller;

    public PageResolverResult( final Page effectivePage, final DescriptorKey controller )
    {
        this.effectivePage = effectivePage;
        this.controller = controller;
    }

    public Page getEffectivePage()
    {
        return effectivePage;
    }

    public DescriptorKey getController()
    {
        return controller;
    }
}
