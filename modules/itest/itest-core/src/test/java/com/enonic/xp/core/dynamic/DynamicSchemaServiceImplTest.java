package com.enonic.xp.core.dynamic;

import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.ApplicationHelper;
import com.enonic.xp.core.impl.app.ApplicationRepoInitializer;
import com.enonic.xp.core.impl.app.CreateDynamicCmsParams;
import com.enonic.xp.core.impl.app.DynamicSchemaServiceImpl;
import com.enonic.xp.core.impl.app.SchemaResourceNames;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.project.ProjectConfig;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
import com.enonic.xp.core.impl.security.PasswordSecurityService;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.itest.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.NodeServiceImpl;
import com.enonic.xp.repo.impl.node.dao.NodeVersionServiceImpl;
import com.enonic.xp.repo.impl.repository.NodeRepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryEntryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.SystemRepoInitializer;
import com.enonic.xp.repo.impl.search.NodeSearchServiceImpl;
import com.enonic.xp.repo.impl.storage.IndexDataServiceImpl;
import com.enonic.xp.repo.impl.storage.NodeStorageServiceImpl;
import com.enonic.xp.repo.impl.version.VersionServiceImpl;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.CreateDynamicPhrasesParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.GetDynamicPhrasesParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.SetDynamicComponentIconParams;
import com.enonic.xp.resource.SetDynamicContentSchemaIconParams;
import com.enonic.xp.resource.SetDynamicMacroIconParams;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicMacroParams;
import com.enonic.xp.resource.UpdateDynamicPhrasesParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.SchemaNotFoundException;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DynamicSchemaServiceImplTest
    extends AbstractElasticsearchIntegrationTest
{
    NodeServiceImpl nodeService;

    private DynamicSchemaServiceImpl dynamicSchemaService;

    private ProjectServiceImpl projectService;

    private static final String CMS_DESCRIPTOR_DEFAULT_VALUE = """
        kind: "CMS"
        mixins: [ ]
        form: [ ]
        """;

    private static Context ctxDefault()
    {
        return ContextBuilder.copyOf( ContextAccessor.current() ).build();
    }

    private static Context createAdminContext()
    {
        return ContextBuilder.copyOf( ctxDefault() )
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED, RoleKeys.ADMIN ).user( User.anonymous() ).build() )
            .build();
    }

    private static Context createSchemaAdminContext()
    {
        return ContextBuilder.copyOf( ctxDefault() )
            .authInfo(
                AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED, RoleKeys.SCHEMA_ADMIN ).user( User.anonymous() ).build() )
            .build();
    }

    // admin context of system-repo, where the schema of an application is persisted below /applications/<app>/cms
    private static Context appRepoAdminContext()
    {
        return ContextBuilder.copyOf( createAdminContext() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .build();
    }

    // the dynamic schema service writes under /applications/<app>/cms/<kind>, and only creates the innermost folder itself
    private void createApplicationNodes( final ApplicationKey applicationKey )
    {
        ApplicationHelper.runAsAdmin( () -> {
            final NodePath appPath = createFolderNode( new NodePath( "/applications" ), applicationKey.toString() );
            final NodePath cmsPath = createFolderNode( appPath, SchemaResourceNames.CMS_ROOT_NAME );

            for ( final String name : List.of( SchemaResourceNames.CONTENT_TYPE_ROOT_NAME, SchemaResourceNames.PART_ROOT_NAME,
                                               SchemaResourceNames.LAYOUT_ROOT_NAME, SchemaResourceNames.PAGE_ROOT_NAME,
                                               SchemaResourceNames.FORM_FRAGMENTS_ROOT_NAME, SchemaResourceNames.MIXINS_ROOT_NAME ) )
            {
                createFolderNode( cmsPath, name );
            }

            nodeService.refresh( RefreshMode.ALL );
        } );
    }

    private NodePath createFolderNode( final NodePath parent, final String name )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( name )
                                       .parent( parent )
                                       .inheritPermissions( true )
                                       .build() ).path();
    }

    @BeforeEach
    void initService()
        throws Exception
    {
        deleteAllIndices();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        BinaryServiceImpl binaryService = new BinaryServiceImpl( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl( client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl( executorService );

        final SearchDaoImpl searchDao = new SearchDaoImpl( client );

        BranchServiceImpl branchService = new BranchServiceImpl( storageDao, searchDao );

        VersionServiceImpl versionService = new VersionServiceImpl( storageDao );

        CommitServiceImpl commitService = new CommitServiceImpl( storageDao );

        IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl( client );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( blobStore, new RepoConfiguration( Map.of() ) );

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl( storageDao );

        NodeSearchServiceImpl searchService = new NodeSearchServiceImpl( searchDao );

        NodeStorageServiceImpl storageService =
            new NodeStorageServiceImpl( versionService, branchService, commitService, nodeDao, indexedDataService );

        final RepositoryEntryServiceImpl repositoryEntryService =
            new RepositoryEntryServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        IndexServiceImpl indexService =
            new IndexServiceImpl( indexServiceInternal, indexedDataService, searchService, nodeDao, repositoryEntryService );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl( indexServiceInternal );

        RepositoryServiceImpl repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, nodeRepositoryService, storageService, searchService, branchService,
                                       () -> null );
        SystemRepoInitializer.create()
            .setIndexServiceInternal( indexServiceInternal )
            .setNodeStorageService( storageService )
            .setRepositoryEntryService( repositoryEntryService )
            .setNodeRepositoryService( nodeRepositoryService )
            .build()
            .initialize();

        nodeService = new NodeServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        ApplicationRepoInitializer.create().setIndexService( indexService ).setNodeService( nodeService ).build().initialize();

        this.dynamicSchemaService = new DynamicSchemaServiceImpl( nodeService );

        final SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocationOnMock -> invocationOnMock.getMethod().getDefaultValue() ) );

        final SecurityAuditLogSupportImpl securityAuditLogSupport = new SecurityAuditLogSupportImpl( mock( AuditLogService.class ) );
        securityAuditLogSupport.activate( securityConfig );

        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService();
        passwordSecurityService.activate( securityConfig );

        SecurityServiceImpl securityService = new SecurityServiceImpl( nodeService, securityAuditLogSupport, passwordSecurityService );
        SecurityInitializer.create()
            .setIndexService( indexService )
            .setSecurityService( securityService )
            .setNodeService( nodeService )
            .build()
            .initialize();

        createApplicationNodes( ApplicationKey.from( "myapp" ) );
        createApplicationNodes( ApplicationKey.from( "my_other_app" ) );

        projectService =
            new ProjectServiceImpl( repositoryService, repositoryService, indexService, nodeService, securityService, eventPublisher,
                                    mock( ProjectConfig.class ) );
        projectService.initialize();

        createAdminContext().runWith( () -> projectService.create(
            CreateProjectParams.create().name( ProjectName.from( "my-project" ) ).displayName( "test" ).build() ) );

    }

    @Test
    void createContentTypeSchema()
        throws Exception
    {
        final String resource = readResource( "_contentType.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<ContentType> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createContentType( params ) );

        final ContentType contentType = (ContentType) result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ).getSchema() ) );

        assertEquals( "myapp:mytype", contentType.getName().toString() );
        assertEquals( "Virtual Content Type", contentType.getTitle() );
        assertEquals( "description", contentType.getDescription() );
        assertEquals( 1, contentType.getForm().size() );
        assertFalse( contentType.allowChildContent() );
        assertFalse( contentType.isAbstract() );
        assertTrue( contentType.isFinal() );
        assertNotNull( contentType.getModifiedTime() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/content-types/mytype/mytype.yaml", result.getResource().getKey().toString() );
        assertTrue( result.getResource().getSize() > 0 );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateContentTypeSchema()
        throws Exception
    {
        final CreateDynamicContentSchemaParams createParams = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) ).resource( """
                                                                          kind: "ContentType"
                                                                          superType: "base:unstructured"
                                                                          """ )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createContentType( createParams ) );

        final String resource = readResource( "_contentType.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<ContentType> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateContentType( updateParams ) );

        final ContentType contentType = (ContentType) result.getSchema();

        createAdminContext().runWith( () -> assertThat( contentType ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getContentType( ContentTypeName.from( "myapp:mytype" ) ).getSchema() ) );

        assertEquals( "myapp:mytype", contentType.getName().toString() );
        assertEquals( "Virtual Content Type", contentType.getTitle() );
        assertEquals( "description", contentType.getDescription() );
        assertEquals( 1, contentType.getForm().size() );
        assertFalse( contentType.allowChildContent() );
        assertFalse( contentType.isAbstract() );
        assertTrue( contentType.isFinal() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/content-types/mytype/mytype.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchema()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<FormFragmentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createFormFragment( params ) );

        final FormFragmentDescriptor fragment = (FormFragmentDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getFormFragment( FormFragmentName.from( "myapp:my-fragment" ) ).getSchema() ) );

        assertEquals( "myapp:my-fragment", fragment.getName().toString() );
        assertEquals( "Virtual FormFragment", fragment.getTitle() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment/my-fragment.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/form-fragments/my-fragment/my-fragment.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createFormFragmentSchemaAsDevSchemaAdmin()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<FormFragmentDescriptor> result =
            createSchemaAdminContext().callWith( () -> dynamicSchemaService.createFormFragment( params ) );

        assertNotNull( result.getResource() );
    }

    @Test
    void createFormFragmentSchemaAsNonSchemaAdmin()
        throws Exception
    {
        final String resource = readResource( "_formFragment.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> ctxDefault().callWith( () -> dynamicSchemaService.createFormFragment( params ) ) );
    }

    @Test
    void updateFormFragmentSchema()
        throws Exception
    {

        final CreateDynamicContentSchemaParams createParams =
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "MyFormFragment"
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createFormFragment( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<FormFragmentDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateFormFragment( updateParams ) );

        final FormFragmentDescriptor fragment = (FormFragmentDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( fragment ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getFormFragment( FormFragmentName.from( "myapp:my-fragment" ) ).getSchema() ) );

        assertEquals( "myapp:my-fragment", fragment.getName().toString() );
        assertEquals( "Virtual FormFragment", fragment.getTitle() );
        assertEquals( "FormFragment description", fragment.getDescription() );
        assertEquals( 2, fragment.getForm().size() );
        assertEquals( "myapp:inline", fragment.getForm().getFormFragment( "inline" ).getFormFragmentName().toString() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/form-fragments/my-fragment/my-fragment.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/form-fragments/my-fragment/my-fragment.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateFormFragmentSchemaAsSchemaAdmin()
        throws Exception
    {

        final CreateDynamicContentSchemaParams createParams =
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "FormFragment"
                               """ )
                .build();

        createSchemaAdminContext().runWith( () -> dynamicSchemaService.createFormFragment( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<FormFragmentDescriptor> result =
            createSchemaAdminContext().callWith( () -> dynamicSchemaService.updateFormFragment( updateParams ) );

        assertNotNull( result.getResource() );
    }

    @Test
    void updateFormFragmentSchemaAsNonSchemaAdmin()
        throws Exception
    {
        final CreateDynamicContentSchemaParams createParams =
            CreateDynamicContentSchemaParams.create()
                .name( FormFragmentName.from( "myapp:my-fragment" ) )
                .resource( """
                               kind: "FormFragment"
                               title: "FormFragment"
                               """ )
                .build();

        createSchemaAdminContext().runWith( () -> dynamicSchemaService.createFormFragment( createParams ) );

        final String resource = readResource( "_formFragment.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:my-fragment" ) )
            .resource( resource )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> ctxDefault().callWith( () -> dynamicSchemaService.updateFormFragment( updateParams ) ) );
    }


    @Test
    void createMixinSchema()
        throws Exception
    {
        final String resource = readResource( "_mixin.yaml" );

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<MixinDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createMixin( params ) );

        final MixinDescriptor mixinDescriptor = (MixinDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getMixin( MixinName.from( "myapp:mymixin" ) ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixinDescriptor.getName().toString() );
        assertEquals( "Virtual Mixin", mixinDescriptor.getTitle() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin/mymixin.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/mixins/mymixin/mymixin.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateMixinSchema()
        throws Exception
    {
        final CreateDynamicContentSchemaParams createParams =
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mymixin" ) )
                .resource( """
                               kind: "Mixin"
                               title: "Virtual MIXIN"
                               form: [ ]
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createMixin( createParams ) );

        final String resource = readResource( "_mixin.yaml" );

        final UpdateDynamicContentSchemaParams updateParams = UpdateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mymixin" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<MixinDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateMixin( updateParams ) );

        final MixinDescriptor mixinDescriptor = (MixinDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( mixinDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getMixin( MixinName.from( "myapp:mymixin" ) ).getSchema() ) );

        assertEquals( "myapp:mymixin", mixinDescriptor.getName().toString() );
        assertEquals( "Virtual Mixin", mixinDescriptor.getTitle() );
        assertEquals( "Mixin description", mixinDescriptor.getDescription() );
        assertEquals( 1, mixinDescriptor.getForm().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/mixins/mymixin/mymixin.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/mixins/mymixin/mymixin.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPartComponent()
        throws Exception
    {
        final String resource = readResource( "_part.yaml" );

        CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<PartDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createPart( params ) );

        final PartDescriptor partDescriptor = (PartDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getPart( DescriptorKey.from( "myapp:mypart" ) ).getSchema() ) );

        assertEquals( "mypart", partDescriptor.getName() );
        assertEquals( "myapp", partDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Part", partDescriptor.getTitle() );
        assertEquals( "key.display-name", partDescriptor.getTitleI18nKey() );
        assertEquals( "My Part Description", partDescriptor.getDescription() );
        assertEquals( "key.description", partDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, partDescriptor.getConfig().size() );
        assertNotNull( partDescriptor.getModifiedTime() );

        assertEquals( 1, partDescriptor.getSchemaConfig().properties().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/parts/mypart/mypart.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updatePartComponent()
        throws Exception
    {

        final CreateDynamicComponentParams createParams =
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                .resource( """
                               kind: "Part"
                               title: "MyPart"
                               form: [ ]
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createPart( createParams ) );

        final String resource = readResource( "_part.yaml" );

        final UpdateDynamicComponentParams updateParams = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<PartDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updatePart( updateParams ) );

        final PartDescriptor partDescriptor = (PartDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( partDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getPart( DescriptorKey.from( "myapp:mypart" ) ).getSchema() ) );

        assertEquals( "mypart", partDescriptor.getName() );
        assertEquals( "myapp", partDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Part", partDescriptor.getTitle() );
        assertEquals( "key.display-name", partDescriptor.getTitleI18nKey() );
        assertEquals( "My Part Description", partDescriptor.getDescription() );
        assertEquals( "key.description", partDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, partDescriptor.getConfig().size() );

        assertEquals( 1, partDescriptor.getSchemaConfig().properties().size() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/parts/mypart/mypart.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createLayoutComponent()
        throws Exception
    {
        final String resource = readResource( "_layout.yaml" );

        CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<LayoutDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createLayout( params ) );

        final LayoutDescriptor layoutDescriptor = (LayoutDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getLayout( DescriptorKey.from( "myapp:mylayout" ) ).getSchema() ) );

        assertEquals( "mylayout", layoutDescriptor.getName() );
        assertEquals( "myapp", layoutDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Layout", layoutDescriptor.getTitle() );
        assertEquals( "key.display-name", layoutDescriptor.getTitleI18nKey() );
        assertEquals( "My Layout Description", layoutDescriptor.getDescription() );
        assertEquals( "key.description", layoutDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, layoutDescriptor.getConfig().size() );
        assertEquals( 3, layoutDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/layouts/mylayout/mylayout.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/layouts/mylayout/mylayout.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateLayoutComponent()
        throws Exception
    {
        final CreateDynamicComponentParams params =
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
                .resource( """
                               kind: "Layout"
                               title: MyLayout
                               regions: [ ]
                               
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createLayout( params ) );

        final String resource = readResource( "_layout.yaml" );

        final UpdateDynamicComponentParams updateParams = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mylayout" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<LayoutDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateLayout( updateParams ) );

        final LayoutDescriptor layoutDescriptor = (LayoutDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( layoutDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getLayout( DescriptorKey.from( "myapp:mylayout" ) ).getSchema() ) );

        assertEquals( "mylayout", layoutDescriptor.getName() );
        assertEquals( "myapp", layoutDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Layout", layoutDescriptor.getTitle() );
        assertEquals( "key.display-name", layoutDescriptor.getTitleI18nKey() );
        assertEquals( "My Layout Description", layoutDescriptor.getDescription() );
        assertEquals( "key.description", layoutDescriptor.getDescriptionI18nKey() );
        assertEquals( 2, layoutDescriptor.getConfig().size() );
        assertEquals( 3, layoutDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/layouts/mylayout/mylayout.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/layouts/mylayout/mylayout.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPageComponent()
        throws Exception
    {
        final String resource = readResource( "_page.yaml" );

        CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<PageDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.createPage( params ) );

        final PageDescriptor pageDescriptor = (PageDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getPage( DescriptorKey.from( "myapp:mypage" ) ).getSchema() ) );

        assertEquals( "mypage", pageDescriptor.getName() );
        assertEquals( "myapp", pageDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Page", pageDescriptor.getTitle() );
        assertEquals( "key.display-name", pageDescriptor.getTitleI18nKey() );
        assertEquals( "My Page Description", pageDescriptor.getDescription() );
        assertEquals( "key.description", pageDescriptor.getDescriptionI18nKey() );
        assertEquals( 1, pageDescriptor.getConfig().size() );
        assertEquals( 3, pageDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/pages/mypage/mypage.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/pages/mypage/mypage.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updatePageComponent()
        throws Exception
    {
        final CreateDynamicComponentParams createParams =
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
                .resource( """
                               kind: "Page"
                               title: "MyPage"
                               regions:
                                 - "main"
                               """ )
                .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createPage( createParams ) );

        final String resource = readResource( "_page.yaml" );

        final UpdateDynamicComponentParams updateParams = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypage" ) )
            .resource( resource )
            .build();

        final DynamicSchemaResult<PageDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updatePage( updateParams ) );

        final PageDescriptor pageDescriptor = (PageDescriptor) result.getSchema();

        createAdminContext().runWith( () -> assertThat( pageDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getPage( DescriptorKey.from( "myapp:mypage" ) ).getSchema() ) );

        assertEquals( "mypage", pageDescriptor.getName() );
        assertEquals( "myapp", pageDescriptor.getApplicationKey().toString() );
        assertEquals( "Virtual Page", pageDescriptor.getTitle() );
        assertEquals( "key.display-name", pageDescriptor.getTitleI18nKey() );
        assertEquals( "My Page Description", pageDescriptor.getDescription() );
        assertEquals( "key.description", pageDescriptor.getDescriptionI18nKey() );
        assertEquals( 1, pageDescriptor.getConfig().size() );
        assertEquals( 3, pageDescriptor.getRegions().numberOfRegions() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/pages/mypage/mypage.yaml", result.getResource().getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/pages/mypage/mypage.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        // no cms descriptor until one is created
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.createCms( CreateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/cms.yaml", result.getResource().getKey().toString() );
        assertNotNull( cmsDescriptor.getModifiedTime() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void getSiteNonExistedApp()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "nonapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();
    }

    @Test
    void updateSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().runWith( () -> dynamicSchemaService.createCms(
            CreateDynamicCmsParams.create().key( applicationKey ).resource( CMS_DESCRIPTOR_DEFAULT_VALUE ).build() ) );

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.updateCms( UpdateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/cms.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateNotCreatedSite()
        throws Exception
    {
        final String resource = readResource( "_cms.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final DynamicSchemaResult<CmsDescriptor> result = createAdminContext().callWith(
            () -> dynamicSchemaService.updateCms( UpdateDynamicCmsParams.create().key( applicationKey ).resource( resource ).build() ) );

        final CmsDescriptor cmsDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( cmsDescriptor ).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields( "mappingDescriptors" ).build() )
            .isEqualTo( dynamicSchemaService.getCmsDescriptor( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/cms.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/cms.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteCms()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        // no cms descriptor until one is created
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> dynamicSchemaService.createCms(
            CreateDynamicCmsParams.create().key( applicationKey ).resource( readResource( "_cms.yaml" ) ).build() ) );

        DynamicSchemaResult<CmsDescriptor> cmsDescriptorResult =
            createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) );

        assertThat( cmsDescriptorResult.getSchema().getForm() ).isNotEmpty();
        assertThat( cmsDescriptorResult.getSchema().getMixinMappings() ).isNotEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteCms( applicationKey ) ) ).isTrue();

        // the deleted descriptor is gone, nothing is synthesized in its place
        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getCmsDescriptor( applicationKey ) ) ).isNull();
    }

    @Test
    void createStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/style/style.yaml", result.getResource().getKey().toString() );
        assertNotNull( styleDescriptor.getModifiedTime() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/style/style.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void updateStyles()
        throws Exception
    {
        final String resource = readResource( "_styles.yaml" );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( "kind: \"Style\"\n" ).build() ) );

        final DynamicSchemaResult<StyleDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.updateStyles(
            UpdateDynamicStylesParams.create().key( applicationKey ).resource( resource ).build() ) );

        final StyleDescriptor styleDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( styleDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getStyles( applicationKey ).getSchema() ) );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertTrue( Instant.now().isAfter( Instant.ofEpochMilli( result.getResource().getTimestamp() ) ) );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/style/style.yaml", result.getResource().getKey().toString() );

        final Node resourceNode =
            appRepoAdminContext().callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/style/style.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void deleteStyles()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();

        createAdminContext().callWith( () -> dynamicSchemaService.createStyles(
            CreateDynamicStylesParams.create().key( applicationKey ).resource( readResource( "_styles.yaml" ) ).build() ) );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNotNull();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.deleteStyles( applicationKey ) ) ).isTrue();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getStyles( applicationKey ) ) ).isNull();
    }


    @Test
    void listPartComponents()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<PartDescriptor>> results = createAdminContext().callWith( () -> dynamicSchemaService.listParts(
            applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<PartDescriptor> part1 = createAdminContext().callWith( () -> dynamicSchemaService.createPart(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart1" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part2 = createAdminContext().callWith( () -> dynamicSchemaService.createPart(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart2" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );
        DynamicSchemaResult<PartDescriptor> part3 = createAdminContext().callWith( () -> dynamicSchemaService.createPart(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "my_other_app:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listParts(
            applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part1, part2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listParts( ApplicationKey.from( "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( part3 ) );

    }

    @Test
    void listContentTypes()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<ContentType>> results = createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes(
            applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<ContentType> contentType1 = createAdminContext().callWith( () -> dynamicSchemaService.createContentType(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype1" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType2 = createAdminContext().callWith( () -> dynamicSchemaService.createContentType(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype2" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );
        DynamicSchemaResult<ContentType> contentType3 = createAdminContext().callWith( () -> dynamicSchemaService.createContentType(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes( applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( contentType1, contentType2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes( ApplicationKey.from(
                                                                                                        "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( contentType3 ) );

    }

    @Test
    void listFormFragments()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<FormFragmentDescriptor>> results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments(
            applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments( applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments( ApplicationKey.from(
                                                                                                        "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment3 ) );

    }

    @Test
    void listFormFragmentsAsSchemaAdmin()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<FormFragmentDescriptor>> results = createSchemaAdminContext().callWith(
            () -> dynamicSchemaService.listFormFragments( applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<FormFragmentDescriptor> fragment1 = createAdminContext().callWith(
            () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment2 = createAdminContext().callWith(
            () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );
        DynamicSchemaResult<FormFragmentDescriptor> fragment3 = createAdminContext().callWith(
            () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                .name( FormFragmentName.from( "my_other_app:mytype" ) )
                                                                .resource( readResource( "_formFragment.yaml" ) )
                                                                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments( applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment1, fragment2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listFormFragments( ApplicationKey.from(
                                                                                                        "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( fragment3 ) );

    }

    @Test
    void listFormFragmentsAsNonSchemaAdmin()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<FormFragmentDescriptor>> results = createSchemaAdminContext().callWith(
            () -> dynamicSchemaService.listFormFragments( applicationKey  ) );

        assertTrue( results.isEmpty() );

        createAdminContext().callWith( () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype1" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:mytype2" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from(
                                                                                               "my_other_app:mytype" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault()
            .callWith( () -> dynamicSchemaService.listFormFragments( applicationKey  ) ) );
    }

    @Test
    void listMixinsTypes()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        List<DynamicSchemaResult<MixinDescriptor>> results = createAdminContext().callWith( () -> dynamicSchemaService.listMixins(
            applicationKey  ) );

        assertTrue( results.isEmpty() );

        DynamicSchemaResult<MixinDescriptor> mixin1 = createAdminContext().callWith( () -> dynamicSchemaService.createMixin(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype1" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin2 = createAdminContext().callWith( () -> dynamicSchemaService.createMixin(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "myapp:mytype2" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .build() ) );
        DynamicSchemaResult<MixinDescriptor> mixin3 = createAdminContext().callWith( () -> dynamicSchemaService.createMixin(
            CreateDynamicContentSchemaParams.create()
                .name( MixinName.from( "my_other_app:mytype" ) )
                .resource( readResource( "_mixin.yaml" ) )
                .build() ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listMixins(
            applicationKey  ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin1, mixin2 ) );

        results = createAdminContext().callWith( () -> dynamicSchemaService.listMixins( ApplicationKey.from(
                                                                                                        "my_other_app" ) ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( mixin3 ) );

    }

    @Test
    void deleteContentTypeComponent()
    {
        DynamicSchemaResult<ContentType> contentType = createAdminContext().callWith( () -> dynamicSchemaService.createContentType(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        final boolean result = createAdminContext().callWith( () -> dynamicSchemaService.deleteContentType(
            contentType.getSchema().getName() ) );

        assertThat( result ).isTrue();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listContentTypes( ApplicationKey.from( "myapp" ) ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getContentType( contentType.getSchema().getName() ) ) ).isNull();
    }

    @Test
    void deleteContentTypeComponentAsSchemaAdmin()
    {
        DynamicSchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> dynamicSchemaService.createContentType(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        final boolean result = createSchemaAdminContext().callWith( () -> dynamicSchemaService.deleteContentType(
            contentType.getSchema().getName() ) );

        assertThat( result ).isTrue();
    }

    @Test
    void deleteContentTypeComponentAsNonSchemaAdmin()
    {
        DynamicSchemaResult<ContentType> contentType = createSchemaAdminContext().callWith( () -> dynamicSchemaService.createContentType(
            CreateDynamicContentSchemaParams.create()
                .name( ContentTypeName.from( "myapp:mytype" ) )
                .resource( readResource( "_contentType.yaml" ) )
                .build() ) );

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault()
            .callWith( () -> dynamicSchemaService.deleteContentType( contentType.getSchema().getName() ) ) );

    }


    @Test
    void deletePartComponent()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        DynamicSchemaResult<PartDescriptor> part = createAdminContext().callWith( () -> dynamicSchemaService.createPart(
            CreateDynamicComponentParams.create()
                .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                .resource( readResource( "_part.yaml" ) )
                .build() ) );

        final boolean result = createAdminContext().callWith( () -> dynamicSchemaService.deletePart(
            part.getSchema().getKey() ) );

        assertTrue( result );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listParts(
            applicationKey  ) ) ).isEmpty();

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.getPart( part.getSchema().getKey() ) ) ).usingRecursiveComparison()
            .isNull();

    }

    @Test
    void createContentTypeSchemaInvalid()
    {
        final String resource = "unsupportedField: [ ]";

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        final RuntimeException exception = assertThrows( RuntimeException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createContentType( params ) ) );

        assertEquals( "Could not parse dynamic content type [myapp:mytype]", exception.getMessage() );
    }

    @Test
    void createContentTypeSchemaWithNameOfAnotherKind()
    {
        final CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mytype" ) )
            .resource( "kind: \"ContentType\"" )
            .build();

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.createContentType( params ) ) );

        assertEquals( "expected ContentTypeName but got MixinName: myapp:mytype", exception.getMessage() );
    }

    @Test
    public void createFormFragmentSchemaInvalid()
    {
        final String resource = """
            kind: "FormFragment"
            unsupportedField: [ ]
            """;

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( FormFragmentName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createFormFragment( params ) ) );
    }

    @Test
    void createMixinSchemaInvalid()
    {
        final String resource = """
            kind: "Mixin"
            unsupportedField: [ ]
            """;

        CreateDynamicContentSchemaParams params = CreateDynamicContentSchemaParams.create()
            .name( MixinName.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createMixin( params ) ) );
    }

    @Test
    void createPartInvalid()
    {
        final String resource = """
            kind: "Part"        
            unsupportedField: [ ]
            """;

        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createPart( params ) ) );
    }

    @Test
    void createLayoutInvalid()
    {
        final String resource = """
            kind: "Layout"
            unsupportedField: [ ]
            """;

        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createLayout( params ) ) );
    }

    @Test
    void createPageInvalid()
    {
        final String resource = """
            kind: "Page"
            unsupportedField: [ ]
            """;

        final CreateDynamicComponentParams params = CreateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mytype" ) )
            .resource( resource )
            .build();

        assertThrows( UncheckedIOException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.createPage( params ) ) );
    }

    @Test
    void createStylesInvalid()
    {
        final String resource = """
            kind: "Style"
            unsupportedField: [ ]
            """;

        final CreateDynamicStylesParams params =
            CreateDynamicStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createStyles( params ) ) );
    }

    @Test
    void createSiteInvalid()
    {
        final String resource = """
            kind: "CMS"
            unsupportedField: [ ]
            """;

        final CreateDynamicCmsParams params =
            CreateDynamicCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( resource ).build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createCms( params ) ) );
    }

    @Test
    void createMacro()
        throws Exception
    {
        final String resource = readResource( "_macro.yaml" );

        final CreateDynamicMacroParams params =
            CreateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( resource ).build();

        final DynamicSchemaResult<MacroDescriptor> result = createAdminContext().callWith( () -> dynamicSchemaService.createMacro( params ) );

        final MacroDescriptor macroDescriptor = result.getSchema();

        createAdminContext().runWith( () -> assertThat( macroDescriptor ).usingRecursiveComparison()
            .isEqualTo( dynamicSchemaService.getMacro( MacroKey.from( "myapp:mymacro" ) ).getSchema() ) );

        assertEquals( "mymacro", macroDescriptor.getName() );
        assertEquals( "myapp", macroDescriptor.getKey().getApplicationKey().toString() );
        assertEquals( "Dynamic Macro", macroDescriptor.getTitle() );
        assertEquals( "key.display-name", macroDescriptor.getTitleI18nKey() );
        assertEquals( "My Macro Description", macroDescriptor.getDescription() );
        assertEquals( "key.description", macroDescriptor.getDescriptionI18nKey() );
        assertEquals( 1, macroDescriptor.getForm().size() );
        assertNotNull( macroDescriptor.getModifiedTime() );
        assertEquals( 1, macroDescriptor.getSchemaConfig().properties().size() );
        assertNull( macroDescriptor.getIcon() );

        assertEquals( "node", result.getResource().getResolverName() );
        assertTrue( result.getResource().exists() );
        assertEquals( resource, result.getResource().readString() );
        assertEquals( "myapp:/cms/macros/mymacro/mymacro.yaml", result.getResource().getKey().toString() );

        // the macros folder did not exist for the application, it is created on the way
        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/macros/mymacro/mymacro.yaml" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createMacroWithoutAdminRole()
    {
        final CreateDynamicMacroParams params = CreateDynamicMacroParams.create()
            .key( MacroKey.from( "myapp:mymacro" ) )
            .resource( "kind: \"Macro\"\ntitle: \"MyMacro\"\n" )
            .build();

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault().callWith( () -> dynamicSchemaService.createMacro( params ) ) );
    }

    @Test
    void updateMacro()
        throws Exception
    {
        final CreateDynamicMacroParams createParams = CreateDynamicMacroParams.create()
            .key( MacroKey.from( "myapp:mymacro" ) )
            .resource( """
                           kind: "Macro"
                           title: "MyMacro"
                           form: [ ]
                           """ )
            .build();

        createAdminContext().runWith( () -> dynamicSchemaService.createMacro( createParams ) );

        final String resource = readResource( "_macro.yaml" );

        final UpdateDynamicMacroParams updateParams =
            UpdateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( resource ).build();

        final DynamicSchemaResult<MacroDescriptor> result =
            createAdminContext().callWith( () -> dynamicSchemaService.updateMacro( updateParams ) );

        assertEquals( "Dynamic Macro", result.getSchema().getTitle() );
        assertEquals( resource, result.getResource().readString() );
    }

    @Test
    void listMacros()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listMacros( applicationKey ) ) ).isEmpty();

        final DynamicSchemaResult<MacroDescriptor> macro1 = createAdminContext().callWith( () -> dynamicSchemaService.createMacro(
            CreateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro1" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );
        final DynamicSchemaResult<MacroDescriptor> macro2 = createAdminContext().callWith( () -> dynamicSchemaService.createMacro(
            CreateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro2" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createMacro( CreateDynamicMacroParams.create()
                                                                                   .key( MacroKey.from( "my_other_app:mymacro" ) )
                                                                                   .resource( readResource( "_macro.yaml" ) )
                                                                                   .build() ) );

        final List<DynamicSchemaResult<MacroDescriptor>> results =
            createAdminContext().callWith( () -> dynamicSchemaService.listMacros( applicationKey ) );

        assertThat( results ).usingRecursiveComparison().isEqualTo( List.of( macro1, macro2 ) );
    }

    @Test
    void deleteMacro()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final DynamicSchemaResult<MacroDescriptor> macro = createAdminContext().callWith( () -> dynamicSchemaService.createMacro(
            CreateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteMacro( macro.getSchema().getKey() ) ) );

        assertThat( createAdminContext().callWith( () -> dynamicSchemaService.listMacros( applicationKey ) ) ).isEmpty();
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getMacro( macro.getSchema().getKey() ) ) );
    }

    @Test
    void createMacroInvalid()
    {
        final CreateDynamicMacroParams params = CreateDynamicMacroParams.create()
            .key( MacroKey.from( "myapp:mymacro" ) )
            .resource( """
                           kind: "Macro"
                           unsupportedField: [ ]
                           """ )
            .build();

        assertThrows( Exception.class, () -> createAdminContext().callWith( () -> dynamicSchemaService.createMacro( params ) ) );
    }

    @Test
    void createPhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final String resource = "action.save=Save\naction.delete=Delete\n";

        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getPhrases(
            GetDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        final Resource result = createAdminContext().callWith( () -> dynamicSchemaService.createPhrases(
            CreateDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( resource ).build() ) );

        assertEquals( "node", result.getResolverName() );
        assertTrue( result.exists() );
        assertEquals( resource, result.readString() );
        assertEquals( "myapp:/cms/i18n/phrases/phrases_en.properties", result.getKey().toString() );

        final Resource fetched = createAdminContext().callWith( () -> dynamicSchemaService.getPhrases(
            GetDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) );

        assertEquals( "node", fetched.getResolverName() );
        assertEquals( resource, fetched.readString() );

        // both the i18n and the phrases folders are created on the way
        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/i18n/phrases/phrases_en.properties" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void createPhrasesWithoutAdminRole()
    {
        final CreateDynamicPhrasesParams params =
            CreateDynamicPhrasesParams.create().key( ApplicationKey.from( "myapp" ) ).name( "phrases_en" ).resource( "a=b\n" ).build();

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault().callWith( () -> dynamicSchemaService.createPhrases( params ) ) );
    }

    @Test
    void updatePhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final String resource = "action.save=Save changes\n";

        createAdminContext().callWith( () -> dynamicSchemaService.createPhrases(
            CreateDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( "action.save=Save\n" ).build() ) );

        final Resource result = createAdminContext().callWith( () -> dynamicSchemaService.updatePhrases(
            UpdateDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( resource ).build() ) );

        assertEquals( "node", result.getResolverName() );
        assertEquals( resource, result.readString() );
        assertEquals( "myapp:/cms/i18n/phrases/phrases_en.properties", result.getKey().toString() );

        final Node resourceNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/i18n/phrases/phrases_en.properties" ) ) );

        assertEquals( resource, resourceNode.data().getString( "resource" ) );
    }

    @Test
    void listPhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.listPhrases( applicationKey ) ).isEmpty() );

        createAdminContext().callWith( () -> dynamicSchemaService.createPhrases(
            CreateDynamicPhrasesParams.create().key( applicationKey ).name( "phrases" ).resource( "action.save=Save\n" ).build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createPhrases(
            CreateDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_no" ).resource( "action.save=Lagre\n" ).build() ) );
        createAdminContext().callWith( () -> dynamicSchemaService.createPhrases( CreateDynamicPhrasesParams.create()
                                                                                     .key( ApplicationKey.from( "my_other_app" ) )
                                                                                     .name( "phrases" )
                                                                                     .resource( "action.save=Other\n" )
                                                                                     .build() ) );

        final List<Resource> result = createAdminContext().callWith( () -> dynamicSchemaService.listPhrases( applicationKey ) );

        assertThat( result.stream().map( resource -> resource.getKey().toString() ) ).containsExactlyInAnyOrder(
            "myapp:/cms/i18n/phrases/phrases.properties", "myapp:/cms/i18n/phrases/phrases_no.properties" );
    }

    @Test
    void deletePhrases()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        createAdminContext().callWith( () -> dynamicSchemaService.createPhrases(
            CreateDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).resource( "action.save=Save\n" ).build() ) );

        assertNotNull( createAdminContext().callWith( () -> dynamicSchemaService.getPhrases(
            GetDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deletePhrases(
            DeleteDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getPhrases(
            GetDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );

        assertFalse( createAdminContext().callWith( () -> dynamicSchemaService.deletePhrases(
            DeleteDynamicPhrasesParams.create().key( applicationKey ).name( "phrases_en" ).build() ) ) );
    }

    @Test
    void setContentTypeIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> dynamicSchemaService.createContentType( CreateDynamicContentSchemaParams.create()
                                                                                           .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                           .resource( readResource( "_contentType.yaml" ) )
                                                                                           .build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        final Icon icon = createAdminContext().callWith( () -> dynamicSchemaService.setContentTypeIcon(
            contentTypeIconParams( "myapp:mytype", ByteSource.wrap( iconData ), "image/svg+xml" ) ) );

        assertEquals( "image/svg+xml", icon.getMimeType() );
        assertArrayEquals( iconData, icon.toByteArray() );

        // stored like the icons persisted on application install: mime type in the data, the content as the icon binary
        final Node iconNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.svg" ) ) );

        assertEquals( "image/svg+xml", iconNode.data().getString( "mimeType" ) );
        assertNull( iconNode.data().getString( "resource" ) );
        assertNotNull( iconNode.getAttachedBinaries().getByBinaryReference( BinaryReference.from( "icon" ) ) );
        assertArrayEquals( iconData, appRepoAdminContext()
            .callWith( () -> nodeService.getBinary( iconNode.id(), BinaryReference.from( "icon" ) ) )
            .read() );

        // the descriptor node is touched, so that the content type is re-read with its new icon
        final Node yamlNode = appRepoAdminContext()
            .callWith( () -> nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.yaml" ) ) );
        assertNotNull( yamlNode.data().getInstant( "iconModifiedTime" ) );

        final Icon fetched = createAdminContext().callWith(
            () -> dynamicSchemaService.getContentTypeIcon( contentTypeParams( "myapp:mytype" ) ) );
        assertEquals( "image/svg+xml", fetched.getMimeType() );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final ContentType contentType = createAdminContext().callWith(
            () -> dynamicSchemaService.getContentType( contentTypeParams( "myapp:mytype" ) ) ).getSchema();
        assertNotNull( contentType.getIcon() );
        assertArrayEquals( iconData, contentType.getIcon().toByteArray() );

        final List<DynamicSchemaResult<ContentType>> listed = createAdminContext().callWith(
            () -> dynamicSchemaService.listContentTypes( ApplicationKey.from( "myapp" ) ) );
        assertNotNull( listed.get( 0 ).getSchema().getIcon() );
    }

    @Test
    void setContentTypeIconReplacesPreviousFormat()
        throws Exception
    {
        createAdminContext().callWith( () -> dynamicSchemaService.createContentType( CreateDynamicContentSchemaParams.create()
                                                                                           .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                           .resource( readResource( "_contentType.yaml" ) )
                                                                                           .build() ) );

        createAdminContext().callWith( () -> dynamicSchemaService.setContentTypeIcon(
            contentTypeIconParams( "myapp:mytype", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ), "image/svg+xml" ) ) );

        final byte[] pngData = {(byte) 0x89, 'P', 'N', 'G'};
        createAdminContext().callWith( () -> dynamicSchemaService.setContentTypeIcon(
            contentTypeIconParams( "myapp:mytype", ByteSource.wrap( pngData ), "image/png" ) ) );

        appRepoAdminContext().runWith( () -> {
            assertNull( nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.svg" ) ) );
            assertNotNull( nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.png" ) ) );
        } );

        final Icon fetched = createAdminContext().callWith(
            () -> dynamicSchemaService.getContentTypeIcon( contentTypeParams( "myapp:mytype" ) ) );
        assertEquals( "image/png", fetched.getMimeType() );
        assertArrayEquals( pngData, fetched.toByteArray() );
    }

    @Test
    void setContentTypeIconForMissingSchema()
    {
        final SetDynamicContentSchemaIconParams params =
            contentTypeIconParams( "myapp:mytype", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ), "image/svg+xml" );

        assertThrows( SchemaNotFoundException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.setContentTypeIcon( params ) ) );
    }

    @Test
    void setContentTypeIconInvalidMimeType()
    {
        final SetDynamicContentSchemaIconParams params =
            contentTypeIconParams( "myapp:mytype", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ), "text/plain" );

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> createAdminContext().callWith(
            () -> dynamicSchemaService.setContentTypeIcon( params ) ) );

        assertEquals( "unsupported icon mime type: text/plain", exception.getMessage() );
    }

    @Test
    void setContentTypeIconEmptyData()
    {
        final SetDynamicContentSchemaIconParams params = contentTypeIconParams( "myapp:mytype", ByteSource.empty(), "image/svg+xml" );

        assertThrows( IllegalArgumentException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.setContentTypeIcon( params ) ) );
    }

    @Test
    void setContentTypeIconExceedingMaxSize()
    {
        final SetDynamicContentSchemaIconParams params =
            contentTypeIconParams( "myapp:mytype", ByteSource.wrap( new byte[100 * 1024 + 1] ), "image/svg+xml" );

        assertThrows( IllegalArgumentException.class,
                      () -> createAdminContext().callWith( () -> dynamicSchemaService.setContentTypeIcon( params ) ) );
    }

    @Test
    void setContentTypeIconWithoutAdminRole()
    {
        final SetDynamicContentSchemaIconParams params =
            contentTypeIconParams( "myapp:mytype", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ), "image/svg+xml" );

        assertThrows( ForbiddenAccessException.class, () -> ctxDefault().callWith( () -> dynamicSchemaService.setContentTypeIcon( params ) ) );
    }

    @Test
    void deleteContentTypeIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> dynamicSchemaService.createContentType( CreateDynamicContentSchemaParams.create()
                                                                                           .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                           .resource( readResource( "_contentType.yaml" ) )
                                                                                           .build() ) );

        createAdminContext().callWith( () -> dynamicSchemaService.setContentTypeIcon(
            contentTypeIconParams( "myapp:mytype", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ), "image/svg+xml" ) ) );

        final ContentTypeName deleteParams = ContentTypeName.from( "myapp:mytype" );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteContentTypeIcon( deleteParams ) ) );

        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getContentTypeIcon( contentTypeParams( "myapp:mytype" ) ) ) );
        final ContentType contentType = createAdminContext().callWith(
            () -> dynamicSchemaService.getContentType( contentTypeParams( "myapp:mytype" ) ) ).getSchema();
        assertNull( contentType.getIcon() );

        assertFalse( createAdminContext().callWith( () -> dynamicSchemaService.deleteContentTypeIcon( deleteParams ) ) );
    }

    @Test
    void deleteContentTypeCascadesIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> dynamicSchemaService.createContentType( CreateDynamicContentSchemaParams.create()
                                                                                           .name( ContentTypeName.from( "myapp:mytype" ) )
                                                                                           .resource( readResource( "_contentType.yaml" ) )
                                                                                           .build() ) );

        createAdminContext().callWith( () -> dynamicSchemaService.setContentTypeIcon(
            contentTypeIconParams( "myapp:mytype", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ), "image/svg+xml" ) ) );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteContentType( ContentTypeName.from(
                                                                                                            "myapp:mytype" ) ) ) );

        appRepoAdminContext().runWith( () -> {
            assertNull( nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.svg" ) ) );
            assertNull( nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.yaml" ) ) );
        } );
    }

    @Test
    void setPartIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> dynamicSchemaService.createPart( CreateDynamicComponentParams.create()
                                                                                       .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                                                                                       .resource( readResource( "_part.yaml" ) )
                                                                                       .build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        createAdminContext().callWith( () -> dynamicSchemaService.setPartIcon( SetDynamicComponentIconParams.create()
                                                                                        .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
                                                                                        .data( ByteSource.wrap( iconData ) )
                                                                                        .mimeType( "image/svg+xml" )
                                                                                        .build() ) );

        final DescriptorKey getParams =
            DescriptorKey.from( "myapp:mypart" );

        final Icon fetched = createAdminContext().callWith( () -> dynamicSchemaService.getPartIcon( getParams ) );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final PartDescriptor partDescriptor =
            createAdminContext().callWith( () -> dynamicSchemaService.getPart( getParams ) ).getSchema();
        assertNotNull( partDescriptor.getIcon() );
        assertArrayEquals( iconData, partDescriptor.getIcon().toByteArray() );

        final DescriptorKey deleteParams = DescriptorKey.from( "myapp:mypart" );
        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deletePartIcon( deleteParams ) ) );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getPartIcon( getParams ) ) );
    }

    @Test
    void setMacroIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> dynamicSchemaService.createMacro(
            CreateDynamicMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( readResource( "_macro.yaml" ) ).build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        createAdminContext().callWith( () -> dynamicSchemaService.setMacroIcon( SetDynamicMacroIconParams.create()
                                                                                    .key( MacroKey.from( "myapp:mymacro" ) )
                                                                                    .data( ByteSource.wrap( iconData ) )
                                                                                    .mimeType( "image/svg+xml" )
                                                                                    .build() ) );

        final Icon fetched = createAdminContext().callWith( () -> dynamicSchemaService.getMacroIcon( MacroKey.from( "myapp:mymacro" ) ) );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final MacroDescriptor macroDescriptor =
            createAdminContext().callWith( () -> dynamicSchemaService.getMacro( MacroKey.from( "myapp:mymacro" ) ) ).getSchema();
        assertNotNull( macroDescriptor.getIcon() );
        assertArrayEquals( iconData, macroDescriptor.getIcon().toByteArray() );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteMacroIcon( MacroKey.from( "myapp:mymacro" ) ) ) );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getMacroIcon( MacroKey.from( "myapp:mymacro" ) ) ) );
    }

    @Test
    void setMixinIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> dynamicSchemaService.createMixin( CreateDynamicContentSchemaParams.create()
                                                                                           .name( MixinName.from( "myapp:mymixin" ) )
                                                                                           .resource( readResource( "_mixin.yaml" ) )
                                                                                           .build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        createAdminContext().callWith( () -> dynamicSchemaService.setMixinIcon( SetDynamicContentSchemaIconParams.create()
                                                                                            .name( MixinName.from( "myapp:mymixin" ) )
                                                                                            .data( ByteSource.wrap( iconData ) )
                                                                                            .mimeType( "image/svg+xml" )
                                                                                            .build() ) );

        final MixinName getParams =
            MixinName.from( "myapp:mymixin" );

        final Icon fetched = createAdminContext().callWith( () -> dynamicSchemaService.getMixinIcon( getParams ) );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final MixinDescriptor mixinDescriptor =
            createAdminContext().callWith( () -> dynamicSchemaService.getMixin( getParams ) ).getSchema();
        assertNotNull( mixinDescriptor.getIcon() );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteMixinIcon(
            MixinName.from( "myapp:mymixin" ) ) ) );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getMixinIcon( getParams ) ) );
    }

    @Test
    void setFormFragmentIcon()
        throws Exception
    {
        createAdminContext().callWith( () -> dynamicSchemaService.createFormFragment( CreateDynamicContentSchemaParams.create()
                                                                                           .name( FormFragmentName.from( "myapp:myfragment" ) )
                                                                                           .resource( readResource( "_formFragment.yaml" ) )
                                                                                           .build() ) );

        final byte[] iconData = "<svg/>".getBytes( StandardCharsets.UTF_8 );

        createAdminContext().callWith( () -> dynamicSchemaService.setFormFragmentIcon( SetDynamicContentSchemaIconParams.create()
                                                                                            .name( FormFragmentName.from( "myapp:myfragment" ) )
                                                                                            .data( ByteSource.wrap( iconData ) )
                                                                                            .mimeType( "image/svg+xml" )
                                                                                            .build() ) );

        final FormFragmentName getParams = FormFragmentName.from( "myapp:myfragment" );

        final Icon fetched = createAdminContext().callWith( () -> dynamicSchemaService.getFormFragmentIcon( getParams ) );
        assertArrayEquals( iconData, fetched.toByteArray() );

        final FormFragmentDescriptor descriptor =
            createAdminContext().callWith( () -> dynamicSchemaService.getFormFragment( getParams ) ).getSchema();
        assertNotNull( descriptor.getIcon() );

        assertTrue( createAdminContext().callWith( () -> dynamicSchemaService.deleteFormFragmentIcon( FormFragmentName.from(
                                                                                                                "myapp:myfragment" ) ) ) );
        assertNull( createAdminContext().callWith( () -> dynamicSchemaService.getFormFragmentIcon( getParams ) ) );
    }

    private static ContentTypeName contentTypeParams( final String name )
    {
        return ContentTypeName.from( name );
    }

    private static SetDynamicContentSchemaIconParams contentTypeIconParams( final String name, final ByteSource data, final String mimeType )
    {
        return SetDynamicContentSchemaIconParams.create()
            .name( ContentTypeName.from( name ) )
            .data( data )
            .mimeType( mimeType )
            .build();
    }

    private String readResource( final String suffix )
        throws Exception
    {
        final String name = getClass().getSimpleName() + suffix;
        final URL url = getClass().getResource( name );

        if ( url == null )
        {
            throw new IllegalArgumentException( "Could not find resource [" + name + "]" );
        }

        return new String( url.openStream().readAllBytes(), StandardCharsets.UTF_8 );
    }
}
