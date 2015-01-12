package com.enonic.wem.portal.internal.controller;

import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.region.Component;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public final class PortalContextImpl
    implements PortalContext
{
    public final static Workspace DEFAULT_WORKSPACE = ContentConstants.WORKSPACE_STAGE;

    private String uri;

    private String method;

    private final Multimap<String, String> params;

    private final Multimap<String, String> formParams;

    private final Multimap<String, String> headers;

    private RenderMode mode;

    private Workspace workspace;

    private Site site;

    private Content content;

    private PageTemplate pageTemplate;

    private Component component;

    private ModuleKey module;

    private PortalResponse response;

    private PageDescriptor pageDescriptor;

    public PortalContextImpl()
    {
        this.uri = "";
        this.mode = RenderMode.LIVE;
        this.workspace = DEFAULT_WORKSPACE;
        this.params = HashMultimap.create();
        this.formParams = HashMultimap.create();
        this.headers = HashMultimap.create();
        this.response = new PortalResponse();
    }

    @Override
    public String getUri()
    {
        return this.uri;
    }

    @Override
    public String getMethod()
    {
        return this.method;
    }

    @Override
    public Workspace getWorkspace()
    {
        return workspace;
    }

    @Override
    public Multimap<String, String> getParams()
    {
        return this.params;
    }

    @Override
    public Multimap<String, String> getFormParams()
    {
        return this.formParams;
    }

    @Override
    public RenderMode getMode()
    {
        return this.mode;
    }

    public void setUri( final String uri )
    {
        this.uri = uri;
    }

    public void setMethod( final String method )
    {
        this.method = method;
    }

    public void setMode( final RenderMode mode )
    {
        this.mode = mode;
    }

    public void setMode( final String mode )
    {
        setMode( RenderMode.from( mode, RenderMode.LIVE ) );
    }

    public void setWorkspace( final Workspace workspace )
    {
        this.workspace = workspace;
    }

    public void setWorkspace( final String workspace )
    {
        setWorkspace( Workspace.from( workspace ) );
    }

    public void addParam( final String name, final String value )
    {
        this.params.put( name, value );
    }

    public void addParams( final Multimap<String, String> params )
    {
        this.params.putAll( params );
    }

    public void addParams( final Map<String, List<String>> params )
    {
        for ( final Map.Entry<String, List<String>> entry : params.entrySet() )
        {
            this.params.putAll( entry.getKey(), entry.getValue() );
        }
    }

    public void addFormParam( final String name, final String value )
    {
        this.formParams.put( name, value );
    }

    public void addFormParams( final Multimap<String, String> params )
    {
        this.formParams.putAll( params );
    }

    public void addFormParams( final Map<String, List<String>> params )
    {
        for ( final Map.Entry<String, List<String>> entry : params.entrySet() )
        {
            this.formParams.putAll( entry.getKey(), entry.getValue() );
        }
    }

    public void addHeader( final String name, final String value )
    {
        this.headers.put( name, value );
    }

    public void addHeaders( final Multimap<String, String> params )
    {
        this.headers.putAll( params );
    }

    public void addHeaders( final Map<String, List<String>> params )
    {
        for ( final Map.Entry<String, List<String>> entry : params.entrySet() )
        {
            this.headers.putAll( entry.getKey(), entry.getValue() );
        }
    }

    @Override
    public Multimap<String, String> getHeaders()
    {
        return this.headers;
    }

    @Override
    public String rewriteUri( final String uri )
    {
        return ServletRequestUrlHelper.rewriteUri( uri );
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
    public Component getComponent()
    {
        return component;
    }

    @Override
    public void setComponent( final Component component )
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
    public PageDescriptor getPageDescriptor()
    {
        return pageDescriptor;
    }

    public void setPageDescriptor( final PageDescriptor pageDescriptor )
    {
        this.pageDescriptor = pageDescriptor;
    }
}
