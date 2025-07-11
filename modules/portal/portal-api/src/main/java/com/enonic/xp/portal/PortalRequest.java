package com.enonic.xp.portal;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.project.Project;
import com.enonic.xp.region.Component;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebRequest;

@PublicApi
public final class PortalRequest
    extends WebRequest
{
    private RenderMode mode = RenderMode.LIVE;

    private RepositoryId repositoryId;

    private Branch branch;

    private ContentPath contentPath;

    private String baseUri;

    private String contextPath;

    private Site site;

    private Content content;

    private Project project;

    private Component component;

    private ApplicationKey applicationKey;

    private PageDescriptor pageDescriptor;

    private ControllerScript controllerScript;

    private Boolean validTicket;

    public PortalRequest()
    {
    }

    public PortalRequest( final WebRequest webRequest )
    {
        super( webRequest );
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

    public Site getSite()
    {
        return site;
    }

    public void setSite( final Site site )
    {
        this.site = site;
    }

    public Project getProject()
    {
        return project;
    }

    public void setProject( final Project project )
    {
        this.project = project;
    }

    public Content getContent()
    {
        return content;
    }

    public void setContent( final Content content )
    {
        this.content = content;
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
        return contentPath;
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
}
