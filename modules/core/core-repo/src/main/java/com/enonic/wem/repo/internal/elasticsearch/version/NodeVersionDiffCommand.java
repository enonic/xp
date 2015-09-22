package com.enonic.wem.repo.internal.elasticsearch.version;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.HasChildQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.branch.BranchIndexPath;
import com.enonic.wem.repo.internal.storage.result.SearchHit;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.FindNodesWithVersionDifferenceParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionDiffResult;

class NodeVersionDiffCommand
    extends AbstractVersionsCommand
{
    private final FindNodesWithVersionDifferenceParams query;

    private NodeVersionDiffCommand( final Builder builder )
    {
        super( builder );
        query = builder.query;
    }

    static Builder create()
    {
        return new Builder();
    }

    NodeVersionDiffResult execute()
    {
        final String indexType = IndexType.BRANCH.getName();

        final BoolQueryBuilder query = createQuery( indexType );

        final ElasticsearchQuery esQuery = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            indexType( IndexType.VERSION.getName() ).
            query( query ).
            setReturnFields( ReturnFields.from( VersionIndexPath.NODE_ID, VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP ) ).
            size( this.query.getSize() ).
            from( this.query.getFrom() ).
            addSortBuilder( new FieldSortBuilder( VersionIndexPath.NODE_PATH.getPath() ).order( SortOrder.ASC ) ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( esQuery );

        final NodeVersionDiffResult.Builder builder = NodeVersionDiffResult.create();

        for ( final SearchHit entry : searchResult.getResults() )
        {
            builder.add( NodeId.from( entry.getField( VersionIndexPath.NODE_ID.toString() ).getSingleValue().toString() ) );
        }

        return builder.build();
    }

    private BoolQueryBuilder createQuery( final String indexType )
    {
        final Branch sourceWs = this.query.getSource();
        final Branch targetWs = this.query.getTarget();

        final BoolQueryBuilder inSourceOnly = onlyInQuery( indexType, sourceWs, targetWs );

        final BoolQueryBuilder inTargetOnly = onlyInQuery( indexType, targetWs, sourceWs );

        final BoolQueryBuilder deletedInSourceOnly = deletedOnlyQuery( indexType, sourceWs, targetWs );

        final BoolQueryBuilder deletedInTargetOnly = deletedOnlyQuery( indexType, targetWs, sourceWs );

        final BoolQueryBuilder sourceTargetCompares =
            joinOnlyInQueries( inSourceOnly, inTargetOnly, deletedInSourceOnly, deletedInTargetOnly );

        return wrapInPathQueryIfNecessary( indexType, sourceTargetCompares );
    }

    private BoolQueryBuilder deletedOnlyQuery( final String indexType, final Branch sourceBranch, final Branch targetBranch )
    {
        return new BoolQueryBuilder().
            must( deletedInBranch( indexType, sourceBranch ) ).
            mustNot( deletedInBranch( indexType, targetBranch ) );
    }

    private BoolQueryBuilder onlyInQuery( final String indexType, final Branch sourceWs, final Branch targetWs )
    {
        return new BoolQueryBuilder().
            must( isInBranch( indexType, sourceWs ) ).
            mustNot( isInBranch( indexType, targetWs ) );
    }

    private BoolQueryBuilder wrapInPathQueryIfNecessary( final String indexType, final BoolQueryBuilder sourceTargetCompares )
    {
        if ( this.query.getNodePath() != null && !this.query.getNodePath().isRoot() )
        {
            return new BoolQueryBuilder().
                must( hasPath( indexType ) ).
                must( sourceTargetCompares );
        }
        return sourceTargetCompares;
    }

    private BoolQueryBuilder joinOnlyInQueries( final BoolQueryBuilder inSourceOnly, final BoolQueryBuilder inTargetOnly,
                                                final BoolQueryBuilder deletedInSourceOnly, final BoolQueryBuilder deletedInTargetOnly )
    {
        return new BoolQueryBuilder().
            should( inSourceOnly ).
            should( inTargetOnly ).
            should( deletedInSourceOnly ).
            should( deletedInTargetOnly );
    }

    private HasChildQueryBuilder hasPath( final String indexType )
    {
        final String queryPath = this.query.getNodePath().toString();

        final BoolQueryBuilder pathQuery = new BoolQueryBuilder().
            should( new WildcardQueryBuilder( BranchIndexPath.PATH.getPath(),
                                              queryPath.endsWith( "/" ) ? queryPath + "*" : queryPath + "/*" ) ).
            should( new TermQueryBuilder( BranchIndexPath.PATH.getPath(), queryPath ) );

        return new HasChildQueryBuilder( indexType, pathQuery );
    }

    private HasChildQueryBuilder deletedInBranch( final String indexType, final Branch sourceBranch )
    {
        return new HasChildQueryBuilder( indexType, new BoolQueryBuilder().
            must( isDeleted() ).
            must( new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.toString(), sourceBranch.getName() ) ) );
    }

    private TermQueryBuilder isDeleted()
    {
        return new TermQueryBuilder( BranchIndexPath.STATE.toString(), NodeState.PENDING_DELETE.value() );
    }

    private HasChildQueryBuilder isInBranch( final String indexType, final Branch source )
    {
        return new HasChildQueryBuilder( indexType, createWsConstraint( source ) );
    }

    private TermQueryBuilder createWsConstraint( final Branch branch )
    {
        return new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.toString(), branch );
    }

    static final class Builder
        extends AbstractVersionsCommand.Builder<Builder>
    {
        private FindNodesWithVersionDifferenceParams query;

        private Builder()
        {
        }

        Builder query( FindNodesWithVersionDifferenceParams query )
        {
            this.query = query;
            return this;
        }

        NodeVersionDiffCommand build()
        {
            return new NodeVersionDiffCommand( this );
        }
    }
}