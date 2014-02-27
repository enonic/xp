package com.enonic.wem.portal.controller;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutRegions;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

public final class JsContext
{
    private JsHttpRequest request;

    private JsHttpResponse response;

    private Content siteContent;

    private Content content;

    private PageTemplate pageTemplate;

    private PortalUrlScriptBean portalUrlScriptBean;

    private PageComponent component;

    public JsContext()
    {
        this.response = new JsHttpResponse();
    }

    public JsHttpRequest getRequest()
    {
        return this.request;
    }

    public void setRequest( final JsHttpRequest request )
    {
        this.request = request;
    }

    public JsHttpResponse getResponse()
    {
        return this.response;
    }

    public void setResponse( final JsHttpResponse response )
    {
        this.response = response;
    }

    public Content getSiteContent()
    {
        return siteContent;
    }

    public void setSiteContent( final Content siteContent )
    {
        this.siteContent = siteContent;
    }

    public Content getContent()
    {
        return content;
    }

    public void setContent( final Content content )
    {
        this.content = content;
    }

    public PageTemplate getPageTemplate()
    {
        return pageTemplate;
    }

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

    public PortalUrlScriptBean getUrl()
    {
        return this.portalUrlScriptBean;
    }

    public void setPortalUrlScriptBean( final PortalUrlScriptBean portalUrlScriptBean )
    {
        this.portalUrlScriptBean = portalUrlScriptBean;
    }

    public PageComponent getComponent()
    {
        return component;
    }

    public void setComponent( final PageComponent component )
    {
        this.component = component;
    }
}
