package com.enonic.xp.core.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.AdditionalAnswers;
import org.osgi.framework.Bundle;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.content.ContentAuditLogFilterService;
import com.enonic.xp.core.impl.content.ContentAuditLogSupportImpl;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.core.impl.media.MediaInfoServiceImpl;
import com.enonic.xp.core.impl.project.ProjectConfig;
import com.enonic.xp.core.impl.project.ProjectPermissionsContextManagerImpl;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
import com.enonic.xp.core.impl.schema.content.ContentTypeServiceImpl;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.core.impl.site.SiteServiceImpl;
import com.enonic.xp.core.internal.concurrent.RecurringJob;
import com.enonic.xp.core.internal.osgi.OsgiSupportMock;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.form.Form;
import com.enonic.xp.impl.task.LocalTaskManagerImpl;
import com.enonic.xp.impl.task.TaskManagerCleanupScheduler;
import com.enonic.xp.impl.task.TaskServiceImpl;
import com.enonic.xp.impl.task.script.NamedTaskFactory;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractContentSynchronizerTest
    extends AbstractNodeTest
{
    protected static final User REPO_TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "repo-test-user" ) ).login( "repo-test-user" ).build();

    protected static final AuthenticationInfo REPO_TEST_ADMIN_USER_AUTHINFO = AuthenticationInfo.create()
        .principals( RoleKeys.AUTHENTICATED )
        .principals( RoleKeys.ADMIN )
        .user( REPO_TEST_DEFAULT_USER )
        .build();

    protected ProjectServiceImpl projectService;

    protected ContentServiceImpl contentService;

    protected MediaInfoServiceImpl mediaInfoService;

    protected PageDescriptorService pageDescriptorService;

    protected PartDescriptorService partDescriptorService;

    protected LayoutDescriptorService layoutDescriptorService;

    protected ContentTypeServiceImpl contentTypeService;

    protected TaskServiceImpl taskService;

    protected Context projectContext;

    protected Context secondProjectContext;

    protected Context layerContext;

    protected Context childLayerContext;

    protected Context secondChildLayerContext;

    protected Context mixedChildLayerContext;

    protected Context projectArchiveContext;

    protected Context layerArchiveContext;

    protected Context childLayerArchiveContext;

    protected Project project;

    protected Project secondProject;

    protected Project nonRelatedProject;

    protected Project layer;

    protected Project childLayer;

    protected Project secondChildLayer;

    protected Project mixedChildLayer;

    protected static Context adminContext()
    {
        return ContextBuilder.create()
            .branch( "master" )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( REPO_TEST_ADMIN_USER_AUTHINFO )
            .build();
    }

    public AbstractContentSynchronizerTest()
    {
        super( true );
    }

    @BeforeEach
    void setUpAbstractContentSynchronizerTest()
    {
        setUpProjectService();
        setUpContentService();
        setupTaskService();
    }

    private void setUpProjectService()
    {
        adminContext().runWith( () -> {
            final SecurityConfig securityConfig = mock( SecurityConfig.class );
            when( securityConfig.auditlog_enabled() ).thenReturn( true );

            final ProjectConfig projectConfig = mock( ProjectConfig.class );
            when( projectConfig.multiInheritance() ).thenReturn( true );

            final AuditLogService auditLogService = mock( AuditLogService.class );

            final SecurityAuditLogSupportImpl securityAuditLogSupport = new SecurityAuditLogSupportImpl( auditLogService );
            securityAuditLogSupport.activate( securityConfig );

            final SecurityServiceImpl securityService = new SecurityServiceImpl( this.nodeService, securityAuditLogSupport );

            SecurityInitializer.create()
                .setIndexService( indexService )
                .setSecurityService( securityService )
                .setNodeService( nodeService )
                .build()
                .initialize();

            projectService = new ProjectServiceImpl( repositoryService, indexService, nodeService, securityService,
                                                     new ProjectPermissionsContextManagerImpl(), eventPublisher, projectConfig );

            project = projectService.create( CreateProjectParams.create()
                                                 .name( ProjectName.from( "source_project" ) )
                                                 .parent( null ) // old project-lib sets parent to null for root projects
                                                 .displayName( "Source Project" )
                                                 .build() );

            secondProject = projectService.create(
                CreateProjectParams.create().name( ProjectName.from( "source_project2" ) ).displayName( "Source Project 2" ).build() );

            nonRelatedProject = projectService.create( CreateProjectParams.create()
                                                           .name( ProjectName.from( "another_project" ) )
                                                           .displayName( "Another Source Project" )
                                                           .build() );

            layer = projectService.create( CreateProjectParams.create()
                                               .name( ProjectName.from( "target_project" ) )
                                               .displayName( "Target Project" )
                                               .addParents( List.of( project.getName(), secondProject.getName() ) )
                                               .build() );

            childLayer = projectService.create( CreateProjectParams.create()
                                                    .name( ProjectName.from( "child_layer" ) )
                                                    .displayName( "Child Layer" )
                                                    .parent( project.getName() )
                                                    .build() );

            secondChildLayer = projectService.create( CreateProjectParams.create()
                                                          .name( ProjectName.from( "second_child_layer" ) )
                                                          .displayName( "Second Child Layer" )
                                                          .parent( project.getName() )
                                                          .build() );

            mixedChildLayer = projectService.create( CreateProjectParams.create()
                                                         .name( ProjectName.from( "mixed_child_layer" ) )
                                                         .displayName( "Mixed Child Layer" )
                                                         .addParents( List.of( childLayer.getName(), secondChildLayer.getName() ) )
                                                         .build() );

            this.projectContext = ContextBuilder.from( ContextAccessor.current() )
                .repositoryId( project.getName().getRepoId() )
                .branch( ContentConstants.BRANCH_DRAFT )
                .authInfo( REPO_TEST_ADMIN_USER_AUTHINFO )
                .build();

            this.secondProjectContext = ContextBuilder.from( ContextAccessor.current() )
                .repositoryId( secondProject.getName().getRepoId() )
                .branch( ContentConstants.BRANCH_DRAFT )
                .authInfo( REPO_TEST_ADMIN_USER_AUTHINFO )
                .build();

            this.layerContext = ContextBuilder.from( ContextAccessor.current() )
                .repositoryId( layer.getName().getRepoId() )
                .branch( ContentConstants.BRANCH_DRAFT )
                .authInfo( REPO_TEST_ADMIN_USER_AUTHINFO )
                .build();

            this.childLayerContext = ContextBuilder.from( ContextAccessor.current() )
                .repositoryId( childLayer.getName().getRepoId() )
                .branch( ContentConstants.BRANCH_DRAFT )
                .authInfo( REPO_TEST_ADMIN_USER_AUTHINFO )
                .build();

            this.secondChildLayerContext = ContextBuilder.from( ContextAccessor.current() )
                .repositoryId( secondChildLayer.getName().getRepoId() )
                .branch( ContentConstants.BRANCH_DRAFT )
                .authInfo( REPO_TEST_ADMIN_USER_AUTHINFO )
                .build();

            this.mixedChildLayerContext = ContextBuilder.from( ContextAccessor.current() )
                .repositoryId( mixedChildLayer.getName().getRepoId() )
                .branch( ContentConstants.BRANCH_DRAFT )
                .authInfo( REPO_TEST_ADMIN_USER_AUTHINFO )
                .build();

            this.projectArchiveContext =
                ContextBuilder.from( this.projectContext ).attribute( CONTENT_ROOT_PATH_ATTRIBUTE, new NodePath( "/archive" ) ).build();

            this.layerArchiveContext =
                ContextBuilder.from( this.layerContext ).attribute( CONTENT_ROOT_PATH_ATTRIBUTE, new NodePath( "/archive" ) ).build();

            this.childLayerArchiveContext =
                ContextBuilder.from( this.childLayerContext ).attribute( CONTENT_ROOT_PATH_ATTRIBUTE, new NodePath( "/archive" ) ).build();

            projectService.initialize();
        } );
    }

    private void setUpContentService()
    {
        final Map<String, List<String>> metadata = new HashMap<>();
        metadata.put( HttpHeaders.CONTENT_TYPE, List.of( "image/jpeg" ) );

        final ExtractedData extractedData = ExtractedData.create().metadata( metadata ).build();

        final BinaryExtractor extractor = mock( BinaryExtractor.class );
        when( extractor.extract( isA( ByteSource.class ) ) ).thenReturn( extractedData );

        mediaInfoService = new MediaInfoServiceImpl();
        mediaInfoService.setBinaryExtractor( extractor );

        XDataService xDataService = mock( XDataService.class );

        MixinService mixinService = mock( MixinService.class );
        when( mixinService.inlineFormItems( isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        pageDescriptorService = mock( PageDescriptorService.class );
        partDescriptorService = mock( PartDescriptorService.class );
        layoutDescriptorService = mock( LayoutDescriptorService.class );

        contentTypeService = new ContentTypeServiceImpl( null, null, mixinService );

        final ResourceService resourceService = mock( ResourceService.class );
        final SiteServiceImpl siteService = new SiteServiceImpl();
        siteService.setResourceService( resourceService );
        siteService.setMixinService( mixinService );

        final ContentAuditLogFilterService contentAuditLogFilterService = mock( ContentAuditLogFilterService.class, invocation -> true );

        final AuditLogService auditLogService = mock( AuditLogService.class );
        final ContentConfig contentConfig = mock( ContentConfig.class );

        final ContentAuditLogSupportImpl contentAuditLogSupport =
            new ContentAuditLogSupportImpl( contentConfig, Runnable::run, auditLogService, contentAuditLogFilterService );

        final ContentConfig config = mock( ContentConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        contentService =
            new ContentServiceImpl( nodeService, pageDescriptorService, partDescriptorService, layoutDescriptorService, config );
        contentService.setEventPublisher( eventPublisher );
        contentService.setMediaInfoService( mediaInfoService );
        contentService.setSiteService( siteService );
        contentService.setContentTypeService( contentTypeService );
        contentService.setxDataService( xDataService );
        contentService.setFormDefaultValuesProcessor( ( form, data ) -> {
        } );
        contentService.setContentAuditLogSupport( contentAuditLogSupport );
    }

    private void setupTaskService()
    {
        final Bundle bundle = OsgiSupportMock.mockBundle();
        when( bundle.getSymbolicName() ).thenReturn( "testBundle" );

        final LocalTaskManagerImpl taskMan = new LocalTaskManagerImpl( Runnable::run, new TaskManagerCleanupSchedulerMock(), event -> {
        } );
        taskMan.activate();

        NamedTaskFactory namedTaskFactory = mock( NamedTaskFactory.class );

        this.taskService = new TaskServiceImpl( taskMan, namedTaskFactory );
    }

    private static class TaskManagerCleanupSchedulerMock
        implements TaskManagerCleanupScheduler
    {

        @Override
        public RecurringJob scheduleWithFixedDelay( final Runnable command )
        {
            command.run();
            return mock( RecurringJob.class );
        }
    }

    protected Content createContent( final ContentPath parent )
    {
        return createContent( parent, "name" );
    }

    protected Content createContent( final ContentPath parent, final String name )
    {
        final PropertyTree data = new PropertyTree();
        data.addStrings( "stringField", "stringValue" );

        final CreateContentParams createParent = CreateContentParams.create()
            .contentData( data )
            .name( name )
            .displayName( name )
            .parent( parent )
            .type( ContentTypeName.folder() )
            .build();

        return this.contentService.create( createParent );
    }

    protected Media createMedia( final String name, final ContentPath parentPath )
        throws IOException
    {
        final CreateMediaParams params = new CreateMediaParams().byteSource( loadImage( "cat-small.jpg" ) )
            .name( "cat-small.jpg" )
            .mimeType( "image/jpeg" )
            .parent( ContentPath.ROOT );

        params.name( name ).parent( parentPath );

        return (Media) this.contentService.create( params );
    }

    protected ByteSource loadImage( final String name )
        throws IOException
    {
        final InputStream imageStream = this.getClass().getResourceAsStream( name );

        return ByteSource.wrap( ByteStreams.toByteArray( imageStream ) );
    }

    protected void compareSynched( final Content sourceContent, final Content targetContent )
    {
        assertEquals( sourceContent.getId(), targetContent.getId() );
        assertEquals( sourceContent.getName(), targetContent.getName() );
        assertEquals( sourceContent.getDisplayName(), targetContent.getDisplayName() );
        assertEquals( sourceContent.getData(), targetContent.getData() );
        assertEquals( sourceContent.getPath(), targetContent.getPath() );
        assertEquals( sourceContent.getAllExtraData(), targetContent.getAllExtraData() );
        assertEquals( sourceContent.getAttachments(), targetContent.getAttachments() );
        assertEquals( sourceContent.getOwner(), targetContent.getOwner() );
        assertEquals( sourceContent.getLanguage(), targetContent.getLanguage() );
        assertEquals( sourceContent.getWorkflowInfo(), targetContent.getWorkflowInfo() );
        assertEquals( sourceContent.getPage(), targetContent.getPage() );
        assertEquals( sourceContent.isValid(), targetContent.isValid() );
        assertEquals( sourceContent.inheritsPermissions(), targetContent.inheritsPermissions() );
        assertEquals( sourceContent.getCreatedTime(), targetContent.getCreatedTime() );

        assertNotEquals( sourceContent.getPermissions(), targetContent.getPermissions() );

        assertTrue( targetContent.getInherit().containsAll( EnumSet.allOf( ContentInheritType.class ) ) );
    }
}
