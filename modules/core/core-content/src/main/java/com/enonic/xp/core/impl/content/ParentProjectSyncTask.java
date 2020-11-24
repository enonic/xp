package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

final class ParentProjectSyncTask
    implements Runnable
{
    private final ProjectService projectService;

    private final IndexService indexService;

    private final ContentSynchronizer contentSynchronizer;

    public ParentProjectSyncTask( final Builder builder )
    {
        this.projectService = builder.projectService;
        this.indexService = builder.indexService;
        this.contentSynchronizer = builder.contentSynchronizer;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run()
    {
        if ( indexService.isMaster() )
        {
            createAdminContext().runWith( () -> this.projectService.list().
                stream().
                filter( project -> project.getParent() != null ).
                sorted( ( o1, o2 ) -> {

                    if ( o2.getName().equals( o1.getParent() ) )
                    {
                        return 1;
                    }

                    if ( o1.getName().equals( o2.getParent() ) )
                    {
                        return -1;
                    }

                    return 0;
                } ).
                forEach( project -> {
                    Project parentProject = this.projectService.get( project.getParent() );
                    doSync( parentProject, project );
                } ) );
        }
    }

    private void doSync( final Project sourceProject, final Project targetProject )
    {
        contentSynchronizer.sync( ContentSyncParams.create().
            sourceProject( sourceProject.getName() ).
            targetProject( targetProject.getName() ).
            build() );
    }

    private Context createAdminContext()
    {
        final AuthenticationInfo authInfo = createAdminAuthInfo();
        return ContextBuilder.from( ContentConstants.CONTEXT_MASTER ).
            authInfo( authInfo ).
            build();
    }

    private AuthenticationInfo createAdminAuthInfo()
    {
        return AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( User.create().
                key( PrincipalKey.ofSuperUser() ).
                login( PrincipalKey.ofSuperUser().getId() ).
                build() ).
            build();
    }

    public static class Builder
    {
        private ProjectService projectService;

        private IndexService indexService;

        private ContentSynchronizer contentSynchronizer;

        private Builder()
        {
        }

        public Builder projectService( final ProjectService projectService )
        {
            this.projectService = projectService;
            return this;
        }

        public Builder indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        public Builder contentSynchronizer( final ContentSynchronizer contentSynchronizer )
        {
            this.contentSynchronizer = contentSynchronizer;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.indexService, "indexService must be set." );
            Preconditions.checkNotNull( this.projectService, "projectService must be set." );
            Preconditions.checkNotNull( this.contentSynchronizer, "contentSynchronizer must be set." );
        }

        public ParentProjectSyncTask build()
        {
            validate();
            return new ParentProjectSyncTask( this );
        }
    }
}
