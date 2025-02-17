package com.enonic.xp.portal.impl.handler.render;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

public final class PageResolverResult
{
    private final Page effectivePage;

    private final DescriptorKey controller;

    private final PageDescriptor pageDescriptor;

    private final String error;

    private PageResolverResult( final Page effectivePage, final DescriptorKey controller, final PageDescriptor pageDescriptor,
                                final String error )
    {
        this.effectivePage = effectivePage;
        this.controller = controller;
        this.pageDescriptor = pageDescriptor;
        this.error = error;
    }

    public PageResolverResult( final Page effectivePage, final DescriptorKey controller, final PageDescriptor pageDescriptor )
    {
        this( effectivePage, controller, pageDescriptor, null );
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

    public Page getEffectivePageOrElseThrow( RenderMode mode )
        throws WebException
    {
        if ( this.effectivePage == null )
        {
            throw new WebException( mode == RenderMode.INLINE || mode == RenderMode.EDIT ? HttpStatus.IM_A_TEAPOT : HttpStatus.NOT_FOUND,
                                    error );
        }
        return this.effectivePage;
    }

    public static PageResolverResult errorResult( final String message )
    {
        return new PageResolverResult( null, null, null, message );
    }
}
