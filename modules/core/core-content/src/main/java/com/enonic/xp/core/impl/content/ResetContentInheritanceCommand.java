package com.enonic.xp.core.impl.content;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
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
        final Context targetContext = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( params.getProjectName().getRepoId() )
            .branch( ContentConstants.BRANCH_DRAFT )
            .authInfo( createAdminAuthInfo() )
            .build();

        targetContext.runWith( () -> {
            if ( contentService.contentExists( params.getContentId() ) )
            {
                final Content targetContent = contentService.getById( params.getContentId() );
                final Set<ContentInheritType> typesToReset = params.getInherit()
                    .stream()
                    .filter( contentInheritType -> !targetContent.getInherit().contains( contentInheritType ) )
                    .collect( Collectors.toSet() );

                if ( !typesToReset.isEmpty() )
                {
                    final PatchContentParams patchParams = PatchContentParams.create().skipSync( true ).contentId( targetContent.getId() ).patcher( edit -> {
                        edit.inherit.setValue( processInherit( edit.inherit.originalValue, typesToReset ) );
                        edit.workflowInfo.setValue( WorkflowInfo.inProgress() );
                    } ).build();

                    contentService.patch( patchParams );

                    syncContent( targetContent.getId(), params.getProjectName() );
                }
            }
        } );
    }

    private EnumSet<ContentInheritType> processInherit( final Set<ContentInheritType> oldTypes, final Set<ContentInheritType> newTypes )
    {
        return EnumSet.copyOf( Stream.concat( oldTypes.stream(), newTypes.stream() ).collect( Collectors.toSet() ) );
    }

    private void syncContent( final ContentId contentId, final ProjectName targetProjectName )
    {
        final List<ProjectName> parents = projectService.get( targetProjectName ).getParents();

        if ( parents.isEmpty() )
        {
            throw new IllegalArgumentException( String.format( "Project with name [%s] has no any parent", targetProjectName ) );
        }

        final ProjectName sourceProjectName = parents.stream()
            .map( projectName -> ContextBuilder.from( ContextAccessor.current() )
                .repositoryId( projectName.getRepoId() )
                .branch( ContentConstants.BRANCH_DRAFT )
                .authInfo( createAdminAuthInfo() )
                .build() )
            .filter( context -> context.callWith( () -> contentService.contentExists( params.getContentId() ) ) )
            .map( context -> ProjectName.from( context.getRepositoryId() ) )
            .findFirst()
            .orElseThrow( () -> new IllegalArgumentException( "No source content to inherit" ) );

        contentSynchronizer.sync( ContentSyncParams.create()
                                      .addContentId( contentId )
                                      .sourceProject( sourceProjectName )
                                      .targetProject( targetProjectName )
                                      .includeChildren( false )
                                      .build() );
    }

    private AuthenticationInfo createAdminAuthInfo()
    {
        return AuthenticationInfo.create()
            .principals( RoleKeys.ADMIN )
            .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
            .build();
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
            super.validate();
            Objects.requireNonNull( this.projectService );
            Objects.requireNonNull( this.contentService );
            Objects.requireNonNull( this.contentSynchronizer );
        }

        public ResetContentInheritanceCommand build()
        {
            validate();
            return new ResetContentInheritanceCommand( this );
        }
    }

}
