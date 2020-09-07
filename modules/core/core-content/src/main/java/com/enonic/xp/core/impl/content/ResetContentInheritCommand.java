package com.enonic.xp.core.impl.content;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.project.ParentProjectSynchronizer;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;


final class ResetContentInheritCommand
    extends AbstractContentCommand
{
    private final ResetContentInheritParams params;

    private final ContentService contentService;

    private final ProjectService projectService;

    private final MediaInfoService mediaInfoService;

    private ResetContentInheritCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfoService = builder.mediaInfoService;
        this.contentService = builder.contentService;
        this.projectService = builder.projectService;
    }

    public static Builder create( final ResetContentInheritParams params )
    {
        return new Builder( params );
    }

    void execute()
    {
        final Context context = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( params.getProjectName().getRepoId() ).branch( ContentConstants.BRANCH_DRAFT ).
            authInfo( createAdminAuthInfo() ).
            build();

        context.runWith( () -> {
            if ( contentService.contentExists( params.getContentId() ) )
            {

                final Content content = contentService.getById( params.getContentId() );
                final Set<ContentInheritType> typesToReset = params.getInherit().
                    stream().
                    filter( contentInheritType -> !content.getInherit().contains( contentInheritType ) ).
                    collect( Collectors.toSet() );

                if ( !typesToReset.isEmpty() )
                {
                    final UpdateContentParams updateParams = new UpdateContentParams().
                        contentId( content.getId() ).
                        modifier( content.getModifier() ).
                        stopInherit( false ).
                        editor( edit -> edit.inherit = processInherit( edit.inherit, typesToReset ) );

                    contentService.update( updateParams );

                    final ParentProjectSynchronizer synchronizer = createSynchronizer( params.getProjectName() );
                    synchronizer.sync( content.getId() );
                }
            }
        } );
    }

    private EnumSet<ContentInheritType> processInherit( final Set<ContentInheritType> oldTypes, final Set<ContentInheritType> newTypes )
    {
        return EnumSet.copyOf( Stream.concat( oldTypes.stream(), newTypes.stream() ).collect( Collectors.toSet() ) );
    }

    private ParentProjectSynchronizer createSynchronizer( final ProjectName targetProjectName )
    {
        final Project targetProject = createAdminContext().callWith( () -> projectService.get( targetProjectName ) );

        if ( targetProject == null )
        {
            throw new IllegalArgumentException( String.format( "Project with name [%s] doesn't exist", targetProjectName ) );
        }

        if ( targetProject.getParent() == null )
        {
            throw new IllegalArgumentException( String.format( "Project with name [%s] has no parent", targetProject.getParent() ) );
        }
        final Project sourceProject = createAdminContext().callWith( () -> projectService.get( targetProject.getParent() ) );

        if ( sourceProject == null )
        {
            throw new IllegalArgumentException( String.format( "Project with name [%s] doesn't exist", targetProject.getParent() ) );
        }
        return doCreateSynchronizer( sourceProject, targetProject );
    }

    private ParentProjectSynchronizer doCreateSynchronizer( final Project sourceProject, final Project targetProject )
    {
        return ParentProjectSynchronizer.create().
            contentService( contentService ).
            mediaInfoService( mediaInfoService ).
            sourceProject( sourceProject ).
            targetProject( targetProject ).
            build();
    }

    private Context createAdminContext()
    {
        final AuthenticationInfo authInfo = createAdminAuthInfo();
        return ContextBuilder.from( ContentConstants.CONTEXT_DRAFT ).
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

        private MediaInfoService mediaInfoService;

        private ProjectService projectService;

        private ContentService contentService;

        private Builder( final ResetContentInheritParams params )
        {
            this.params = params;
        }

        public Builder mediaInfoService( final MediaInfoService value )
        {
            this.mediaInfoService = value;
            return this;
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

        @Override
        void validate()
        {
            super.validate();
        }

        public ResetContentInheritCommand build()
        {
            validate();
            return new ResetContentInheritCommand( this );
        }
    }

}
