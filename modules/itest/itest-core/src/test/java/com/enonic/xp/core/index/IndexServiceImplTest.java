package com.enonic.xp.core.index;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.index.IndexType;
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
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.node.FindNodesByQueryCommand;
import com.enonic.xp.repo.impl.node.PushNodesCommand;
import com.enonic.xp.security.SystemConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IndexServiceImplTest
    extends AbstractNodeTest
{
    private Node rootNode;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.rootNode = this.createDefaultRootNode();
    }

    @Test
    public void initialize()
        throws Exception
    {
        final Node node =
            createNode( CreateNodeParams.create().name( "myNode" ).parent( NodePath.ROOT ).refresh( RefreshMode.ALL ).build() );

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( WS_DEFAULT ).
            repositoryId( testRepoId ).
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
            refresh( RefreshMode.ALL ).
            build() );

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( WS_DEFAULT ).
            repositoryId( testRepoId ).
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
            refresh( RefreshMode.ALL ).
            build() );

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( WS_DEFAULT ).
            repositoryId( testRepoId ).
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
            refresh( RefreshMode.ALL ).
            build() );

        PushNodesCommand.create().
            ids( NodeIds.from( node.id() ) ).
            target( WS_OTHER ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        refresh();

        assertNotNull( queryForNode( node.id() ) );
        assertNotNull( ctxOther().callWith( () -> queryForNode( node.id() ) ) );


        this.indexService.reindex( ReindexParams.create().
            addBranch( WS_OTHER ).
            repositoryId( testRepoId ).
            initialize( true ).
            build() );

        refresh();

        assertNull( queryForNode( node.id() ) );
        assertNotNull( ctxOther().callWith( () -> queryForNode( node.id() ) ) );
    }

    @Test
    public void reindex_cms_repo()
        throws Exception
    {

        final Context cmsRepoContext =
            ContextBuilder.from( ContextAccessor.current() ).repositoryId( testRepoId ).branch( ContentConstants.BRANCH_DRAFT ).build();

        cmsRepoContext.callWith( this::createDefaultRootNode );

        cmsRepoContext.callWith( () -> createNode( CreateNodeParams.create()
                                                       .setNodeId( NodeId.from( "su" ) )
                                                       .name( "su" )
                                                       .refresh( RefreshMode.ALL )
                                                       .parent( NodePath.ROOT )
                                                       .build() ) );

        assertEquals( 2, cmsRepoContext.callWith( this::findAllNodes ).getNodeHits().getSize() );

        this.indexService.reindex( ReindexParams.create().
            addBranch( cmsRepoContext.getBranch() ).
            repositoryId( cmsRepoContext.getRepositoryId() ).
            initialize( true ).
            build() );

        refresh();

        assertEquals( 2, cmsRepoContext.
            callWith( this::findAllNodes ).getNodeHits().getSize() );
    }


    @Test
    public void reindex_system_repo()
        throws Exception
    {

        final Context systemRepoContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build();

        systemRepoContext.callWith( this::createDefaultRootNode );

        systemRepoContext.callWith( () -> createNode( CreateNodeParams.create()
                                                          .setNodeId( NodeId.from( "su" ) )
                                                          .name( "su" )
                                                          .parent( NodePath.ROOT )
                                                          .refresh( RefreshMode.ALL )
                                                          .build() ) );

        final int nodesInSystemRepoCount = systemRepoContext.callWith( this::findAllNodes ).getNodeHits().getSize();

        this.indexService.reindex( ReindexParams.create().
            addBranch( systemRepoContext.getBranch() ).
            repositoryId( systemRepoContext.getRepositoryId() ).
            initialize( true ).
            build() );

        refresh();

        assertEquals( nodesInSystemRepoCount, systemRepoContext.callWith( this::findAllNodes ).getNodeHits().getSize() );
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

        return nodes.stream().filter( n -> nodeId.equals( n.id() ) ).findAny().orElse( null );
    }


    @Test
    public void updateIndexSettings()
        throws Exception
    {
        final UpdateIndexSettingsResult result = this.indexService.updateIndexSettings( UpdateIndexSettingsParams.create().
            repository( testRepoId ).
            settings( "{\"index\": {\"number_of_replicas\": 2}}" ).
            build() );

        assertEquals( 2, result.getUpdatedIndexes().size() );
    }

    @Test
    public void getIndexSettings_empty()
        throws Exception
    {
        final Map<String, String> indexSettings = this.indexService.getIndexSettings( testRepoId, IndexType.SEARCH );

        assertNull( indexSettings.get( "index.invalid_path" ) );
    }

    @Test
    public void getIndexSettings()
        throws Exception
    {
        this.indexService.updateIndexSettings( UpdateIndexSettingsParams.create().
            repository( testRepoId ).
            settings( "{\"index\": {\"number_of_replicas\": 2}}" ).
            build() );

        final Map<String, String> indexSettings = this.indexService.getIndexSettings( testRepoId, IndexType.SEARCH );

        assertEquals( "2", indexSettings.get( "index.number_of_replicas" ) );
    }

}
