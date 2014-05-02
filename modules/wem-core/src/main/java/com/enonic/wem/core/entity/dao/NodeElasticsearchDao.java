package com.enonic.wem.core.entity.dao;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.elastic.ByIdQuery;
import com.enonic.wem.core.index.elastic.ByIdsQuery;
import com.enonic.wem.core.index.elastic.ByParentPathQuery;
import com.enonic.wem.core.index.elastic.ByPathQuery;
import com.enonic.wem.core.index.elastic.ByPathsQuery;
import com.enonic.wem.core.index.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.index.elastic.IndexDocumentId;

public class NodeElasticsearchDao
{
    public static final String CONTENT_ROOT_NODE_NAME = "content";

    public static final NodePath CONTENT_ROOT_NODE = NodePath.newNodePath( NodePath.ROOT, CONTENT_ROOT_NODE_NAME ).build();

    @Inject
    private ElasticsearchIndexService elasticsearchIndexService;

    public Node create( final CreateNodeArguments createNodeArguments )
    {
        Preconditions.checkNotNull( createNodeArguments.parent(), "Path of parent Node must be specified" );
        Preconditions.checkArgument( createNodeArguments.parent().isAbsolute(),
                                     "Path to parent Node must be absolute: " + createNodeArguments.parent() );

        final DateTime now = DateTime.now();

        final Node newNode = Node.newNode().
            id( new EntityId() ).
            createdTime( now ).
            modifiedTime( now ).
            creator( createNodeArguments.creator() ).
            modifier( createNodeArguments.creator() ).
            parent( createNodeArguments.parent() ).
            name( NodeName.from( createNodeArguments.name() ) ).
            rootDataSet( createNodeArguments.rootDataSet() ).
            attachments( createNodeArguments.attachments() ).
            entityIndexConfig( createNodeArguments.entityIndexConfig() ).
            build();

        final NodeStorageDocument nodeStorageDocument = NodeStorageDocumentFactory.create( newNode );

        elasticsearchIndexService.storeDocument( nodeStorageDocument );

        return newNode;
    }

    public Node update( final UpdateNodeArgs updateNodeArguments )
    {
        Preconditions.checkNotNull( updateNodeArguments.nodeToUpdate(), "nodeToUpdate must be specified" );

        final Node persistedNode = getById( updateNodeArguments.nodeToUpdate() );

        final DateTime now = DateTime.now();

        // TODO: Should'nt new Node(Node) Handle THIS?!

        final Node.Builder updateNodeBuilder = Node.newNode( persistedNode ).
            id( persistedNode.id() ).
            parent( persistedNode.parent() ).
            name( persistedNode.name() ).
            creator( persistedNode.creator() ).
            createdTime( persistedNode.getCreatedTime() ).
            modifiedTime( now ).
            modifier( updateNodeArguments.updater() ).
            rootDataSet( updateNodeArguments.rootDataSet() ).
            attachments( syncronizeAttachments( updateNodeArguments, persistedNode ) ).
            entityIndexConfig( updateNodeArguments.entityIndexConfig() != null
                                   ? updateNodeArguments.entityIndexConfig()
                                   : persistedNode.getEntityIndexConfig() );

        final Node updatedNode = updateNodeBuilder.build();

        final NodeStorageDocument nodeStorageDocument = NodeStorageDocumentFactory.update( updatedNode );

        elasticsearchIndexService.storeDocument( nodeStorageDocument );

        return updatedNode;
    }

    private Attachments syncronizeAttachments( final UpdateNodeArgs updateNodeArgs, final Node persistedNode )
    {

        final Attachments persistedAttachments = persistedNode.attachments();

        if ( updateNodeArgs.attachments() == null )
        {
            return persistedAttachments;
        }

        return updateNodeArgs.attachments();

    }

    public boolean move( final MoveNodeArguments moveNodeArguments )
    {
        final Node persistedNode = getById( moveNodeArguments.nodeToMove() );

        if ( persistedNode.path().equals( new NodePath( moveNodeArguments.parentPath(), moveNodeArguments.name() ) ) )
        {
            return false;
        }

        final DateTime now = DateTime.now();

        final Node movedNode = Node.newNode( persistedNode ).
            id( persistedNode.id() ).
            name( moveNodeArguments.name() ).
            parent( moveNodeArguments.parentPath() ).
            creator( persistedNode.creator() ).
            createdTime( persistedNode.getCreatedTime() ).
            modifiedTime( now ).
            modifier( moveNodeArguments.updater() ).
            rootDataSet( persistedNode.data() ).
            attachments( persistedNode.attachments() ).
            entityIndexConfig( moveNodeArguments.getEntityIndexConfig() != null
                                   ? moveNodeArguments.getEntityIndexConfig()
                                   : persistedNode.getEntityIndexConfig() ).
            build();

        final NodeStorageDocument nodeStorageDocument = NodeStorageDocumentFactory.update( movedNode );

        elasticsearchIndexService.storeDocument( nodeStorageDocument );

        return true;
    }

    public Node getById( final EntityId entityId )
    {
        final GetResponse getResponse = elasticsearchIndexService.get( ByIdQuery.byId( entityId.toString() ).
            index( Index.STORE ).
            indexType( IndexType.ENTITY ).
            build() );

        if ( !getResponse.isExists() )
        {
            throw new NodeNotFoundException( "Node with id " + entityId + " not found" );
        }

        return ElasticsearchResponseNodeTranslator.toNode( getResponse );
    }

    public Nodes getByIds( final EntityIds entityIds )
    {
        if ( entityIds.isEmpty() )
        {
            return Nodes.empty();
        }

        final ByIdsQuery.Builder builder = ByIdsQuery.
            byIds().
            index( Index.STORE ).
            indexType( IndexType.ENTITY );

        for ( final EntityId entityId : entityIds )
        {
            builder.add( new IndexDocumentId( entityId.toString() ) );
        }

        final SearchResponse searchResponse = elasticsearchIndexService.get( builder.build() );

        verifyGetResult( searchResponse, entityIds.getSize(), entityIds.getSize() );

        return ElasticsearchResponseNodeTranslator.toNodes( searchResponse );
    }

    public Node getByPath( final NodePath path )
    {
        final SearchResponse searchResponse = elasticsearchIndexService.get( ByPathQuery.byPath( path.toString() ).
            index( Index.STORE ).
            indexType( IndexType.ENTITY ).
            build() );

        verifyGetResult( searchResponse, 1, 1 );

        final Nodes nodes = ElasticsearchResponseNodeTranslator.toNodes( searchResponse );

        return nodes.get( 0 );
    }

    public Nodes getByPaths( final NodePaths paths )
    {
        final SearchResponse searchResponse = elasticsearchIndexService.get( ByPathsQuery.byPaths().
            setPaths( paths ).
            index( Index.STORE ).
            indexType( IndexType.ENTITY ).
            build() );

        verifyGetResult( searchResponse, paths.getSize(), paths.getSize() );

        return ElasticsearchResponseNodeTranslator.toNodes( searchResponse );
    }


    public Nodes getByParent( final NodePath parent )
    {
        final SearchResponse searchResponse = elasticsearchIndexService.get( ByParentPathQuery.byParentPath( parent.toString() ).
            index( Index.STORE ).
            indexType( IndexType.ENTITY ).
            build() );

        verifyGetResult( searchResponse, null, null );

        return ElasticsearchResponseNodeTranslator.toNodes( searchResponse );
    }

    public Node deleteById( final EntityId entityId )
    {
        final Node nodeToDelete = this.getById( entityId );

        doDeleteNodeWithChildren( nodeToDelete );

        return nodeToDelete;
    }

    public Node deleteByPath( final NodePath nodePath )
    {
        final Node nodeToDelete = this.getByPath( nodePath );

        doDeleteNodeWithChildren( nodeToDelete );

        return nodeToDelete;
    }

    private boolean doDeleteNodeWithChildren( final Node nodeToDelete )
    {
        final Nodes children = this.getByParent( nodeToDelete.path() );

        for ( final Node child : children )
        {
            elasticsearchIndexService.delete( new DeleteDocument( Index.STORE, IndexType.ENTITY, child.id().toString() ) );

        }

        return elasticsearchIndexService.delete( new DeleteDocument( Index.STORE, IndexType.ENTITY, nodeToDelete.id().toString() ) );
    }

    public void verifyGetResult( final SearchResponse searchResponse, final Integer min, final Integer max )
    {
        final int length = searchResponse.getHits().getHits().length;

        if ( min != null && length < min )
        {
            throw new IllegalArgumentException( "Expected at least: " + min + " results, actual " + length );
        }

        if ( max != null && length > max )
        {
            throw new IllegalArgumentException( "Expected at most: " + max + " results, actual " + length );
        }

    }

}
