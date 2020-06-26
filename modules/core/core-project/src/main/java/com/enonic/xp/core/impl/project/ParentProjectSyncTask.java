package com.enonic.xp.core.impl.project;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class ParentProjectSyncTask
    implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger( ParentProjectSyncTask.class );

    private ProjectService projectService;

    private ContentService contentService;

    public ParentProjectSyncTask( final Builder builder )
    {
        this.projectService = builder.projectService;
        this.contentService = builder.contentService;
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

                if ( parentProject == null )
                {
                    LOG.warn( "parent project [{}] does not exist.", project.getParent() );
                }
                else
                {
                    doSync( project, project.getParent() );
                }
            } ) );

    }

    private void doSync( final Project targetProject, final ProjectName sourceProjectName )
    {

        final ProjectName targetProjectName = targetProject.getName();

        final Context targetContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( targetProjectName.getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            build();

        final Context sourceContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( sourceProjectName.getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            build();

        if ( targetProject.getParent() == null )
        {
            throw new IllegalArgumentException( "target project [" + targetProject + "] has no parent to import from." );
        }

        if ( !sourceProjectName.equals( targetProject.getParent() ) )
        {
            throw new IllegalArgumentException( "[" + sourceProjectName + "] is not a parent project for [" + targetProject + "]." );
        }

        final Queue<ContentPath> queue = new LinkedList( List.of( ContentPath.ROOT ) );

        sourceContext.runWith( () -> {
            while ( queue.size() > 0 )
            {

                final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create().
                    parentPath( queue.poll() ).
                    recursive( false ).
                    childOrder( ChildOrder.path() ).
                    size( -1 ).
                    build() );

                for ( final Content content : result.getContents() )
                {
                    if ( WorkflowState.READY.equals( content.getWorkflowInfo().getState() ) )
                    {
                        final CreateContentParams params = createParams( content );
                        targetContext.runWith( () -> {

                            if ( !contentService.contentExists( content.getPath() ) )
                            {
                                contentService.create( params );
                            }

                            if ( content.hasChildren() )
                            {
                                queue.offer( content.getPath() );
                            }

                        } );
                    }
                }
            }

        } );
    }

    private CreateContentParams createParams( final Content source )
    {
        final CreateContentParams.Builder builder = CreateContentParams.create();

        builder.contentId( source.getId() ).
            contentData( source.getData() ).
            extraDatas( source.getAllExtraData() ).
            type( source.getType() ).
            owner( source.getOwner() ).
            displayName( source.getDisplayName() ).
            name( source.getName() ).
            parent( source.getParentPath() ).
            requireValid( false ).
            createSiteTemplateFolder( false ).
            inheritPermissions( true ).
            createAttachments( CreateAttachments.from( source.getAttachments().
                stream().
                map( attachment -> {
                    final ByteSource binary = contentService.getBinary( source.getId(), attachment.getBinaryReference() );

                    return CreateAttachment.create().
                        name( attachment.getName() ).
                        label( attachment.getLabel() ).
                        mimeType( attachment.getMimeType() ).
                        text( attachment.getTextContent() ).
                        byteSource( binary ).
                        build();
                } ).collect( Collectors.toSet() ) ) ).
            childOrder( source.getChildOrder() ).
            language( source.getLanguage() );/*.
//          permissions( source.getPermissions() ).
            contentPublishInfo( source.getPublishInfo() ).
            workflowInfo( source.getWorkflowInfo() );*/

        return builder.build();
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
