package com.enonic.xp.repo.impl.index;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.PurgeIndexParams;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.index.UpdateIndexSettingsParams;
import com.enonic.xp.index.UpdateIndexSettingsResult;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.FindNodesByQueryCommand;
import com.enonic.xp.repo.impl.node.PushNodesCommand;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.security.SystemConstants;

import static org.junit.Assert.*;

public class IndexServiceImplTest
    extends AbstractNodeTest
{
    private IndexServiceImpl indexService;

    private Node rootNode;

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.indexService = new IndexServiceImpl();
        this.indexService.setNodeSearchService( this.searchService );
        this.indexService.setIndexServiceInternal( this.indexServiceInternal );
        this.indexService.setNodeVersionService( this.nodeDao );
        this.indexService.setIndexDataService( this.indexedDataService );
        this.indexService.setIndexDataService( this.indexedDataService );
        this.indexService.setRepositoryEntryService( this.repositoryEntryService );

        this.rootNode = this.createDefaultRootNode();
    }

    @Test
    public void initialize()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( true ).
            build() );

        refresh();

        assertEquals( 2, result.getReindexNodes().getSize() );

        assertNotNull( queryForNode( node.id() ) );
        assertNotNull( queryForNode( rootNode.id() ) );
    }

    @Test
    public void not_initialize()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( false ).
            build() );

        assertEquals( 2, result.getReindexNodes().getSize() );

        assertNotNull( queryForNode( node.id() ) );
        assertNotNull( queryForNode( rootNode.id() ) );
    }

    @Test
    public void purge_then_reindex()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        this.indexService.purgeSearchIndex( new PurgeIndexParams( CTX_DEFAULT.getRepositoryId() ) );

        assertNull( queryForNode( node.id() ) );

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( false ).
            build() );

        assertEquals( 2, result.getReindexNodes().getSize() );

        refresh();

        assertNotNull( queryForNode( node.id() ) );
        assertNotNull( queryForNode( rootNode.id() ) );
    }

    @Test
    public void reindex_other_branch()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        PushNodesCommand.create().
            ids( NodeIds.from( node.id() ) ).
            target( CTX_OTHER.getBranch() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        refresh();

        assertNotNull( queryForNode( node.id() ) );
        assertNotNull( CTX_OTHER.callWith( () -> queryForNode( node.id() ) ) );

        this.indexService.purgeSearchIndex( new PurgeIndexParams( CTX_DEFAULT.getRepositoryId() ) );

        refresh();

        assertNull( queryForNode( node.id() ) );

        this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( false ).
            build() );

        refresh();

        assertNotNull( queryForNode( node.id() ) );
        assertNull( CTX_OTHER.callWith( () -> queryForNode( node.id() ) ) );

        this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_OTHER.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( true ).
            build() );

        refresh();

        assertNull( queryForNode( node.id() ) );
        assertNotNull( CTX_OTHER.callWith( () -> queryForNode( node.id() ) ) );
    }

    @Test
    public void reindex_cms_repo()
        throws Exception
    {

        final Context cmsRepoContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( ContentConstants.CONTENT_REPO.getId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            build();

        cmsRepoContext.callWith( this::createDefaultRootNode );

        cmsRepoContext.
            callWith( () -> createNode( CreateNodeParams.create().
                setNodeId( NodeId.from( "su" ) ).
                name( "su" ).
                parent( NodePath.ROOT ).
                build() ) );

        refresh();

        assertEquals( 2, cmsRepoContext.
            callWith( this::findAllNodes ).getHits() );

        this.indexService.purgeSearchIndex( new PurgeIndexParams( cmsRepoContext.getRepositoryId() ) );

        assertEquals( 0, cmsRepoContext.
            callWith( this::findAllNodes ).getHits() );

        this.indexService.reindex( ReindexParams.create().
            addBranch( cmsRepoContext.getBranch() ).
            repositoryId( cmsRepoContext.getRepositoryId() ).
            initialize( true ).
            build() );

        refresh();

        assertEquals( 2, cmsRepoContext.
            callWith( this::findAllNodes ).getHits() );
    }


    @Test
    public void reindex_system_repo()
        throws Exception
    {

        final Context systemRepoContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build();

        systemRepoContext.callWith( this::createDefaultRootNode );

        systemRepoContext.
            callWith( () -> createNode( CreateNodeParams.create().
                setNodeId( NodeId.from( "su" ) ).
                name( "su" ).
                parent( NodePath.ROOT ).
                build() ) );

        refresh();

        assertEquals( 5, systemRepoContext.
            callWith( this::findAllNodes ).getHits() );

        this.indexService.purgeSearchIndex( new PurgeIndexParams( systemRepoContext.getRepositoryId() ) );

        assertEquals( 0, systemRepoContext.
            callWith( this::findAllNodes ).getHits() );

        this.indexService.reindex( ReindexParams.create().
            addBranch( systemRepoContext.getBranch() ).
            repositoryId( systemRepoContext.getRepositoryId() ).
            initialize( true ).
            build() );

        refresh();

        assertEquals( 5, systemRepoContext.
            callWith( this::findAllNodes ).getHits() );

    }

    private FindNodesByQueryResult findAllNodes()
    {
        return FindNodesByQueryCommand.create().
            query( NodeQuery.create().build() ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute();
    }

    private Node queryForNode( final NodeId nodeId )
    {
        final FindNodesByQueryResult result = FindNodesByQueryCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            query( NodeQuery.create().query( QueryParser.parse( "_id = '" + nodeId.toString() + "'" ) ).build() ).
            build().
            execute();

        final Nodes nodes = getNodes( result.getNodeIds() );

        return nodes.getNodeById( nodeId );
    }


    @Test
    public void updateIndexSettings()
        throws Exception
    {
        final UpdateIndexSettingsResult result = this.indexService.updateIndexSettings( UpdateIndexSettingsParams.create().
            repository( TEST_REPO.getId() ).
            settings( "{\"index\": {\"number_of_replicas\": 2}}" ).
            build() );

        assertEquals( 2, result.getUpdatedIndexes().size() );
    }

    @Test
    public void getIndexSettings_empty()
        throws Exception
    {
        final IndexSettings indexSettings = this.indexService.getIndexSettings( TEST_REPO.getId(), IndexType.SEARCH );

        assertNull( indexSettings.getNode().get( "index.invalid_path" ) );
    }

    @Test
    public void getIndexSettings()
        throws Exception
    {
        this.indexService.updateIndexSettings( UpdateIndexSettingsParams.create().
            repository( TEST_REPO.getId() ).
            settings( "{\"index\": {\"number_of_replicas\": 2}}" ).
            build() );

        final IndexSettings indexSettings = this.indexService.getIndexSettings( TEST_REPO.getId(), IndexType.SEARCH );

        assertEquals( "\"2\"", indexSettings.getNode().get( "index.number_of_replicas" ).toString() );
    }

}