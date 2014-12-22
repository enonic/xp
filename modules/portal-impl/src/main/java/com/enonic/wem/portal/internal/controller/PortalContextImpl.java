package com.enonic.wem.portal.internal.controller;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;

public final class PortalContextImpl
    implements PortalContext, PortalRequest
{
    private PortalRequest request;

    private PortalResponse response;

    private Site site;

    private Content content;

    private PageTemplate pageTemplate;

    private PageComponent component;

    private ModuleKey module;

    public PortalContextImpl()
    {
        this.response = new PortalResponse();
    }

    @Override
    public PortalRequest getRequest()
    {
        return this.request;
    }

    public void setRequest( final PortalRequest request )
    {
        this.request = request;
    }

    @Override
    public PortalResponse getResponse()
    {
        return this.response;
    }

    public void setResponse( final PortalResponse response )
    {
        this.response = response;
    }

    @Override
    public RenderMode getMode()
    {
        return this.request.getMode();
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

    public void setPageTemplate( final PageTemplate pageTemplate )
    {
        this.pageTemplate = pageTemplate;
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
    public ModuleKey getModule()
    {
        return this.module;
    }

    public void setModule( final ModuleKey module )
    {
        this.module = module;
    }

    @Override
    public String getMethod()
    {
        return this.request.getMethod();
    }

    @Override
    public Workspace getWorkspace()
    {
        return this.request.getWorkspace();
    }

    @Override
    public Multimap<String, String> getParams()
    {
        return this.request.getParams();
    }

    @Override
    public String getBaseUri()
    {
        return this.request.getBaseUri();
    }

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

    @Override
    public Multimap<String, String> getHeaders()
    {
        return this.request.getHeaders();
    }
}
