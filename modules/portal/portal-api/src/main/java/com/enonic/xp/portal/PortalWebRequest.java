package com.enonic.xp.portal;

import com.google.common.annotations.Beta;

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
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebRequestImpl;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Beta
public final class PortalWebRequest
    extends WebRequestImpl
{
    private final static Branch DEFAULT_BRANCH = ContentConstants.BRANCH_DRAFT;

    private String baseUri;

    private RenderMode mode;

    private Branch branch;

    private ContentPath contentPath;

    private Site site;

    private Content content;

    private PageTemplate pageTemplate;

    private Component component;

    private ApplicationKey applicationKey;

    private PageDescriptor pageDescriptor;

    private ControllerScript controllerScript;

    private PortalWebRequest( final Builder builder )
    {
        super( builder );
        baseUri = builder.baseUri;
        mode = builder.mode;
        branch = builder.branch;
        contentPath = builder.contentPath;
        site = builder.site;
        content = builder.content;
        pageTemplate = builder.pageTemplate;
        component = builder.component;
        applicationKey = builder.applicationKey;
        pageDescriptor = builder.pageDescriptor;
        controllerScript = builder.controllerScript;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final WebRequest webRequest )
    {
        return new Builder( webRequest );
    }

    public String getBaseUri()
    {
        return baseUri;
    }

    public RenderMode getMode()
    {
        return mode;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public ContentPath getContentPath()
    {
        return this.content != null ? this.content.getPath() : this.contentPath;
    }

    public Site getSite()
    {
        return site;
    }

    public Content getContent()
    {
        return content;
    }

    public PageTemplate getPageTemplate()
    {
        return pageTemplate;
    }

    public Component getComponent()
    {
        return component;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public PageDescriptor getPageDescriptor()
    {
        return pageDescriptor;
    }

    public ControllerScript getControllerScript()
    {
        return controllerScript;
    }

    public String rewriteUri( final String uri )
    {
        return ServletRequestUrlHelper.rewriteUri( uri ).getRewrittenUri();
    }


    public static final class Builder
        extends WebRequestImpl.Builder<Builder>
    {
        private String baseUri = "";

        private RenderMode mode = RenderMode.LIVE;

        private Branch branch = DEFAULT_BRANCH;

        private ContentPath contentPath = ContentPath.from( "/" );

        private Site site;

        private Content content;

        private PageTemplate pageTemplate;

        private Component component;

        private ApplicationKey applicationKey;

        private PageDescriptor pageDescriptor;

        private ControllerScript controllerScript;

        private Builder()
        {
        }

        public Builder( final WebRequest webRequest )
        {
            super( webRequest );
            if ( webRequest instanceof PortalWebRequest )
            {
                PortalWebRequest portalWebRequest = (PortalWebRequest) webRequest;
                baseUri = portalWebRequest.baseUri;
                mode = portalWebRequest.mode;
                branch = portalWebRequest.branch;
                contentPath = portalWebRequest.contentPath;
                site = portalWebRequest.site;
                content = portalWebRequest.content;
                pageTemplate = portalWebRequest.pageTemplate;
                component = portalWebRequest.component;
                applicationKey = portalWebRequest.applicationKey;
                pageDescriptor = portalWebRequest.pageDescriptor;
                controllerScript = portalWebRequest.controllerScript;
            }
        }

        public Builder baseUri( final String baseUri )
        {
            this.baseUri = baseUri;
            return this;
        }

        public Builder mode( final RenderMode mode )
        {
            this.mode = mode;
            return this;
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder contentPath( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public Builder site( final Site site )
        {
            this.site = site;
            return this;
        }

        public Builder content( final Content content )
        {
            this.content = content;
            return this;
        }

        public Builder pageTemplate( final PageTemplate pageTemplate )
        {
            this.pageTemplate = pageTemplate;
            return this;
        }

        public Builder component( final Component component )
        {
            this.component = component;
            return this;
        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder pageDescriptor( final PageDescriptor pageDescriptor )
        {
            this.pageDescriptor = pageDescriptor;
            return this;
        }

        public Builder controllerScript( final ControllerScript controllerScript )
        {
            this.controllerScript = controllerScript;
            return this;
        }

        public PortalWebRequest build()
        {
            return new PortalWebRequest( this );
        }
    }
}
