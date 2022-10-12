package com.enonic.xp.core.impl.content;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;


final class ResetContentInheritanceCommand
    extends AbstractContentCommand
{
    private final ResetContentInheritParams params;

    private final ContentService contentService;

    private final ProjectService projectService;

    private final ContentSynchronizer contentSynchronizer;


    private ResetContentInheritanceCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.contentService = builder.contentService;
        this.projectService = builder.projectService;
        this.contentSynchronizer = builder.contentSynchronizer;
    }

    public static Builder create( final ResetContentInheritParams params )
    {
        return new Builder( params );
    }

    void execute()
    {
        final ProjectName sourceProjectName = fetchSourceProjectName( params.getProjectName() );

        validateSourceContentExist( sourceProjectName );

        final Context targetContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( params.getProjectName().getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            authInfo( createAdminAuthInfo() ).
            build();

        targetContext.runWith( () -> {
            if ( contentService.contentExists( params.getContentId() ) )
            {
                final Content targetContent = contentService.getById( params.getContentId() );
                final Set<ContentInheritType> typesToReset = params.getInherit().
                    stream().
                    filter( contentInheritType -> !targetContent.getInherit().contains( contentInheritType ) ).
                    collect( Collectors.toSet() );

                if ( !typesToReset.isEmpty() )
                {
                    final UpdateContentParams updateParams = new UpdateContentParams().
                        contentId( targetContent.getId() ).
                        stopInherit( false ).
                        editor( edit -> {
                            edit.inherit = processInherit( edit.inherit, typesToReset );
                            edit.workflowInfo = WorkflowInfo.inProgress();
                        } );

                    contentService.update( updateParams );

                    syncContent( targetContent.getId(), sourceProjectName, params.getProjectName() );
                }
            }
        } );
    }

    private EnumSet<ContentInheritType> processInherit( final Set<ContentInheritType> oldTypes, final Set<ContentInheritType> newTypes )
    {
        return EnumSet.copyOf( Stream.concat( oldTypes.stream(), newTypes.stream() ).collect( Collectors.toSet() ) );
    }

    private void syncContent( final ContentId contentId, final ProjectName sourceProjectName, final ProjectName targetProjectName )
    {
        contentSynchronizer.sync( ContentSyncParams.create()
                                      .addContentId( contentId )
                                      .sourceProject( sourceProjectName )
                                      .targetProject( targetProjectName )
                                      .includeChildren( false )
                                      .build() );
    }

    private ProjectName fetchSourceProjectName( final ProjectName targetProjectName )
    {
        final Project targetProject = createAdminContext().callWith( () -> projectService.get( targetProjectName ) );

        if ( targetProject == null )
        {
            throw new IllegalArgumentException( String.format( "Project with name [%s] doesn't exist", targetProjectName ) );
        }

        if ( targetProject.getParent() == null )
        {
            throw new IllegalArgumentException( String.format( "Project with name [%s] has no parent", targetProject.getName() ) );
        }

        final Project sourceProject = createAdminContext().callWith( () -> projectService.get( targetProject.getParent() ) );

        if ( sourceProject == null )
        {
            throw new IllegalArgumentException( String.format( "Project with name [%s] doesn't exist", targetProject.getParent() ) );
        }

        return sourceProject.getName();
    }

    private void validateSourceContentExist( final ProjectName sourceProjectName )
    {
        final Context sourceContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( sourceProjectName.getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            authInfo( createAdminAuthInfo() ).
            build();

        if ( !sourceContext.callWith( () -> contentService.contentExists( params.getContentId() ) ) )
        {
            throw new IllegalArgumentException(
                String.format( "[%s] content is missed in [%s] project", params.getContentId(), sourceProjectName ) );
        }
    }

    private Context createAdminContext()
    {
        final AuthenticationInfo authInfo = createAdminAuthInfo();
        return ContextBuilder.from( ContextAccessor.current() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            repositoryId( ContentConstants.CONTENT_REPO_ID ).
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
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ResetContentInheritParams params;

        private ProjectService projectService;

        private ContentService contentService;

        private ContentSynchronizer contentSynchronizer;

        private Builder( final ResetContentInheritParams params )
        {
            this.params = params;
        }

        public Builder projectService( final ProjectService value )
        {
            this.projectService = value;
            return this;
        }

        public Builder contentService( final ContentService value )
        {
            this.contentService = value;
            return this;
        }

        public Builder contentSynchronizer( final ContentSynchronizer contentSynchronizer )
        {
            this.contentSynchronizer = contentSynchronizer;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( this.projectService, "projectService must be set." );
            Preconditions.checkNotNull( this.contentService, "contentService must be set." );
            Preconditions.checkNotNull( this.contentSynchronizer, "contentSynchronizer must be set." );

            super.validate();
        }

        public ResetContentInheritanceCommand build()
        {
            validate();
            return new ResetContentInheritanceCommand( this );
        }
    }

}
