package com.enonic.wem.core.content;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.DefaultParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.wem.api.content.attachment.CreateAttachment;
import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.media.MediaInfoServiceImpl;
import com.enonic.wem.core.schema.content.BuiltinContentTypeProvider;
import com.enonic.wem.core.schema.content.ContentTypeServiceImpl;
import com.enonic.wem.core.schema.mixin.BuiltinMixinProvider;
import com.enonic.wem.core.schema.mixin.MixinServiceImpl;
import com.enonic.wem.event.internal.EventPublisherImpl;
import com.enonic.wem.module.internal.ModuleRegistry;
import com.enonic.wem.module.internal.ModuleServiceImpl;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.repo.internal.elasticsearch.version.ElasticsearchVersionService;
import com.enonic.wem.repo.internal.elasticsearch.workspace.ElasticsearchWorkspaceService;
import com.enonic.wem.repo.internal.entity.NodeServiceImpl;
import com.enonic.wem.repo.internal.entity.dao.NodeDaoImpl;
import com.enonic.wem.repo.internal.repository.RepositoryInitializer;

public class AbstractContentServiceTest
    extends AbstractElasticsearchIntegrationTest
{
    protected ContentServiceImpl contentService;

    protected NodeServiceImpl nodeService;

    private NodeDaoImpl nodeDao;

    private ElasticsearchVersionService versionService;

    private ElasticsearchWorkspaceService workspaceService;

    private ElasticsearchIndexService indexService;

    private ElasticsearchQueryService queryService;

    protected BlobStore binaryBlobStore;


    protected static final Workspace WS_DEFAULT = Workspace.create().
        name( "draft" ).
        build();

    protected static final Workspace WS_OTHER = Workspace.create().
        name( "online" ).
        build();

    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( UserStoreKey.system(), "test-user" ) ).login( "test-user" ).build();

    public static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        user( TEST_DEFAULT_USER ).
        build();


    protected static final Context CTX_DEFAULT = ContextBuilder.create().
        workspace( WS_DEFAULT ).
        repositoryId( TEST_REPO.getId() ).
        authInfo( TEST_DEFAULT_USER_AUTHINFO ).
        build();

    protected static final Context CTX_OTHER = ContextBuilder.create().
        workspace( WS_OTHER ).
        repositoryId( TEST_REPO.getId() ).
        authInfo( TEST_DEFAULT_USER_AUTHINFO ).
        build();

    @Rule
    public TemporaryFolder WEM_HOME = new TemporaryFolder();


    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        System.setProperty( "wem.home", WEM_HOME.getRoot().getPath() );

        ContextAccessor.INSTANCE.set( CTX_DEFAULT );

        this.binaryBlobStore = new FileBlobStore( "test" );

        this.queryService = new ElasticsearchQueryService();
        this.queryService.setElasticsearchDao( elasticsearchDao );

        this.workspaceService = new ElasticsearchWorkspaceService();
        this.workspaceService.setElasticsearchDao( elasticsearchDao );

        this.versionService = new ElasticsearchVersionService();
        this.versionService.setElasticsearchDao( elasticsearchDao );

        this.indexService = new ElasticsearchIndexService();
        this.indexService.setClient( client );
        this.indexService.setElasticsearchDao( elasticsearchDao );

        this.nodeDao = new NodeDaoImpl();
        this.nodeDao.setWorkspaceService( this.workspaceService );

        this.contentService = new ContentServiceImpl();

        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexService( indexService );
        this.nodeService.setQueryService( queryService );
        this.nodeService.setNodeDao( nodeDao );
        this.nodeService.setVersionService( versionService );
        this.nodeService.setWorkspaceService( workspaceService );

        final MixinServiceImpl mixinService = new MixinServiceImpl();
        final BuiltinMixinProvider mixinProvider = new BuiltinMixinProvider();
        mixinService.addProvider( mixinProvider );

        final ContentNodeTranslator contentNodeTranslator = new ContentNodeTranslator();
        contentNodeTranslator.setMixinService( mixinService );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl();

        final MediaInfoServiceImpl mediaInfoService = new MediaInfoServiceImpl();
        mediaInfoService.setDetector( new DefaultDetector() );
        mediaInfoService.setParser( new DefaultParser() );

        final ModuleRegistry moduleRegistry = Mockito.mock( ModuleRegistry.class );
        final ModuleServiceImpl moduleService = new ModuleServiceImpl();
        moduleService.setRegistry( moduleRegistry );

        final ContentTypeServiceImpl contentTypeService = new ContentTypeServiceImpl();
        contentTypeService.setMixinService( mixinService );
        contentTypeService.addProvider( new BuiltinContentTypeProvider() );

        this.contentService.setNodeService( this.nodeService );
        this.contentService.setEventPublisher( eventPublisher );
        this.contentService.setMediaInfoService( mediaInfoService );
        this.contentService.setModuleService( moduleService );
        this.contentService.setContentNodeTranslator( contentNodeTranslator );
        this.contentService.setContentTypeService( contentTypeService );
        this.contentService.setMixinService( mixinService );

        createContentRepository();
        waitForClusterHealth();

        final ContentInitializer contentInitializer = new ContentInitializer( this.nodeService );
        contentInitializer.initialize();
    }

    void createContentRepository()
    {
        createRepository( TEST_REPO );
    }

    void createRepository( final Repository repository )
    {
        NodeServiceImpl nodeService = new NodeServiceImpl();
        nodeService.setIndexService( indexService );
        nodeService.setQueryService( queryService );
        nodeService.setNodeDao( nodeDao );
        nodeService.setVersionService( versionService );
        nodeService.setWorkspaceService( workspaceService );

        RepositoryInitializer repositoryInitializer = new RepositoryInitializer( indexService );
        repositoryInitializer.initializeRepository( repository );

        refresh();
    }

    protected ByteSource loadImage( final String name )
        throws IOException
    {
        final InputStream imageStream = this.getClass().getResourceAsStream( name );

        return ByteSource.wrap( ByteStreams.toByteArray( imageStream ) );
    }


    protected CreateAttachments createAttachment( final String name, final String mimeType, final ByteSource image )
    {
        return CreateAttachments.from( CreateAttachment.create().
            name( name ).
            mimeType( mimeType ).
            byteSource( image ).
            build() );
    }


}
