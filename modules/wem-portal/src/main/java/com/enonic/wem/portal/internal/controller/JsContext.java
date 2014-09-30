package com.enonic.wem.portal.internal.controller;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutRegions;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.url.PortalUrlBuilders;

public final class JsContext
    implements PortalContext
{
    private JsHttpRequest request;

    private JsHttpResponse response;

    private Content siteContent;

    private Content content;

    private PageTemplate pageTemplate;

    private PageComponent component;

    private String resolvedModule;

    public JsContext()
    {
        this.response = new JsHttpResponse();
    }

    @Override
    public PortalRequest getRequest()
    {
        return this.request;
    }

    public void setRequest( final JsHttpRequest request )
    {
        this.request = request;
    }

    @Override
    public PortalResponse getResponse()
    {
        return this.response;
    }

    public void setResponse( final JsHttpResponse response )
    {
        this.response = response;
    }

    @Override
    public Content getSiteContent()
    {
        return siteContent;
    }

    public void setSiteContent( final Content siteContent )
    {
        this.siteContent = siteContent;
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
    public PageRegions getPageRegions()
    {
        if ( this.content == null )
        {
            return null;
        }

        final Page page = this.content.getPage();
        if ( ( page != null ) && page.hasRegions() )
        {
            return page.getRegions();
        }
        else
        {
            return this.pageTemplate.getRegions();
        }
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
    public String getResolvedModule()
    {
        return this.resolvedModule;
    }

    public void setResolvedModule( final String resolvedModule )
    {
        this.resolvedModule = resolvedModule;
    }
}
