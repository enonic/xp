package com.enonic.xp.portal.impl.url;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.RepositoryId;

final class ContentProjectResolver
{
    private final String explicitProjectName;

    ContentProjectResolver( final String explicitProjectName )
    {
        this.explicitProjectName = explicitProjectName;
    }

    public ProjectName resolve()
    {
        if ( explicitProjectName != null )
        {
            return ProjectName.from( explicitProjectName );
        }

        final PortalRequest portalRequest = PortalRequestAccessor.get();
        if ( portalRequest != null && portalRequest.isSiteBase() )
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
}
