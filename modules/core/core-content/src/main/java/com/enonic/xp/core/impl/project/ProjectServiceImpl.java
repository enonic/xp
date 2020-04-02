package com.enonic.xp.core.impl.project;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.attachment.AttachmentSerializer;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.ContentInitializer;
import com.enonic.xp.core.impl.issue.IssueInitializer;
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
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.security.SecurityService;
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

    private SecurityService securityService;

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
            accessControlList( CreateProjectRootAccessListCommand.create().
                projectName( params.getName() ).
                build().
                execute() ).
            build().
            initialize();

        IssueInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            repositoryId( params.getName().getRepoId() ).
            accessControlList( CreateProjectIssuesAccessListCommand.create().
                projectName( params.getName() ).
                build().
                execute() ).
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
        final UpdateRepositoryParams updateParams = UpdateRepositoryParams.create().
            repositoryId( params.getName().getRepoId() ).
            editor( editableRepository -> {

                if ( params.getIcon() != null )
                {
                    editableRepository.binaryAttachments.add( createProjectIcon( params.getIcon() ) );
                }
                editableRepository.data = createProjectData( params );
            } ).
            build();

        final Repository updatedRepository = repositoryService.updateRepository( updateParams );

        if ( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( params.getName() ) )
        {
            CreateProjectPermissionRolesCommand.create().
                securityService( securityService ).
                projectName( params.getName() ).
                projectDisplayName( params.getDisplayName() ).
                build().
                execute();
        }

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
                        ( ProjectConstants.DEFAULT_PROJECT_NAME.equals( project.getName() )
                            ? projectPermissionsContextManager.hasManagerAccess( authenticationInfo )
                            : projectPermissionsContextManager.hasAnyProjectPermission( project.getName(), authenticationInfo ) ) ).
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
        }, projectName );
    }

    private boolean doDelete( final ProjectName projectName )
    {
        final DeleteRepositoryParams params = DeleteRepositoryParams.from( projectName.getRepoId() );
        final RepositoryId deletedRepositoryId = this.repositoryService.deleteRepository( params );

        if ( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) )
        {
            DeleteProjectPermissionRolesCommand.create().
                securityService( securityService ).
                projectName( projectName ).
                build().
                execute();
        }

        return deletedRepositoryId != null;
    }

    @Override
    public ProjectPermissions getPermissions( final ProjectName projectName )
    {
        if ( ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) )
        {
            throw new IllegalArgumentException( "Default project has no roles." );
        }

        return callWithGetContext( () -> doGetPermissions( projectName ), projectName );
    }

    private ProjectPermissions doGetPermissions( final ProjectName projectName )
    {
        return GetProjectPermissionsCommand.create().
            securityService( securityService ).
            projectName( projectName ).
            build().
            execute();
    }

    @Override
    public ProjectPermissions modifyPermissions( final ProjectName projectName, final ProjectPermissions projectPermissions )
    {
        if ( ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) )
        {
            throw new IllegalArgumentException( "Default project permissions cannot be modified." );
        }

        return callWithUpdateContext( () -> {

            final ProjectPermissions result = doModifyPermissions( projectName, projectPermissions );
            LOG.info( "Project permissions updated: " + projectName );

            return result;


        }, projectName );
    }

    private ProjectPermissions doModifyPermissions( final ProjectName projectName, final ProjectPermissions projectPermissions )
    {
        return UpdateProjectPermissionsCommand.create().
            projectName( projectName ).
            permissions( projectPermissions ).
            securityService( securityService ).
            build().
            execute();
    }

    private PropertyTree createProjectData( final ModifyProjectParams params )
    {
        final PropertyTree data = new PropertyTree();

        final PropertySet set = data.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        set.addString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, params.getDescription() );
        set.addString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY, params.getDisplayName() );
        if ( params.getIcon() != null )
        {
            AttachmentSerializer.create( set, CreateAttachments.from( params.getIcon() ), ProjectConstants.PROJECT_ICON_PROPERTY );
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

    private <T> T callWithDeleteContext( final Callable<T> runnable, final ProjectName projectName )
    {
        return projectPermissionsContextManager.initDeleteContext( projectName ).callWith( runnable );
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
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setProjectPermissionsContextManager( final ProjectPermissionsContextManager projectPermissionsContextManager )
    {
        this.projectPermissionsContextManager = projectPermissionsContextManager;
    }
}
