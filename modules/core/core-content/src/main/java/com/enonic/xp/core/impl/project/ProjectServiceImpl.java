package com.enonic.xp.core.impl.project;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.ContentInitializer;
import com.enonic.xp.core.impl.issue.IssueInitializer;
import com.enonic.xp.core.impl.project.layer.LayerAlreadyExistsException;
import com.enonic.xp.core.impl.project.layer.LayerNotFoundException;
import com.enonic.xp.data.PropertySet;
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
import com.enonic.xp.project.layer.ContentLayer;
import com.enonic.xp.project.layer.ContentLayerKey;
import com.enonic.xp.project.layer.CreateLayerParams;
import com.enonic.xp.project.layer.ModifyLayerParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.EditableRepository;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

@Component(immediate = true)
public class ProjectServiceImpl
    implements ProjectService
{
    private final static Logger LOG = LoggerFactory.getLogger( ProjectServiceImpl.class );

    private RepositoryService repositoryService;

    private IndexService indexService;

    private NodeService nodeService;

    private ProjectPermissionsContextManager projectPermissionsContextManager;


    @Override
    public Project create( CreateProjectParams params )
    {
        return callWithCreateContext( ( () -> {
            final Project result = doCreate( params );
            LOG.info( "Project created: " + params.getName() );

            return result;
        } ) );
    }

    private Project doCreate( final CreateProjectParams params )
    {
        if ( repositoryService.isInitialized( params.getName().getRepoId() ) )
        {
            throw new ProjectAlreadyExistsException( params.getName() );
        }

        final ContentInitializer.Builder contentInitializer = ContentInitializer.create();

        contentInitializer.
            setIndexService( indexService ).
            setNodeService( nodeService ).
            setRepositoryService( repositoryService ).
            repositoryId( params.getName().getRepoId() ).
            build().
            initialize();

        IssueInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            repositoryId( params.getName().getRepoId() ).
            build().
            initialize();

        final ModifyProjectParams modifyProjectParams = ModifyProjectParams.create( params ).build();
        return doModify( modifyProjectParams );
    }

    @Override
    public Project modify( ModifyProjectParams params )
    {
        return callWithUpdateContext( ( () -> {
            final Project result = doModify( params );
            LOG.info( "Project updated: " + params.getName() );

            return result;
        } ), params.getName() );
    }

    private Project doModify( final ModifyProjectParams params )
    {
        final Project prevProject = doGet( params.getName() );

        final UpdateRepositoryParams updateParams = UpdateRepositoryParams.create().
            repositoryId( params.getName().getRepoId() ).
            editor( editableRepository -> {

                if ( prevProject != null && prevProject.getIcon() != null )
                {
                    editableRepository.binaryAttachments = editableRepository.binaryAttachments.stream().
                        filter( binaryAttachment -> binaryAttachment.getReference().equals( prevProject.getIcon().getBinaryReference() ) ).
                        collect( Collectors.toList() );
                }

                if ( params.getIcon() != null )
                {
                    editableRepository.binaryAttachments.add( createIcon( params.getIcon() ) );
                }

                final PropertySet projectData =
                    editableRepository.data.getSet( ProjectConstants.PROJECT_DATA_SET_NAME ) == null ? editableRepository.data.addSet(
                        ProjectConstants.PROJECT_DATA_SET_NAME ) : editableRepository.data.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );

                params.toData().getSet( ProjectConstants.PROJECT_DATA_SET_NAME ).getProperties().forEach( property -> {
                    projectData.setProperty( property.getName(), property.getValue() );
                } );
            } ).
            build();

        final Repository updatedRepository = repositoryService.updateRepository( updateParams );
        return Project.from( updatedRepository );
    }

    @Override
    public Projects list()
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();

        return callWithListContext( () -> {
            final Projects projects = this.doList();

            return Projects.create().
                addAll( projects.stream().
                    filter( project -> projectPermissionsContextManager.hasAdminAccess( authenticationInfo ) ||
                        projectPermissionsContextManager.hasAnyProjectPermission( project.getName(), authenticationInfo ) ).
                    collect( Collectors.toSet() ) ).
                build();
        } );
    }

    private Projects doList()
    {
        return Projects.from( this.repositoryService.list() );
    }

    @Override
    public Project get( final ProjectName projectName )
    {
        return callWithGetContext( () -> doGet( projectName ), projectName );
    }

    private Project doGet( final ProjectName projectName )
    {
        return Project.from( this.repositoryService.get( projectName.getRepoId() ) );
    }

    @Override
    public boolean delete( ProjectName projectName )
    {
        return callWithDeleteContext( () -> {
            final boolean result = doDelete( projectName );
            LOG.info( "Project deleted: " + projectName );

            return result;
        } );
    }

    private boolean doDelete( final ProjectName projectName )
    {
        final DeleteRepositoryParams params = DeleteRepositoryParams.from( projectName.getRepoId() );
        final RepositoryId deletedRepositoryId = this.repositoryService.deleteRepository( params );

        return deletedRepositoryId != null;
    }

    @Override
    public ContentLayer createLayer( final CreateLayerParams params )
    {
        return callWithUpdateContext( ( () -> {
            final ContentLayer result = doCreateLayer( params );
            LOG.info( "Layer created: " + params.getKey() );

            return result;
        } ), params.getKey().getProjectName() );
    }

    private ContentLayer doCreateLayer( final CreateLayerParams params )
    {
        final Project project = doGet( params.getKey().getProjectName() );

        if ( project == null )
        {
            throw new ProjectNotFoundException( params.getKey().getProjectName() );
        }

        if ( project.getKeys().contains( params.getKey() ) )
        {
            throw new LayerAlreadyExistsException( params.getKey() );
        }

        return doModifyLayer( ModifyLayerParams.create( params ).build() );
    }

    @Override
    public ContentLayer modifyLayer( final ModifyLayerParams params )
    {
        return callWithUpdateContext( ( () -> {
            final ContentLayer result = doModifyLayer( params );
            LOG.info( "Layer updated: " + params.getKey() );

            return result;
        } ), params.getKey().getProjectName() );
    }

    private ContentLayer doModifyLayer( final ModifyLayerParams params )
    {
        final Project prevProject = doGet( params.getKey().getProjectName() );

        final UpdateRepositoryParams updateParams = UpdateRepositoryParams.create().
            repositoryId( params.getKey().getProjectName().getRepoId() ).
            editor( editableRepository -> {

                if ( prevProject != null )
                {
                    final ContentLayer prevLayer = prevProject.getLayers().getLayer( params.getKey() );

                    if ( prevLayer != null && prevLayer.getIcon() != null )
                    {
                        editableRepository.binaryAttachments = editableRepository.binaryAttachments.stream().
                            filter(
                                binaryAttachment -> binaryAttachment.getReference().equals( prevLayer.getIcon().getBinaryReference() ) ).
                            collect( Collectors.toList() );
                    }
                }

                if ( params.getIcon() != null )
                {
                    editableRepository.binaryAttachments.add( createIcon( params.getIcon() ) );
                }

                this.doRemoveLayer( editableRepository, params.getKey() );
                editableRepository.data.getSet( ProjectConstants.PROJECT_DATA_SET_NAME ).addSet( ProjectConstants.PROJECT_LAYERS_PROPERTY,
                                                                                                 params.toData() );
            } ).
            build();

        final Repository updatedRepository = repositoryService.updateRepository( updateParams );
        final Project updatedProject = Project.from( updatedRepository );

        return updatedProject.getLayers().getLayer( params.getKey() );
    }

    @Override
    public boolean deleteLayer( final ContentLayerKey key )
    {
        return callWithUpdateContext( () -> {
            final boolean result = doDeleteLayer( key );
            LOG.info( "Layer deleted: " + key );

            return result;
        }, key.getProjectName() );
    }

    private boolean doDeleteLayer( final ContentLayerKey key )
    {
        final Project project = doGet( key.getProjectName() );

        if ( !project.getLayers().getKeys().contains( key ) )
        {
            throw new LayerNotFoundException( key );
        }

        final UpdateRepositoryParams updateParams = UpdateRepositoryParams.create().
            repositoryId( key.getProjectName().getRepoId() ).
            editor( editableRepository -> {
                this.doRemoveLayer( editableRepository, key );
            } ).
            build();

        repositoryService.updateRepository( updateParams );
        return true;
    }

    private void doRemoveLayer( final EditableRepository editableRepository, final ContentLayerKey key )
    {
        final PropertySet projectData = editableRepository.data.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );

        if ( projectData == null )
        {
            throw new ProjectNotFoundException( key.getProjectName() );
        }

        final Iterable<PropertySet> layerSets = projectData.getSets( ProjectConstants.PROJECT_LAYERS_PROPERTY );
        final Iterator<PropertySet> layersIterator = layerSets.iterator();

        while ( layersIterator.hasNext() )
        {
            final PropertySet layerSet = layersIterator.next();
            if ( key.toString().equals( layerSet.getPropertyNames()[0] ) )
            {
                layersIterator.remove();
            }
        }

        projectData.removeProperties( ProjectConstants.PROJECT_LAYERS_PROPERTY );
        projectData.addSets( ProjectConstants.PROJECT_LAYERS_PROPERTY, Iterables.toArray( layerSets, PropertySet.class ) );
    }

    private BinaryAttachment createIcon( final CreateAttachment icon )
    {
        if ( icon != null )
        {
            return new BinaryAttachment( BinaryReference.from( icon.getName() ), icon.getByteSource() );
        }

        return null;
    }

    private <T> T callWithCreateContext( final Callable<T> runnable )
    {
        return projectPermissionsContextManager.initCreateContext().callWith( runnable );
    }

    private <T> T callWithUpdateContext( final Callable<T> runnable, final ProjectName projectName )
    {
        return projectPermissionsContextManager.initUpdateContext( projectName ).callWith( runnable );
    }

    private <T> T callWithGetContext( final Callable<T> runnable, final ProjectName projectName )
    {
        return projectPermissionsContextManager.initGetContext( projectName ).callWith( runnable );
    }

    private <T> T callWithListContext( final Callable<T> runnable )
    {
        return projectPermissionsContextManager.initListContext().callWith( runnable );
    }

    private <T> T callWithDeleteContext( final Callable<T> runnable )
    {
        return projectPermissionsContextManager.initDeleteContext().callWith( runnable );
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

    @Reference
    public void setProjectPermissionsContextManager( final ProjectPermissionsContextManager projectPermissionsContextManager )
    {
        this.projectPermissionsContextManager = projectPermissionsContextManager;
    }
}
