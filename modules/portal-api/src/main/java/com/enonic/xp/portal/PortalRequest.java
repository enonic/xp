package com.enonic.xp.portal;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.page.PageDescriptor;
import com.enonic.xp.content.page.PageTemplate;
import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Beta
public final class PortalRequest
{
    public final static Branch DEFAULT_BRANCH = ContentConstants.BRANCH_DRAFT;

    private String uri;

    private String method;

    private final Multimap<String, String> params;

    private final Multimap<String, String> formParams;

    private final Multimap<String, String> headers;

    private final Map<String, String> cookies;

    private RenderMode mode;

    private Branch branch;

    private ContentPath contentPath;

    private String baseUri;

    private Site site;

    private Content content;

    private PageTemplate pageTemplate;

    private Component component;

    private ModuleKey module;

    private PortalResponse response;

    private PageDescriptor pageDescriptor;

    public PortalRequest()
    {
        this.uri = "";
        this.baseUri = "";
        this.contentPath = ContentPath.from( "/" );
        this.mode = RenderMode.LIVE;
        this.branch = DEFAULT_BRANCH;
        this.params = HashMultimap.create();
        this.formParams = HashMultimap.create();
        this.headers = HashMultimap.create();
        this.cookies = Maps.newHashMap();
        this.response = new PortalResponse();
    }

    public String getUri()
    {
        return this.uri;
    }

    public String getMethod()
    {
        return this.method;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public Multimap<String, String> getParams()
    {
        return this.params;
    }

    public Multimap<String, String> getFormParams()
    {
        return this.formParams;
    }

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

    public Multimap<String, String> getHeaders()
    {
        return this.headers;
    }

    public String rewriteUri( final String uri )
    {
        return ServletRequestUrlHelper.rewriteUri( uri );
    }

    @Deprecated
    public PortalResponse getResponse()
    {
        return this.response;
    }

    @Deprecated
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

    public ContentPath getContentPath()
    {
        return ( this.content != null ) ? this.content.getPath() : this.contentPath;
    }

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

    public Map<String, String> getCookies()
    {
        return this.cookies;
    }
}
