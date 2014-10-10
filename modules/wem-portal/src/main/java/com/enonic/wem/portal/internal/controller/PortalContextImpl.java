package com.enonic.wem.portal.internal.controller;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutRegions;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.portal.url.PortalUrlBuilders;

public class PortalContextImpl
    implements PortalContext
{
    private PortalRequestImpl request;

    private PortalResponseImpl response;

    private RenderingMode mode;

    private Site site;

    private Content content;

    private PageTemplate pageTemplate;

    private PageComponent component;

    private Module module;

    public PortalContextImpl()
    {
        this.response = new PortalResponseImpl();
    }

    @Override
    public PortalRequest getRequest()
    {
        return this.request;
    }

    public void setRequest( final PortalRequestImpl request )
    {
        this.request = request;
    }

    @Override
    public PortalResponse getResponse()
    {
        return this.response;
    }

    public void setResponse( final PortalResponseImpl response )
    {
        this.response = response;
    }

    @Override
    public RenderingMode getMode()
    {
        return mode;
    }

    public void setMode( final RenderingMode mode )
    {
        this.mode = mode;
    }

    @Override
    public Site getSite()
    {
        return site;
    }

    public void setSite( final Site site )
    {
        this.site = site;
    }

    @Override
    public Content getContent()
    {
        return content;
    }

    public void setContent( final Content content )
    {
        this.content = content;
    }

    @Override
    public PageTemplate getPageTemplate()
    {
        return pageTemplate;
    }

    @Override
    public LayoutRegions getLayoutRegions()
    {
        if ( this.component == null )
        {
            return null;
        }

        if ( !( this.component instanceof LayoutComponent ) )
        {
            return null;
        }

        LayoutComponent layoutComponent = (LayoutComponent) this.component;

        if ( layoutComponent.hasRegions() )
        {
            return layoutComponent.getRegions();
        }
        else
        {
            final PageComponent possibleLayoutComponent = this.pageTemplate.getRegions().getComponent( this.component.getPath() );
            if ( possibleLayoutComponent == null )
            {
                return null;
            }

            if ( !( possibleLayoutComponent instanceof LayoutComponent ) )
            {
                return null;
            }
            layoutComponent = (LayoutComponent) possibleLayoutComponent;
            return layoutComponent.getRegions();
        }
    }

    public void setPageTemplate( final PageTemplate pageTemplate )
    {
        this.pageTemplate = pageTemplate;
    }

    public PortalUrlBuilders getUrl()
    {
        return new PortalUrlBuilders( this );
    }

    @Override
    public PageComponent getComponent()
    {
        return component;
    }

    @Override
    public void setComponent( final PageComponent component )
    {
        this.component = component;
    }

    @Override
    public Module getModule()
    {
        return this.module;
    }

    public void setModule( final Module module )
    {
        this.module = module;
    }
}
