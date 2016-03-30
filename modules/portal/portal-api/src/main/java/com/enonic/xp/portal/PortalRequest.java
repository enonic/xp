package com.enonic.xp.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.region.Component;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Beta
public final class PortalRequest
{
    private final static Branch DEFAULT_BRANCH = ContentConstants.BRANCH_DRAFT;

    private HttpMethod method;

    private final Multimap<String, String> params;

    private final Map<String, String> headers;

    private final Map<String, String> cookies;

    private String scheme;

    private String host;

    private int port;

    private String path;

    private String url;

    private RenderMode mode;

    private Branch branch;

    private ContentPath contentPath;

    private String baseUri;

    private Site site;

    private Content content;

    private PageTemplate pageTemplate;

    private Component component;

    private ApplicationKey applicationKey;

    private PageDescriptor pageDescriptor;

    private ControllerScript controllerScript;

    private String endpointPath;

    private String contentType;

    private Object body;

    private HttpServletRequest rawRequest;

    private boolean webSocket;

    public PortalRequest()
    {
        this.baseUri = "";
        this.contentPath = ContentPath.from( "/" );
        this.mode = RenderMode.LIVE;
        this.branch = DEFAULT_BRANCH;
        this.params = HashMultimap.create();
        this.headers = Maps.newHashMap();
        this.cookies = Maps.newHashMap();
        this.webSocket = false;
    }

    public HttpMethod getMethod()
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

    public String getScheme()
    {
        return scheme;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getPath()
    {
        return path;
    }

    public String getUrl()
    {
        return url;
    }

    public RenderMode getMode()
    {
        return this.mode;
    }

    public void setMethod( final HttpMethod method )
    {
        this.method = method;
    }

    public void setScheme( final String scheme )
    {
        this.scheme = scheme;
    }

    public void setHost( final String host )
    {
        this.host = host;
    }

    public void setPort( final int port )
    {
        this.port = port;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public void setUrl( final String url )
    {
        this.url = url;
    }

    public void setMode( final RenderMode mode )
    {
        this.mode = mode;
    }

    public void setBranch( final Branch branch )
    {
        this.branch = branch;
    }

    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    public String rewriteUri( final String uri )
    {
        return ServletRequestUrlHelper.rewriteUri( uri ).getRewrittenUri();
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

    public ApplicationKey getApplicationKey()
    {
        return this.applicationKey;
    }

    public void setApplicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
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

    public String getEndpointPath()
    {
        return this.endpointPath;
    }

    public void setEndpointPath( final String endpointPath )
    {
        this.endpointPath = Strings.emptyToNull( endpointPath );
    }

    public String getContentType()
    {
        return this.contentType;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    public Object getBody()
    {
        return this.body;
    }

    public void setBody( final Object body )
    {
        this.body = body;
    }

    public String getBodyAsString()
    {
        return this.body != null ? this.body.toString() : null;
    }

    public HttpServletRequest getRawRequest()
    {
        return rawRequest;
    }

    public void setRawRequest( final HttpServletRequest rawRequest )
    {
        this.rawRequest = rawRequest;
    }

    public boolean isWebSocket()
    {
        return this.webSocket;
    }

    public void setWebSocket( final boolean webSocket )
    {
        this.webSocket = webSocket;
    }

    public ControllerScript getControllerScript()
    {
        return controllerScript;
    }

    public void setControllerScript( final ControllerScript controllerScript )
    {
        this.controllerScript = controllerScript;
    }
}
