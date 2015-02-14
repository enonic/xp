package com.enonic.xp.portal;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentConstants;
import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.content.page.PageDescriptor;
import com.enonic.xp.core.content.page.PageTemplate;
import com.enonic.xp.core.content.page.region.Component;
import com.enonic.xp.core.content.site.Site;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.branch.Branch;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public final class PortalContext
    implements PortalRequest
{
    public final static Branch DEFAULT_BRANCH = ContentConstants.BRANCH_DRAFT;

    private String uri;

    private String method;

    private final Multimap<String, String> params;

    private final Multimap<String, String> formParams;

    private final Multimap<String, String> headers;

    private RenderMode mode;

    private Branch branch;

    private ContentPath contentPath;

    private String baseUri;

    private HttpServletRequest rawRequest;

    private Site site;

    private Content content;

    private PageTemplate pageTemplate;

    private Component component;

    private ModuleKey module;

    private PortalResponse response;

    private PageDescriptor pageDescriptor;

    public PortalContext()
    {
        this.uri = "";
        this.baseUri = "";
        this.contentPath = ContentPath.from( "/" );
        this.mode = RenderMode.LIVE;
        this.branch = DEFAULT_BRANCH;
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
    public Branch getBranch()
    {
        return branch;
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

    public void setBranch( final Branch branch )
    {
        this.branch = branch;
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

    public PortalResponse getResponse()
    {
        return this.response;
    }

    public void setResponse( final PortalResponse response )
    {
        this.response = response;
    }

    public Site getSite()
    {
        return site;
    }

    public void setSite( final Site site )
    {
        this.site = site;
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

    public void setPageTemplate( final PageTemplate pageTemplate )
    {
        this.pageTemplate = pageTemplate;
    }

    public Component getComponent()
    {
        return component;
    }

    public void setComponent( final Component component )
    {
        this.component = component;
    }

    public ModuleKey getModule()
    {
        return this.module;
    }

    public void setModule( final ModuleKey module )
    {
        this.module = module;
    }

    public PageDescriptor getPageDescriptor()
    {
        return pageDescriptor;
    }

    public void setPageDescriptor( final PageDescriptor pageDescriptor )
    {
        this.pageDescriptor = pageDescriptor;
    }

    @Override
    public ContentPath getContentPath()
    {
        return ( this.content != null ) ? this.content.getPath() : this.contentPath;
    }

    @Override
    public HttpServletRequest getRawRequest()
    {
        return this.rawRequest;
    }

    @Override
    public String getBaseUri()
    {
        return this.baseUri;
    }

    public void setContentPath( final ContentPath contentPath )
    {
        this.contentPath = contentPath;
    }

    public void setBaseUri( final String baseUri )
    {
        this.baseUri = baseUri;
    }

    public void setRawRequest( final HttpServletRequest rawRequest )
    {
        this.rawRequest = rawRequest;
    }
}
