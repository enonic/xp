package com.enonic.xp.core.impl.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class ParentProjectSyncTask
    implements Runnable
{
    private final ProjectService projectService;

    private final ContentService contentService;

    private final MediaInfoService mediaInfoService;

    public ParentProjectSyncTask( final Builder builder )
    {
        this.projectService = builder.projectService;
        this.contentService = builder.contentService;
        this.mediaInfoService = builder.mediaInfoService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run()
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
                doSync( project, parentProject );
            } ) );

    }

    public void run( final Project targetProject, final Project sourceProject )
    {
        createAdminContext().runWith( () -> doSync( targetProject, sourceProject ) );
    }

    private void doSync( final Project targetProject, final Project sourceProject )
    {
        final ParentProjectSynchronizer parentProjectSynchronizer = ParentProjectSynchronizer.create().
            targetProject( targetProject ).
            sourceProject( sourceProject ).
            contentService( contentService ).
            mediaInfoService( mediaInfoService ).
            build();

        parentProjectSynchronizer.syncRoot();
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

        private ContentService contentService;

        private MediaInfoService mediaInfoService;

        private Builder()
        {
        }

        public Builder projectService( final ProjectService projectService )
        {
            this.projectService = projectService;
            return this;
        }

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder mediaInfoService( final MediaInfoService mediaInfoService )
        {
            this.mediaInfoService = mediaInfoService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.projectService, "projectService must be set." );
            Preconditions.checkNotNull( this.contentService, "contentService must be set." );
        }

        public ParentProjectSyncTask build()
        {
            validate();
            return new ParentProjectSyncTask( this );
        }
    }
}
