package com.enonic.xp.core.impl.project;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentSerializer;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.project.init.ContentInitializer;
import com.enonic.xp.core.impl.project.init.IssueInitializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectIconParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectGraph;
import com.enonic.xp.project.ProjectGraphEntry;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectNotFoundException;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

public class ProjectServiceImpl
    implements ProjectService
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectServiceImpl.class );

    private final RepositoryService repositoryService;

    private final IndexService indexService;

    private final NodeService nodeService;

    private final SecurityService securityService;

    private final ProjectPermissionsContextManager projectPermissionsContextManager;

    private final EventPublisher eventPublisher;

    public ProjectServiceImpl( final RepositoryService repositoryService, final IndexService indexService, final NodeService nodeService,
                               final SecurityService securityService,
                               final ProjectPermissionsContextManager projectPermissionsContextManager,
                               final EventPublisher eventPublisher )
    {
        this.repositoryService = repositoryService;
        this.indexService = indexService;
        this.nodeService = nodeService;
        this.securityService = securityService;
        this.projectPermissionsContextManager = projectPermissionsContextManager;
        this.eventPublisher = eventPublisher;
    }

    public void initialize()
    {
        adminContext().runWith( () -> doCreate( CreateProjectParams.create().
            name( ProjectConstants.DEFAULT_PROJECT.getName() ).
            displayName( ProjectConstants.DEFAULT_PROJECT.getDisplayName() ).
            description( ProjectConstants.DEFAULT_PROJECT.getDescription() ).
            build() ) );
    }

    @Override
    public Project create( CreateProjectParams params )
    {
        return callWithCreateContext( () -> {
            if ( repositoryService.isInitialized( params.getName().getRepoId() ) )
            {
                throw new ProjectAlreadyExistsException( params.getName() );
            }
            final Project result = doCreate( params );

            CreateProjectRolesCommand.create().
                securityService( securityService ).
                projectName( params.getName() ).
                projectDisplayName( params.getDisplayName() ).
                build().
                execute();

            eventPublisher.publish( ProjectEvents.created( params.getName() ) );

            LOG.debug( "Project created: " + params.getName() );

            return result;
        } );
    }

    private Project doCreate( final CreateProjectParams params )
    {
        ContentInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            setRepositoryService( repositoryService ).
            repositoryId( params.getName().getRepoId() ).
            setData( createProjectData( params ) ).
            accessControlList( CreateProjectRootAccessListCommand.create().
                projectName( params.getName() ).
                permissions( params.getPermissions() ).
                build().
                execute() ).
            forceInitialization( params.isForceInitialization() ).
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
            forceInitialization( params.isForceInitialization() ).
            build().
            initialize();

        return Project.from( repositoryService.get( params.getName().getRepoId() ) );
    }

    @Override
    public Project modify( ModifyProjectParams params )
    {
        return callWithUpdateContext( () -> {
            final Project result = doModify( params );
            LOG.debug( "Project updated: " + params.getName() );

            return result;
        }, params.getName() );
    }

    private Project doModify( final ModifyProjectParams params )
    {
        final UpdateRepositoryParams updateParams = UpdateRepositoryParams.create().
            repositoryId( params.getName().getRepoId() ).
            editor( editableRepository -> modifyProjectData( params, editableRepository.data ) ).
            build();

        final Repository updatedRepository = repositoryService.updateRepository( updateParams );

        if ( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( params.getName() ) )
        {
            UpdateProjectRoleNamesCommand.create().
                securityService( securityService ).
                projectName( params.getName() ).
                projectDisplayName( params.getDisplayName() ).
                build().
                execute();
        }

        return Project.from( updatedRepository );
    }

    @Override
    public void modifyIcon( final ModifyProjectIconParams params )
    {
        callWithUpdateContext( () -> {
            doModifyIcon( params );
            LOG.debug( "Icon for project updated: " + params.getName() );

            return true;
        }, params.getName() );
    }

    private void doModifyIcon( final ModifyProjectIconParams params )
    {
        final UpdateRepositoryParams updateParams = UpdateRepositoryParams.create().
            repositoryId( params.getName().getRepoId() ).
            editor( editableRepository -> {

                if ( params.getIcon() != null )
                {
                    try
                    {
                        editableRepository.binaryAttachments.add( createProjectIcon( params.getIcon(), params.getScaleWidth() ) );
                    }
                    catch ( IOException e )
                    {
                        throw new UncheckedIOException( e );
                    }
                }

                final PropertySet projectData = editableRepository.data.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );
                setIconData( projectData, params.getIcon() );
            } ).
            build();

        repositoryService.updateRepository( updateParams );
    }

    @Override
    public ByteSource getIcon( final ProjectName projectName )
    {
        return callWithGetContext( () -> doGetIcon( projectName ), projectName );
    }

    private ByteSource doGetIcon( final ProjectName projectName )
    {
        return Optional.ofNullable( doGet( projectName ) ).
            map( Project::getIcon ).
            map( Attachment::getBinaryReference ).
            map( binaryReference -> repositoryService.getBinary( projectName.getRepoId(), binaryReference ) ).
            orElse( null );
    }

    @Override
    public Projects list()
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();

        return callWithListContext( () -> {
            final Projects projects = this.doList();

            return Projects.create().
                addAll( projects.stream().
                    filter( project -> ProjectAccessHelper.hasAdminAccess( authenticationInfo ) ||
                        ( ProjectConstants.DEFAULT_PROJECT_NAME.equals( project.getName() )
                            ? ProjectAccessHelper.hasManagerAccess( authenticationInfo )
                            : projectPermissionsContextManager.hasAnyProjectRole( authenticationInfo, project.getName(),
                                                                                  EnumSet.allOf( ProjectRole.class ) ) ) ).
                    collect( Collectors.toSet() ) ).
                build();
        } );
    }

    @Override
    public ProjectGraph graph( final ProjectName projectName )
    {
        final ProjectGraph.Builder graph = ProjectGraph.create();
        final Project targetProject;

        try
        {
            targetProject = this.get( projectName );

            if ( targetProject == null )
            {
                throw new ProjectNotFoundException( projectName );
            }
        }
        catch ( ProjectAccessException e )
        {
            throw new ProjectNotFoundException( e.getProjectName() );
        }

        final Projects projects = adminContext().callWith( this::doList );

        Project project = targetProject;

        final List<Project> parents = new ArrayList<>();

        while ( project.getParent() != null )
        {
            project = getProject( projects, project.getParent() );
            parents.add( project );
        }

        Collections.reverse( parents );

        parents.add( targetProject );

        parents.forEach( p -> graph.add( ProjectGraphEntry.create().
            name( p.getName() ).
            parent( p.getParent() ).
            build() ) );

        final Queue<Project> children = new ArrayDeque<>();
        children.add( targetProject );

        while ( !children.isEmpty() )
        {
            final Project current = children.poll();
            projects.stream().
                filter( p -> current.getName().equals( p.getParent() ) ).
                forEach( p -> {
                    children.offer( p );
                    graph.add( ProjectGraphEntry.create().
                        name( p.getName() ).
                        parent( p.getParent() ).
                        build() );
                } );
        }

        return graph.build();
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
            LOG.debug( "Project deleted: " + projectName );

            return result;
        }, projectName );
    }

    private boolean doDelete( final ProjectName projectName )
    {
        final DeleteRepositoryParams params = DeleteRepositoryParams.from( projectName.getRepoId() );
        final RepositoryId deletedRepositoryId = this.repositoryService.deleteRepository( params );

        if ( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) )
        {
            DeleteProjectRolesCommand.create().
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
        return GetProjectRolesCommand.create().
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
            LOG.debug( "Project permissions updated: " + projectName );

            return result;


        }, projectName );
    }

    private ProjectPermissions doModifyPermissions( final ProjectName projectName, final ProjectPermissions projectPermissions )
    {
        return UpdateProjectRolesCommand.create().
            projectName( projectName ).
            permissions( projectPermissions ).
            securityService( securityService ).
            build().
            execute();
    }

    private PropertyTree createProjectData( final CreateProjectParams params )
    {
        PropertyTree data = new PropertyTree();

        final PropertySet projectData = data.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );

        projectData.setString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, params.getDescription() );
        projectData.setString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY, params.getDisplayName() );
        if ( params.getParent() != null )
        {
            projectData.setString( ProjectConstants.PROJECT_PARENTS_PROPERTY, params.getParent().toString() );
        }
        return data;
    }

    private PropertyTree modifyProjectData( final ModifyProjectParams params, final PropertyTree data )
    {
        PropertySet projectData = data.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );

        if ( projectData == null )
        {
            projectData = data.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        }

        projectData.setString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, params.getDescription() );
        projectData.setString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY, params.getDisplayName() );

        return data;
    }

    private void setIconData( final PropertySet parent, final CreateAttachment icon )
    {
        parent.removeProperties( ProjectConstants.PROJECT_ICON_PROPERTY );

        if ( icon != null )
        {
            AttachmentSerializer.create( parent, CreateAttachments.from( icon ), ProjectConstants.PROJECT_ICON_PROPERTY );
        }
        else
        {
            parent.addSet( ProjectConstants.PROJECT_ICON_PROPERTY, null );
        }
    }

    private BinaryAttachment createProjectIcon( final CreateAttachment icon, final int size )
        throws IOException
    {

        if ( icon != null )
        {
            final ByteSource source = icon.getByteSource();

            if ( "image/svg+xml".equals( icon.getMimeType() ) )
            {
                return new BinaryAttachment( BinaryReference.from( icon.getName() ), icon.getByteSource() );
            }

            try (InputStream inputStream = source.openStream())
            {
                final BufferedImage bufferedImage = ImageIO.read( inputStream );

                if ( size > 0 && ( bufferedImage.getWidth() >= size ) )
                {
                    final BufferedImage scaledImage = scaleWidth( bufferedImage, size );
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageHelper.writeImage( out, scaledImage, ImageHelper.getFormatByMimeType( icon.getMimeType() ), -1 );

                    return new BinaryAttachment( BinaryReference.from( icon.getName() ), ByteSource.wrap( out.toByteArray() ) );
                }

                return new BinaryAttachment( BinaryReference.from( icon.getName() ), icon.getByteSource() );
            }
        }

        return null;
    }

    private BufferedImage scaleWidth( final BufferedImage source, final int sizeInt )
    {
        final BigDecimal newWidth = new BigDecimal( sizeInt );
        final BigDecimal size = newWidth.setScale( 2, RoundingMode.UP );

        final BigDecimal width = new BigDecimal( source.getWidth() );
        final BigDecimal height = new BigDecimal( source.getHeight() );

        final BigDecimal scale = size.divide( width, RoundingMode.UP );
        final BigDecimal newHeight = height.multiply( scale ).setScale( 0, RoundingMode.UP );

        return ImageHelper.getScaledInstance( source, newWidth.intValue(), newHeight.intValue() );
    }

    private Project getProject( final Projects projects, final ProjectName name )
    {
        return projects.stream().
            filter( project -> project.getName().equals( name ) ).
            findFirst().
            orElseThrow( () -> new ProjectNotFoundException( name ) );
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

    private Context adminContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.
                copyOf( ContextAccessor.current().getAuthInfo() ).
                principals( RoleKeys.ADMIN, RoleKeys.CONTENT_MANAGER_ADMIN ).
                build() ).
            build();
    }
}
