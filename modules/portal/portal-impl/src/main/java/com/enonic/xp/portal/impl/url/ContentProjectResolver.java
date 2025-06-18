package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.RepositoryId;

final class ContentProjectResolver
{
    private final String projectName;

    private final boolean preferSiteRequest;

    private ContentProjectResolver( final Builder builder )
    {
        this.projectName = builder.projectName;
        this.preferSiteRequest = Objects.requireNonNullElse( builder.preferSiteRequest, true );
    }

    public ProjectName resolve()
    {
        if ( projectName != null )
        {
            return ProjectName.from( projectName );
        }

        final PortalRequest portalRequest = PortalRequestAccessor.get();
        if ( preferSiteRequest && PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            return ProjectName.from( portalRequest.getRepositoryId() );
        }

        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        if ( repositoryId == null )
        {
            throw new IllegalArgumentException( "RepositoryId must be set" );
        }

        if ( !repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) )
        {
            throw new IllegalArgumentException( String.format( "RepositoryId is not a content repository: %s", repositoryId ) );
        }

        return ProjectName.from( repositoryId );
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private String projectName;

        private Boolean preferSiteRequest;

        Builder setProjectName( final String projectName )
        {
            this.projectName = projectName;
            return this;
        }

        Builder setPreferSiteRequest( final Boolean preferSiteRequest )
        {
            this.preferSiteRequest = preferSiteRequest;
            return this;
        }

        ContentProjectResolver build()
        {
            return new ContentProjectResolver( this );
        }
    }
}
