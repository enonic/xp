package com.enonic.xp.core.impl.project;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.attachment.AttachmentSerializer;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ContentInitializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.util.BinaryReference;

@Component(immediate = true)
public class ProjectServiceImpl
    implements ProjectService
{
    private final static Logger LOG = LoggerFactory.getLogger( ProjectServiceImpl.class );

    private RepositoryService repositoryService;

    private IndexService indexService;

    private NodeService nodeService;

    @Override
    public Project create( CreateProjectParams params )
    {
        return callWithContext( () -> doCreate( params ) );
    }

    private Project doCreate( final CreateProjectParams params )
    {
        final ContentInitializer.Builder contentInitializer = ContentInitializer.create();

        if ( params.getIcon() != null )
        {
            contentInitializer.addBinaryAttachment( createProjectIcon( params.getIcon() ) );
        }
        final PropertyTree data = createProjectData( params.getDisplayName(), params.getDescription(), params.getIcon() );

        contentInitializer.
            setIndexService( indexService ).
            setNodeService( nodeService ).
            setRepositoryService( repositoryService ).
            repositoryId( params.getName().getRepoId() ).
            data( data ).
            build().
            initialize();

        LOG.info( "Project created: " + params.getName() );

        final Repository repository = repositoryService.get( params.getName().getRepoId() );
        return Project.from( repository );
    }

    @Override
    public Project modify( ModifyProjectParams params )
    {
        return callWithContext( () -> doModify( params ) );
    }

    private Project doModify( final ModifyProjectParams params )
    {
        final UpdateRepositoryParams updateParams = UpdateRepositoryParams.create().
            repositoryId( params.getName().getRepoId() ).
            editor( editableRepository -> {

                if ( params.getIcon() != null )
                {
                    editableRepository.binaryAttachments.add( createProjectIcon( params.getIcon() ) );
                }
                editableRepository.data = createProjectData( params.getDisplayName(), params.getDescription(), params.getIcon() );
            } ).
            build();

        final Repository updatedRepository = repositoryService.updateRepository( updateParams );

        LOG.info( "Project updated: " + params.getName() );

        return Project.from( updatedRepository );
    }

    @Override
    public Projects list()
    {
        return callWithContext( this::doList );
    }

    private Projects doList()
    {
        return Projects.from( this.repositoryService.list() );
    }

    @Override
    public Project get( final ProjectName projectName )
    {
        return callWithContext( () -> doGet( projectName ) );
    }

    private Project doGet( final ProjectName projectName )
    {
        return Project.from( this.repositoryService.get( projectName.getRepoId() ) );
    }

    @Override
    public void delete( ProjectName projectName )
    {
        runWithContext( () -> doDelete( projectName ) );
    }

    private void doDelete( final ProjectName projectName )
    {
        final DeleteRepositoryParams params = DeleteRepositoryParams.from( projectName.getRepoId() );
        this.repositoryService.deleteRepository( params );

        LOG.info( "Project deleted: " + projectName );
    }

    private PropertyTree createProjectData( final String displayName, final String description, final CreateAttachment icon )
    {
        final PropertyTree data = new PropertyTree();

        final PropertySet set = data.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        set.addString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, description );
        set.addString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY, displayName );
        if ( icon != null )
        {
            AttachmentSerializer.create( set, CreateAttachments.from( icon ), ProjectConstants.PROJECT_ICON_PROPERTY );
        }
        else
        {
            set.addSet( ProjectConstants.PROJECT_ICON_PROPERTY, null );
        }

        return data;
    }

    private BinaryAttachment createProjectIcon( final CreateAttachment icon )
    {
        if ( icon != null )
        {
            return new BinaryAttachment( BinaryReference.from( icon.getName() ), icon.getByteSource() );
        }

        return null;
    }

    private <T> T callWithContext( Callable<T> runnable )
    {
        return context().callWith( runnable );
    }

    private <T> void runWithContext( Runnable runnable )
    {
        context().runWith( runnable );
    }

    private Context context()
    {
        return ContextBuilder.create().
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            branch( ContentConstants.BRANCH_MASTER ).
            authInfo( ContextAccessor.current().getAuthInfo() ).
            build();
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
