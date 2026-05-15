package com.enonic.xp.core.impl.project;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentSerializer;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.project.init.ArchiveInitializer;
import com.enonic.xp.core.impl.project.init.ContentInitializer;
import com.enonic.xp.core.impl.project.init.ContentRepoInitializer;
import com.enonic.xp.core.impl.project.init.IssueInitializer;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.ApplyNodePermissionsListener;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.VersionAttributesResolver;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.EditableProject;
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
import com.enonic.xp.project.SetProjectPublicReadParams;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.repository.internal.InternalRepositoryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.vacuum.VacuumConstants;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@NullMarked
public class ProjectServiceImpl
    implements ProjectService
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectServiceImpl.class );

    private final RepositoryService repositoryService;

    private final InternalRepositoryService internalRepositoryService;

    private final IndexService indexService;

    private final NodeService nodeService;

    private final SecurityService securityService;

    private final EventPublisher eventPublisher;

    private final ProjectConfig config;

    public ProjectServiceImpl( final RepositoryService repositoryService, final InternalRepositoryService internalRepositoryService,
                               final IndexService indexService, final NodeService nodeService, final SecurityService securityService,
                               final EventPublisher eventPublisher, final ProjectConfig config )
    {
        this.repositoryService = repositoryService;
        this.internalRepositoryService = internalRepositoryService;
        this.indexService = indexService;
        this.nodeService = nodeService;
        this.securityService = securityService;
        this.eventPublisher = eventPublisher;
        this.config = config;
    }

    public void initialize()
    {
        adminContext().runWith( () -> {

            final Repositories repositories = this.repositoryService.list();

            getProjectRepositories( repositories ).forEach( repository -> {
                final ProjectName projectName = requireNonNull( ProjectName.from( repository.getId() ) );
                final PropertySet projectData = repository.getData().getSet( ProjectConstants.PROJECT_DATA_SET_NAME );

                final String displayName =
                    requireNonNullElse( projectData.getString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY ), projectName.toString() );

                doInitRootNodes( CreateProjectParams.create()
                                     .name( projectName )
                                     .displayName( displayName )
                                     .description( projectData.getString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) )
                                     .build(), null );
            } );
        } );
    }

    private static void buildParents( final Project.Builder project, final PropertySet projectData )
    {
        for ( final String s : projectData.getStrings( ProjectConstants.PROJECT_PARENTS_PROPERTY ) )
        {
            project.addParent( ProjectName.from( s ) );
        }
    }

    private List<Repository> getProjectRepositories( final Repositories repositories )
    {
        return repositories.stream().map( repository -> {
            final ProjectName projectName = ProjectName.from( repository.getId() );
            if ( projectName == null )
            {
                return null;
            }
            final PropertyTree repositoryData = repository.getData();

            final PropertySet projectData = repositoryData.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );

            if ( projectData == null )
            {
                return null;
            }

            return repository;

        } ).filter( Objects::nonNull ).toList();
    }

    private static void buildIcon( final Project.Builder project, final PropertySet projectData )
    {
        final PropertySet iconData = projectData.getSet( ProjectConstants.PROJECT_ICON_PROPERTY );

        if ( iconData != null )
        {
            project.icon( Attachment.create()
                              .name( iconData.getString( ContentPropertyNames.ATTACHMENT_NAME ) )
                              .label( iconData.getString( ContentPropertyNames.ATTACHMENT_LABEL ) )
                              .mimeType( iconData.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) )
                              .size( iconData.getLong( ContentPropertyNames.ATTACHMENT_SIZE ) )
                              .sha512( iconData.getString( ContentPropertyNames.ATTACHMENT_SHA512 ) )
                              .build() );
        }
    }

    private void doInitRootNodes( final CreateProjectParams params, final @Nullable PropertyTree contentRootData )
    {
        ContentRepoInitializer.create()
            .setIndexService( indexService )
            .repositoryService( repositoryService )
            .internalRepositoryService( internalRepositoryService )
            .repositoryId( params.getName().getRepoId() )
            .repositoryData( createProjectData( params ) )
            .forceInitialization( params.isForceInitialization() )
            .build()
            .initialize();

        ContentInitializer.create()
            .setIndexService( indexService )
            .setNodeService( nodeService )
            .repositoryId( params.getName().getRepoId() )
            .setContentData( contentRootData )
            .accessControlList( CreateProjectRootAccessListCommand.create()
                                    .projectName( params.getName() )
                                    .publicRead( params.isPublicRead() )
                                    .build()
                                    .execute() )
            .forceInitialization( params.isForceInitialization() )
            .build()
            .initialize();

        IssueInitializer.create()
            .setIndexService( indexService )
            .setNodeService( nodeService )
            .repositoryId( params.getName().getRepoId() )
            .accessControlList( CreateProjectIssuesAccessListCommand.create().projectName( params.getName() ).build().execute() )
            .forceInitialization( params.isForceInitialization() )
            .build()
            .initialize();

        ArchiveInitializer.create()
            .setIndexService( indexService )
            .setNodeService( nodeService )
            .repositoryId( params.getName().getRepoId() )
            .accessControlList( CreateProjectRootAccessListCommand.create().projectName( params.getName() ).build().execute() )
            .forceInitialization( params.isForceInitialization() )
            .build()
            .initialize();
    }

    private static void buildSiteConfigs( final Project.Builder project, final SiteConfigs siteConfigs )
    {
        siteConfigs.forEach( project::addSiteConfig );
    }

    @Override
    public Project modify( ModifyProjectParams params )
    {
        return callWithUpdateContext( () -> {
            final Project result = doModify( params );
            LOG.debug( "Project updated: {}", params.getName() );

            return result;
        }, params.getName() );
    }

    @Override
    public Project create( CreateProjectParams params )
    {
        return callWithCreateContext( () -> {
            if ( repositoryService.get( params.getName().getRepoId() ) != null )
            {
                throw new ProjectAlreadyExistsException( params.getName() );
            }

            if ( createsCircleDependency( params.getName(), params.getParents() ) )
            {
                throw new ProjectCircleDependencyException( params.getName(), params.getParents() );
            }

            if ( !config.multiInheritance() && params.getParents().size() > 1 )
            {
                throw new ProjectMultipleParentsException( params.getName(), params.getParents() );
            }

            final PropertyTree contentRootData = toContentRootData( params );

            doInitRootNodes( params, contentRootData );

            CreateProjectRolesCommand.create()
                .securityService( securityService )
                .projectName( params.getName() )
                .projectDisplayName( params.getDisplayName() )
                .build()
                .execute();

            eventPublisher.publish( ProjectEvents.created( params.getName() ) );

            LOG.debug( "Project created: {}", params.getName() );

            return Objects.requireNonNull(
                toProject( repositoryService.get( params.getName().getRepoId() ), getProjectContentRoot( params.getName() ) ),
                "Project must exist after create" );
        } );
    }

    @Override
    public void modifyIcon( final ModifyProjectIconParams params )
    {
        callWithUpdateContext( () -> {
            doModifyIcon( params );
            LOG.debug( "Icon for project updated: {}", params.getName() );

            return true;
        }, params.getName() );
    }

    private void doModifyIcon( final ModifyProjectIconParams params )
    {
        final UpdateRepositoryParams updateParams =
            UpdateRepositoryParams.create().repositoryId( params.getName().getRepoId() ).editor( editableRepository -> {

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
            } ).build();

        repositoryService.updateRepository( updateParams );
    }

    @Override
    public @Nullable ByteSource getIcon( final ProjectName projectName )
    {
        return callWithGetContext( () -> doGetIcon( projectName ), projectName );
    }

    private @Nullable ByteSource doGetIcon( final ProjectName projectName )
    {
        return Optional.ofNullable( doGet( projectName ) )
            .map( Project::getIcon )
            .map( Attachment::getBinaryReference )
            .map( binaryReference -> repositoryService.getBinary( projectName.getRepoId(), binaryReference ) )
            .orElse( null );
    }

    @Override
    public Projects list()
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();

        return callWithListContext( () -> {
            final Projects projects = this.doList();

            return projects.stream()
                .filter( project -> ProjectAccessHelper.hasAccess( authenticationInfo, project.getName(), ProjectRole.values() ) )
                .collect( Projects.collector() );
        } );
    }

    @Override
    public ApplicationKeys getAvailableApplications( final ProjectName projectName )
    {
        final Project project = get( projectName );

        if ( project == null )
        {
            throw new ProjectNotFoundException( projectName );
        }

        return callWithListContext( () -> Stream.concat( Stream.of( project ), doGetParents( project ).stream() )
            .map( Project::getSiteConfigs )
            .flatMap( SiteConfigs::stream )
            .map( SiteConfig::getApplicationKey )
            .collect( ApplicationKeys.collector() ) );
    }

    @Override
    public ProjectGraph graph( final ProjectName projectName )
    {
        return doGraph( projectName );
    }

    private ProjectGraph doGraph( final ProjectName projectName )
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

        callWithListContext( () -> {
            Stream.concat( doGetParents( targetProject ).stream(), Stream.of( targetProject ) )
                .map( p -> ProjectGraphEntry.create().name( p.getName() ).addParents( p.getParents() ).build() )
                .forEach( graph::add );

            final Queue<Project> children = new ArrayDeque<>();
            children.add( targetProject );

            final Projects projects = adminContext().callWith( this::doList );

            while ( !children.isEmpty() )
            {
                final Project current = children.poll();
                projects.stream().filter( p -> p.getParents().contains( current.getName() ) ).forEach( p -> {
                    children.offer( p );
                    graph.add( ProjectGraphEntry.create().name( p.getName() ).addParents( p.getParents() ).build() );
                } );
            }

            return null;
        } );

        return graph.build();
    }

    private Set<Project> doGetParents( final Project project )
    {
        final Set<Project> parents = new LinkedHashSet<>();
        addDirectParents( project, parents );

        return parents;
    }

    private void addDirectParents( final Project project, final Set<Project> result )
    {
        project.getParents().stream().map( this::doGet ).forEach( parent -> {
            if ( parent != null )
            {
                addDirectParents( parent, result );
                result.add( parent );
            }
        } );
    }

    @Override
    public @Nullable Project get( final ProjectName projectName )
    {
        return callWithGetContext( () -> doGet( projectName ), projectName );
    }

    @Override
    public boolean delete( ProjectName projectName )
    {
        return callWithDeleteContext( () -> {
            final boolean result = doDelete( projectName );
            LOG.debug( "Project deleted: {}", projectName );

            return result;
        }, projectName );
    }

    private boolean doDelete( final ProjectName projectName )
    {
        final DeleteRepositoryParams params = DeleteRepositoryParams.from( projectName.getRepoId() );
        final RepositoryId deletedRepositoryId = this.repositoryService.deleteRepository( params );

        DeleteProjectRolesCommand.create().securityService( securityService ).projectName( projectName ).build().execute();

        return deletedRepositoryId != null;
    }

    @Override
    public ProjectPermissions getPermissions( final ProjectName projectName )
    {
        return callWithGetContext( () -> doGetPermissions( projectName ), projectName );
    }

    private ProjectPermissions doGetPermissions( final ProjectName projectName )
    {
        return GetProjectRolesCommand.create().securityService( securityService ).projectName( projectName ).build().execute();
    }

    @Override
    public ProjectPermissions modifyPermissions( final ProjectName projectName, final ProjectPermissions projectPermissions )
    {
        return callWithUpdateContext( () -> {

            final ProjectPermissions result = doModifyPermissions( projectName, projectPermissions );
            LOG.debug( "Project permissions updated: {}", projectName );

            return result;


        }, projectName );
    }

    @Override
    public AccessControlList getRootPermissions( final ProjectName projectName )
    {
        return callWithGetContext( () -> doGetRootPermissions( projectName ), projectName );
    }

    private AccessControlList doGetRootPermissions( final ProjectName projectName )
    {
        try
        {
            final Node contentRoot =
                contentRootDataContext( projectName ).callWith( () -> nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) );
            return contentRoot != null ? contentRoot.getPermissions() : AccessControlList.empty();
        }
        catch ( RepositoryNotFoundException | BranchNotFoundException e )
        {
            return AccessControlList.empty();
        }
    }

    @Override
    public boolean getPublicRead( final ProjectName projectName )
    {
        return callWithGetContext( () -> doGetPublicRead( projectName ), projectName );
    }

    private boolean doGetPublicRead( final ProjectName projectName )
    {
        try
        {
            final Node contentRoot =
                contentRootDataContext( projectName ).callWith( () -> nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) );
            return contentRoot != null && contentRoot.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ );
        }
        catch ( RepositoryNotFoundException | BranchNotFoundException e )
        {
            return false;
        }
    }

    @Override
    public boolean setPublicRead( final SetProjectPublicReadParams params )
    {
        return callWithUpdateContext( () -> doSetPublicRead( params ), params.getName() );
    }

    private boolean doSetPublicRead( final SetProjectPublicReadParams params )
    {
        final boolean publicRead = params.isPublicRead();
        final ApplyPermissionsListener listener = params.getListener();

        return contentRootDataContext( params.getName() ).callWith( () -> {
            final Node contentRoot = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );
            if ( contentRoot == null )
            {
                return false;
            }

            final AccessControlList currentPermissions = contentRoot.getPermissions();
            final AccessControlList newPermissions = publicRead
                ? AccessControlList.create( currentPermissions )
                  .add( AccessControlEntry.create()
                        .principal( RoleKeys.EVERYONE )
                        .allow( Permission.READ )
                        .build() )
                  .build()
                : AccessControlList.create( currentPermissions ).remove( RoleKeys.EVERYONE ).build();

            final ApplyNodePermissionsParams.Builder paramsBuilder = ApplyNodePermissionsParams.create()
                .nodeId( contentRoot.id() )
                .permissions( newPermissions )
                .scope( ApplyPermissionsScope.TREE )
                .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                .versionAttributesResolver( projectVersionAttributesResolver( "content.permissions" ) );

            if ( listener != null )
            {
                paramsBuilder.applyPermissionsListener( new ApplyPermissionsListenerAdapter( listener ) );
            }

            nodeService.applyPermissions( paramsBuilder.build() );

            return publicRead;
        } );
    }

    private static final class ApplyPermissionsListenerAdapter
        implements ApplyNodePermissionsListener
    {
        private final ApplyPermissionsListener delegate;

        ApplyPermissionsListenerAdapter( final ApplyPermissionsListener delegate )
        {
            this.delegate = delegate;
        }

        @Override
        public void setTotal( final int count )
        {
            delegate.setTotal( count );
        }

        @Override
        public void permissionsApplied( final int count )
        {
            delegate.permissionsApplied( count );
        }

        @Override
        public void notEnoughRights( final int count )
        {
            delegate.notEnoughRights( count );
        }
    }

    private ProjectPermissions doModifyPermissions( final ProjectName projectName, final ProjectPermissions projectPermissions )
    {
        return UpdateProjectRolesCommand.create()
            .projectName( projectName )
            .permissions( projectPermissions )
            .securityService( securityService )
            .build()
            .execute();
    }

    private PropertyTree createProjectData( final CreateProjectParams params )
    {
        PropertyTree data = new PropertyTree();

        final PropertySet projectData = data.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );

        if ( !params.getParents().isEmpty() )
        {
            projectData.addStrings( ProjectConstants.PROJECT_PARENTS_PROPERTY,
                                    params.getParents().stream().map( ProjectName::toString ).collect( Collectors.toList() ) );
        }

        return data;
    }

    private void setIconData( final PropertySet parent, final @Nullable CreateAttachment icon )
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

    private <T> T callWithCreateContext( final Callable<T> runnable )
    {
        return ProjectPermissionsContextManager.initCreateContext().callWith( runnable );
    }

    private <T> T callWithUpdateContext( final Callable<T> runnable, final ProjectName projectName )
    {
        return ProjectPermissionsContextManager.initUpdateContext( projectName ).callWith( runnable );
    }

    private <T> T callWithGetContext( final Callable<T> runnable, final ProjectName projectName )
    {
        return ProjectPermissionsContextManager.initGetContext( projectName ).callWith( runnable );
    }

    private <T> T callWithListContext( final Callable<T> runnable )
    {
        return ProjectPermissionsContextManager.initListContext().callWith( runnable );
    }

    private <T> T callWithDeleteContext( final Callable<T> runnable, final ProjectName projectName )
    {
        return ProjectPermissionsContextManager.initDeleteContext( projectName ).callWith( runnable );
    }

    private Project doModify( final ModifyProjectParams params )
    {
        final ProjectName projectName = params.getName();

        final Repository repository = repositoryService.get( projectName.getRepoId() );
        if ( repository == null )
        {
            throw new ProjectNotFoundException( projectName );
        }

        final Node currentContentRoot = getProjectContentRoot( projectName );
        if ( currentContentRoot == null )
        {
            throw new ProjectNotFoundException( projectName );
        }
        final Project current = toProject( repository, currentContentRoot );
        if ( current == null )
        {
            throw new ProjectNotFoundException( projectName );
        }

        final EditableProject editable = new EditableProject( current );
        params.getEditor().edit( editable );

        Preconditions.checkArgument( editable.displayName != null && !editable.displayName.isBlank(),
                                     "displayName must not be null or blank" );

        UpdateProjectRoleNamesCommand.create()
            .securityService( securityService )
            .projectName( projectName )
            .projectDisplayName( editable.displayName )
            .build()
            .execute();

        final Node updatedContentRootNode = updateProjectContentRoot( projectName, editable );

        return Objects.requireNonNull( toProject( repository, updatedContentRootNode ), "Project must exist after modify" );
    }

    private Node updateProjectContentRoot( final ProjectName projectName, final EditableProject editable )
    {
        final NodeEditor editor = edit -> {
            if ( editable.displayName != null )
            {
                edit.data.setString( ContentPropertyNames.DISPLAY_NAME, editable.displayName );
            }
            else
            {
                edit.data.removeProperties( ContentPropertyNames.DISPLAY_NAME );
            }
            if ( editable.language != null )
            {
                edit.data.setString( ContentPropertyNames.LANGUAGE, editable.language.toLanguageTag() );
            }
            else
            {
                edit.data.removeProperties( ContentPropertyNames.LANGUAGE );
            }
            final PropertySet data = edit.data.getSet( ContentPropertyNames.DATA );
            if ( data == null )
            {
                throw new IllegalStateException( "Cannot update project config" );
            }
            if ( editable.description != null )
            {
                data.setString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, editable.description );
            }
            else
            {
                data.removeProperties( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY );
            }
            data.removeProperties( ContentPropertyNames.SITECONFIG );
            SiteConfigsDataSerializer.toData( requireNonNullElse( editable.siteConfigs, SiteConfigs.empty() ), data );
        };
        final PatchNodeParams params = PatchNodeParams.create()
            .path( ContentConstants.CONTENT_ROOT_PATH )
            .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
            .editor( editor )
            .versionAttributesResolver( projectVersionAttributesResolver( "project.modify" ) )
            .build();
        return contentRootDataContext( projectName ).callWith(
            () -> nodeService.patch( params ).getResult( ContextAccessor.current().getBranch() ) );
    }

    private static VersionAttributesResolver projectVersionAttributesResolver( final String actionKey )
    {
        return ( _, _, _, _ ) -> Attributes.create()
            .attribute( actionKey, GenericValue.newObject()
                .put( "user", ContextAccessor.current().getAuthInfo().getUser().getKey().toString() )
                .put( "optime", Millis.now().toString() )
                .build() )
            .attribute( VacuumConstants.VACUUM_SKIP_ATTRIBUTE, GenericValue.newObject().build() )
            .build();
    }

    private Projects doList()
    {
        return this.repositoryService.list().stream().map( repository -> {
            final ProjectName projectName = ProjectName.from( repository.getId() );
            return projectName != null ? toProject( repository, getProjectContentRoot( projectName ) ) : null;
        } ).filter( Objects::nonNull ).collect( Projects.collector() );
    }

    private @Nullable Project doGet( final ProjectName projectName )
    {
        return toProject( this.repositoryService.get( projectName.getRepoId() ), getProjectContentRoot( projectName ) );
    }

    private PropertyTree toContentRootData( final CreateProjectParams params )
    {
        PropertyTree data = new PropertyTree();

        data.setString( ContentPropertyNames.DISPLAY_NAME, params.getDisplayName() );

        if ( params.getLanguage() != null )
        {
            data.setString( ContentPropertyNames.LANGUAGE, params.getLanguage().toLanguageTag() );
        }

        final PropertySet contentRootData = data.addSet( ContentPropertyNames.DATA );

        if ( params.getDescription() != null )
        {
            contentRootData.setString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, params.getDescription() );
        }

        if ( !params.getSiteConfigs().isEmpty() )
        {
            SiteConfigsDataSerializer.toData( params.getSiteConfigs(), contentRootData );
        }

        return data;
    }

    private @Nullable Node getProjectContentRoot( final ProjectName projectName )
    {
        try
        {
            return contentRootDataContext( projectName ).callWith( () -> nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) );
        }
        catch ( RepositoryNotFoundException | BranchNotFoundException e )
        {
            return null;
        }
    }

    private SiteConfigs toProjectSiteConfigs( final PropertyTree contentRootData )
    {
        return Optional.ofNullable( contentRootData.getSet( ContentPropertyNames.DATA ) )
            .map( SiteConfigsDataSerializer::fromData )
            .orElse( SiteConfigs.empty() );
    }

    private @Nullable Project toProject( final @Nullable Repository repository, final @Nullable Node contentRoot )
    {
        if ( repository == null || contentRoot == null )
        {
            return null;
        }

        final PropertyTree repositoryData = repository.getData();

        final PropertySet projectData = repositoryData.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );

        if ( projectData == null )
        {
            return null;
        }

        final ProjectName projectName = ProjectName.from( repository.getId() );
        if ( projectName == null )
        {
            return null;
        }

        final PropertyTree contentRootData = contentRoot.data();
        final PropertySet contentDataSet = contentRootData.getSet( ContentPropertyNames.DATA );
        final String languageTag = contentRootData.getString( ContentPropertyNames.LANGUAGE );

        final Project.Builder project = Project.create()
            .name( projectName )
            .displayName( contentRootData.getString( ContentPropertyNames.DISPLAY_NAME ) )
            .description( contentDataSet != null ? contentDataSet.getString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) : null )
            .language( languageTag != null ? Locale.forLanguageTag( languageTag ) : null );

        buildParents( project, projectData );
        buildIcon( project, projectData );
        buildSiteConfigs( project, toProjectSiteConfigs( contentRootData ) );

        return project.build();
    }

    private Context contentRootDataContext( final ProjectName projectName )
    {
        return ContextBuilder.create()
            .repositoryId( projectName.getRepoId() )
            .branch( ContentConstants.BRANCH_DRAFT )
            .authInfo( AuthenticationInfo.copyOf( ContextAccessor.current().getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();
    }

    private Context adminContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.copyOf( ContextAccessor.current().getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();
    }

    private boolean createsCircleDependency( final ProjectName projectName, final List<ProjectName> parentNames )
    {
        if ( parentNames.contains( projectName ) )
        {
            return true;
        }

        for ( ProjectName parentName : parentNames )
        {
            final Project project = this.doGet( parentName );
            if ( project == null )
            {
                continue;
            }

            final List<ProjectName> parents = project.getParents();

            if ( createsCircleDependency( projectName, parents ) )
            {
                return true;
            }
        }

        return false;
    }

}
