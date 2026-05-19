package com.enonic.xp.core.impl.project;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
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
import com.enonic.xp.attachment.AttachmentNames;
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

                doInitRootNodes( CreateProjectParams.create()
                                     .name( projectName )
                                     .displayName( projectName.toString() )
                                     .build(), null );
            } );
        } );
    }

    private List<Repository> getProjectRepositories( final Repositories repositories )
    {
        return repositories.stream()
            .filter( repository -> ProjectName.from( repository.getId() ) != null )
            .filter( repository -> !repository.getBranches().contains( ContentConstants.BRANCH_DRAFT ) )
            .toList();
    }

    private void doInitRootNodes( final CreateProjectParams params, final @Nullable PropertyTree contentRootData )
    {
        ContentRepoInitializer.create()
            .setIndexService( indexService )
            .repositoryService( repositoryService )
            .internalRepositoryService( internalRepositoryService )
            .repositoryId( params.getName().getRepoId() )
            .repositoryData( new PropertyTree() )
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

    @Override
    public Project modify( ModifyProjectParams params )
    {
        return ProjectPermissionsContextManager.initUpdateContext( params.getName() ).callWith( () -> {
            final Project result = doModify( params );
            LOG.debug( "Project updated: {}", params.getName() );
            return result;
        } );
    }

    @Override
    public Project create( CreateProjectParams params )
    {
        return ProjectPermissionsContextManager.initCreateContext().callWith( () -> {
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

            final Project.Builder project = Project.create()
                .name( params.getName() )
                .displayName( params.getDisplayName() )
                .description( params.getDescription() )
                .language( params.getLanguage() );
            params.getParents().forEach( project::addParent );
            params.getSiteConfigs().forEach( project::addSiteConfig );
            return project.build();
        } );
    }

    @Override
    public void modifyIcon( final ModifyProjectIconParams params )
    {
        final ModifyProjectParams.Builder builder = ModifyProjectParams.create().name( params.getName() ).editor( _ -> {
        } );
        if ( params.getIcon() != null )
        {
            final CreateAttachment scaled =
                params.getScaleWidth() > 0 ? scaleProjectIcon( params.getIcon(), params.getScaleWidth() ) : params.getIcon();
            builder.icon( CreateAttachment.create( scaled ).name( AttachmentNames.THUMBNAIL ).build() );
        }
        else
        {
            builder.removeIcon();
        }
        modify( builder.build() );
    }

    @Override
    public @Nullable ByteSource getIcon( final ProjectName projectName )
    {
        return ProjectPermissionsContextManager.initGetContext( projectName ).callWith( () -> doGetIcon( projectName ) );
    }

    private @Nullable ByteSource doGetIcon( final ProjectName projectName )
    {
        final Node contentRoot = getProjectContentRoot( projectName );
        if ( contentRoot == null )
        {
            return null;
        }
        final BinaryReference thumbnailRef = BinaryReference.from( AttachmentNames.THUMBNAIL );
        if ( contentRoot.getAttachedBinaries().getByBinaryReference( thumbnailRef ) == null )
        {
            return null;
        }
        return contentRootDataContext( projectName ).callWith( () -> nodeService.getBinary( contentRoot.id(), thumbnailRef ) );
    }

    @Override
    public Projects list()
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();

        return ProjectPermissionsContextManager.initListContext()
            .callWith( () -> this.doList()
                .stream()
                .filter( project -> ProjectAccessHelper.hasAccess( authenticationInfo, project.getName(), ProjectRole.values() ) )
                .collect( Projects.collector() ) );
    }

    @Override
    public ApplicationKeys getAvailableApplications( final ProjectName projectName )
    {
        final Project project = get( projectName );

        if ( project == null )
        {
            throw new ProjectNotFoundException( projectName );
        }

        return ProjectPermissionsContextManager.initListContext()
            .callWith( () -> Stream.concat( Stream.of( project ), doGetParents( project ).stream() )
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

        return ProjectPermissionsContextManager.initListContext().callWith( () -> {
            Stream.concat( doGetParents( targetProject ).stream(), Stream.of( targetProject ) )
                .map( p -> ProjectGraphEntry.create().name( p.getName() ).addParents( p.getParents() ).build() )
                .forEach( graph::add );

            final Queue<Project> children = new ArrayDeque<>();
            children.add( targetProject );

            final Projects projects = this.doList();

            while ( !children.isEmpty() )
            {
                final Project current = children.poll();
                projects.stream().filter( p -> p.getParents().contains( current.getName() ) ).forEach( p -> {
                    children.offer( p );
                    graph.add( ProjectGraphEntry.create().name( p.getName() ).addParents( p.getParents() ).build() );
                } );
            }

            return graph.build();
        } );
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
        return ProjectPermissionsContextManager.initGetContext( projectName ).callWith( () -> doGet( projectName ) );
    }

    @Override
    public boolean delete( ProjectName projectName )
    {
        return ProjectPermissionsContextManager.initDeleteContext( projectName ).callWith( () -> {
            final boolean result = doDelete( projectName );
            LOG.debug( "Project deleted: {}", projectName );

            return result;
        } );
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
        return ProjectPermissionsContextManager.initGetContext( projectName )
            .callWith(
                () -> GetProjectRolesCommand.create().securityService( securityService ).projectName( projectName ).build().execute() );
    }

    @Override
    public ProjectPermissions modifyPermissions( final ProjectName projectName, final ProjectPermissions projectPermissions )
    {
        return ProjectPermissionsContextManager.initUpdateContext( projectName ).callWith( () -> {
            final ProjectPermissions result = doModifyPermissions( projectName, projectPermissions );
            LOG.debug( "Project permissions updated: {}", projectName );
            return result;
        } );
    }

    @Override
    public AccessControlList getRootPermissions( final ProjectName projectName )
    {
        return ProjectPermissionsContextManager.initGetContext( projectName ).callWith( () -> doGetRootPermissions( projectName ) );
    }

    private AccessControlList doGetRootPermissions( final ProjectName projectName )
    {
        final Node contentRoot = getProjectContentRootOrThrow( projectName );
        return contentRoot.getPermissions();
    }

    @Override
    public boolean getPublicRead( final ProjectName projectName )
    {
        return ProjectPermissionsContextManager.initGetContext( projectName ).callWith( () -> doGetPublicRead( projectName ) );
    }

    private boolean doGetPublicRead( final ProjectName projectName )
    {
        final Node contentRoot = getProjectContentRootOrThrow( projectName );
        return contentRoot.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ );
    }

    private Node getProjectContentRootOrThrow( final ProjectName projectName )
    {
        final Node contentRoot;
        try
        {
            contentRoot =
                contentRootDataContext( projectName ).callWith( () -> nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) );
        }
        catch ( RepositoryNotFoundException | BranchNotFoundException e )
        {
            throw new ProjectNotFoundException( projectName );
        }
        if ( contentRoot == null )
        {
            throw new ProjectNotFoundException( projectName );
        }
        return contentRoot;
    }

    @Override
    public boolean setPublicRead( final SetProjectPublicReadParams params )
    {
        return ProjectPermissionsContextManager.initUpdateContext( params.getName() ).callWith( () -> doSetPublicRead( params ) );
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

    private static PropertyTree createProjectMarkerData()
    {
        // Empty PROJECT_DATA_SET_NAME set serves as the "this repo is a project" marker on the system repo's
        // repository data node. After the 8->9 migration, all per-project metadata (displayName, description,
        // parents, icon) lives on the project's /content node; only the marker remains here.
        final PropertyTree data = new PropertyTree();
        data.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        return data;
    }

    private CreateAttachment scaleProjectIcon( final CreateAttachment icon, final int targetWidth )
    {
        if ( "image/svg+xml".equals( icon.getMimeType() ) )
        {
            return icon;
        }

        try (InputStream inputStream = icon.getByteSource().openStream())
        {
            final BufferedImage bufferedImage = ImageIO.read( inputStream );

            if ( bufferedImage.getWidth() < targetWidth )
            {
                return icon;
            }

            final int newHeight = Math.max( 1, bufferedImage.getHeight() * targetWidth / bufferedImage.getWidth() );
            final BufferedImage scaledImage = ImageHelper.getScaledInstance( bufferedImage, targetWidth, newHeight );
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageHelper.writeImage( out, scaledImage, ImageHelper.getFormatByMimeType( icon.getMimeType() ), -1 );

            return CreateAttachment.create( icon ).byteSource( ByteSource.wrap( out.toByteArray() ) ).build();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
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

        final Node updatedContentRootNode = updateProjectContentRoot( projectName, editable, params.getIcon(), params.isRemoveIcon() );

        final Project.Builder project = Project.create()
            .name( projectName )
            .displayName( editable.displayName )
            .description( editable.description )
            .language( editable.language );
        current.getParents().forEach( project::addParent );
        requireNonNullElse( editable.siteConfigs, SiteConfigs.empty() ).forEach( project::addSiteConfig );
        buildIcon( project, updatedContentRootNode.data() );
        return project.build();
    }

    private Node updateProjectContentRoot( final ProjectName projectName, final EditableProject editable,
                                           final @Nullable CreateAttachment icon, final boolean removeIcon )
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

            final PropertySet data = requireNonNull( edit.data.getSet( ContentPropertyNames.DATA ), "Content root data set must exist" );
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

            if ( icon != null )
            {
                edit.data.removeProperties( ContentPropertyNames.ATTACHMENT );
                AttachmentSerializer.create( edit.data, CreateAttachments.from( icon ) );
            }
            else if ( removeIcon )
            {
                edit.data.removeProperties( ContentPropertyNames.ATTACHMENT );
            }
        };

        final PatchNodeParams.Builder patchBuilder = PatchNodeParams.create()
            .path( ContentConstants.CONTENT_ROOT_PATH )
            .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
            .editor( editor )
            .versionAttributesResolver( projectVersionAttributesResolver( "project.modify" ) );
        if ( icon != null )
        {
            patchBuilder.attachBinary( icon.getBinaryReference(), icon.getByteSource() );
        }
        final PatchNodeParams patchParams = patchBuilder.build();
        return contentRootDataContext( projectName ).callWith(
            () -> nodeService.patch( patchParams ).getResult( ContextAccessor.current().getBranch() ) );
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

        if ( !params.getParents().isEmpty() )
        {
            contentRootData.addStrings( ProjectConstants.PROJECT_PARENTS_PROPERTY,
                                        params.getParents().stream().map( ProjectName::toString ).toList() );
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

    private @Nullable Project toProject( final @Nullable Repository repository, final @Nullable Node contentRoot )
    {
        if ( repository == null || contentRoot == null )
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

        if ( contentDataSet != null )
        {
            buildParents( project, contentDataSet );
        }
        buildIcon( project, contentRootData );
        Optional.ofNullable( contentDataSet )
            .map( SiteConfigsDataSerializer::fromData )
            .orElse( SiteConfigs.empty() )
            .forEach( project::addSiteConfig );

        return project.build();
    }

    private static void buildParents( final Project.Builder project, final PropertySet contentDataSet )
    {
        for ( final String s : contentDataSet.getStrings( ProjectConstants.PROJECT_PARENTS_PROPERTY ) )
        {
            project.addParent( ProjectName.from( s ) );
        }
    }

    private static void buildIcon( final Project.Builder project, final PropertyTree contentRootData )
    {
        final PropertySet thumbnailSet = contentRootData.getSet( ContentPropertyNames.ATTACHMENT );
        if ( thumbnailSet == null )
        {
            return;
        }
        project.icon( Attachment.create()
                          .name( thumbnailSet.getString( ContentPropertyNames.ATTACHMENT_NAME ) )
                          .label( thumbnailSet.getString( ContentPropertyNames.ATTACHMENT_LABEL ) )
                          .mimeType( thumbnailSet.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) )
                          .size( thumbnailSet.getLong( ContentPropertyNames.ATTACHMENT_SIZE ) )
                          .sha512( thumbnailSet.getString( ContentPropertyNames.ATTACHMENT_SHA512 ) )
                          .build() );
    }

    private static Context contentRootDataContext( final ProjectName projectName )
    {
        return ContextBuilder.create()
            .repositoryId( projectName.getRepoId() )
            .branch( ContentConstants.BRANCH_DRAFT )
            .authInfo( AuthenticationInfo.copyOf( ContextAccessor.current().getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();
    }

    private static Context adminContext()
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
