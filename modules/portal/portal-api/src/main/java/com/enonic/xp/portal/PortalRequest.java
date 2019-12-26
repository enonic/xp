package com.enonic.xp.portal;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.region.Component;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@PublicApi
public final class PortalRequest
    extends WebRequest
{
    private final static Branch DEFAULT_BRANCH = ContentConstants.BRANCH_DRAFT;

    private final static RepositoryId DEFAULT_REPOSITORY_ID = ContentConstants.CONTENT_REPO_ID;

    private RenderMode mode;

    private RepositoryId repositoryId;

    private Branch branch;

    private ContentPath contentPath;

    private String baseUri;

    private String contextPath;

    private Site site;

    private Content content;

    private PageTemplate pageTemplate;

    private Component component;

    private ApplicationKey applicationKey;

    private PageDescriptor pageDescriptor;

    private ControllerScript controllerScript;

    private Boolean validTicket;

    public PortalRequest()
    {
    }

    {
        this.baseUri = "";
        this.contentPath = ContentPath.from( "/" );
        this.mode = RenderMode.LIVE;
        this.branch = DEFAULT_BRANCH;
        this.repositoryId = DEFAULT_REPOSITORY_ID;
    }

    public PortalRequest( final WebRequest webRequest )
    {
        super(webRequest);
    }

    public Branch getBranch()
    {
        return branch;
    }

    public RenderMode getMode()
    {
        return this.mode;
    }

    public void setMode( final RenderMode mode )
    {
        this.mode = mode;
    }

    public void setBranch( final Branch branch )
    {
        this.branch = branch;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public void setRepositoryId( final RepositoryId repositoryId )
    {
        this.repositoryId = repositoryId;
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

    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath( final String contextPath )
    {
        this.contextPath = contextPath;
    }

    public ControllerScript getControllerScript()
    {
        return controllerScript;
    }

    public void setControllerScript( final ControllerScript controllerScript )
    {
        this.controllerScript = controllerScript;
    }

    public Boolean isValidTicket()
    {
        return validTicket;
    }

    public void setValidTicket( final Boolean validTicket )
    {
        this.validTicket = validTicket;
    }

    public boolean isSiteBase()
    {
        return baseUri.startsWith( "/site" ) || baseUri.startsWith( "/admin/site" );
    }
}
