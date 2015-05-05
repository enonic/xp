package com.enonic.wem.repo.internal.elasticsearch.branch;

import java.time.Instant;

import org.elasticsearch.index.query.QueryBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.branch.BranchContext;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.GetQuery;
import com.enonic.wem.repo.internal.elasticsearch.ReturnFields;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.query.filter.ValueFilter;

@Component
public class ElasticsearchBranchService
    implements BranchService
{
    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final StoreBranchDocument storeBranchDocument, final BranchContext context )
    {
        StoreBranchDocumentCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            branch( context.getBranch() ).
            repository( context.getRepositoryId() ).
            document( storeBranchDocument ).
            build().
            execute();
    }

    @Override
    public void delete( final NodeId nodeId, final BranchContext context )
    {
        DeleteNodeVersionCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepositoryId() ).
            branch( context.getBranch() ).
            nodeId( nodeId ).
            build().
            execute();
    }

    @Override
    public NodeBranchVersion get( final NodeId nodeId, final BranchContext context )
    {
        final BranchDocumentId branchDocumentId = new BranchDocumentId( nodeId, context.getBranch() );

        final GetResult getResult = this.elasticsearchDao.get( GetQuery.create().
            id( branchDocumentId.toString() ).
            indexName( IndexNameResolver.resolveStorageIndexName( context.getRepositoryId() ) ).
            indexTypeName( IndexType.BRANCH.getName() ).
            returnFields(
                ReturnFields.from( BranchIndexPath.VERSION_ID, BranchIndexPath.STATE, BranchIndexPath.PATH, BranchIndexPath.TIMESTAMP ) ).
            routing( nodeId.toString() ).
            build() );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        final NodeReturnValue nodeReturnValue = NodeReturnValue.from( getResult );

        return createFromReturnValue( nodeReturnValue );
    }

    @Override
    public NodeBranchQueryResult findAll( final NodeBranchQuery nodeBranchQuery, final BranchContext context )
    {
        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( ValueFilter.create().
                fieldName( BranchIndexPath.BRANCH_NAME.getPath() ).
                addValue( Value.newString( context.getBranch().getName() ) ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveStorageIndexName( context.getRepositoryId() ) ).
            indexType( IndexType.BRANCH.getName() ).
            query( queryBuilder ).
            size( nodeBranchQuery.getSize() ).
            from( nodeBranchQuery.getFrom() ).
            setReturnFields( ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID ) ).
            build();

        final SearchResult searchResult = this.elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return NodeBranchQueryResult.empty();
        }

        return NodeBranchQueryResultFactory.create( searchResult );
    }


    private NodeBranchVersion createFromReturnValue( final NodeReturnValue nodeReturnValue )
    {
        return NodeBranchVersion.create().
            timestamp( nodeReturnValue.timestamp ).
            nodePath( nodeReturnValue.nodePath ).
            nodeVersionId( nodeReturnValue.getNodeVersionId() ).
            nodeState( nodeReturnValue.getState() ).
            build();
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }

    private static class NodeReturnValue
    {
        private final NodePath nodePath;

        private final NodeVersionId nodeVersionId;

        private final NodeState state;

        private final Instant timestamp;

        public NodeReturnValue( final Instant timestamp, final NodePath nodePath, final NodeVersionId nodeVersionId, final NodeState state )
        {
            this.timestamp = timestamp;
            this.nodePath = nodePath;
            this.nodeVersionId = nodeVersionId;
            this.state = state;
        }

        public static NodeReturnValue from( final SearchResultEntry searchResultEntry )
        {
            final SearchResultFieldValue timestamp = searchResultEntry.getField( BranchIndexPath.TIMESTAMP.getPath() );
            final SearchResultFieldValue nodeVersionIdValue = searchResultEntry.getField( BranchIndexPath.VERSION_ID.getPath() );
            final SearchResultFieldValue nodePathValue = searchResultEntry.getField( BranchIndexPath.PATH.getPath() );
            final SearchResultFieldValue stateValue = searchResultEntry.getField( BranchIndexPath.STATE.getPath() );

            return createNodeReturnValue( timestamp, nodeVersionIdValue, nodePathValue, stateValue );
        }

        public static NodeReturnValue from( final GetResult getResult )
        {
            final SearchResultFieldValue timestamp = getResult.getSearchResult().getField( BranchIndexPath.TIMESTAMP.getPath() );
            final SearchResultFieldValue nodeVersionIdValue = getResult.getSearchResult().getField( BranchIndexPath.VERSION_ID.getPath() );
            final SearchResultFieldValue nodePathValue = getResult.getSearchResult().getField( BranchIndexPath.PATH.getPath() );
            final SearchResultFieldValue stateValue = getResult.getSearchResult().getField( BranchIndexPath.STATE.getPath() );

            return createNodeReturnValue( timestamp, nodeVersionIdValue, nodePathValue, stateValue );
        }

        private static NodeReturnValue createNodeReturnValue( final SearchResultFieldValue timestamp,
                                                              final SearchResultFieldValue nodeVersionIdValue,
                                                              final SearchResultFieldValue nodePathValue,
                                                              final SearchResultFieldValue stateValue )
        {
            Preconditions.checkNotNull( timestamp, "Expected value '" + BranchIndexPath.TIMESTAMP.getPath() + "' in getResult " );
            Preconditions.checkNotNull( nodeVersionIdValue, "Expected value '" + BranchIndexPath.VERSION_ID.getPath() + "' in getResult " );
            Preconditions.checkNotNull( nodePathValue, "Expected value '" + BranchIndexPath.PATH.getPath() + "' in getResult " );
            Preconditions.checkNotNull( stateValue, "Expected value '" + BranchIndexPath.STATE.getPath() + "' in getResult " );

            return new NodeReturnValue( Instant.ofEpochMilli( (Long) timestamp.getValue() ),
                                        NodePath.newPath( nodePathValue.getValue().toString() ).build(),
                                        NodeVersionId.from( nodeVersionIdValue.getValue().toString() ),
                                        NodeState.from( stateValue.getValue().toString() ) );
        }

        public NodePath getNodePath()
        {
            return nodePath;
        }

        public NodeVersionId getNodeVersionId()
        {
            return nodeVersionId;
        }

        public NodeState getState()
        {
            return state;
        }

        public Instant getTimestamp()
        {
            return timestamp;
        }
    }
}

