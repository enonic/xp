package com.enonic.wem.repo.internal.elasticsearch.branch;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.elasticsearch.ReturnFields;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.version.VersionIndexPath;

public class GetNodeVersionIdsByIdsCommand
    extends AbstractBranchCommand
{
    private final Branch branch;

    private final NodeIds nodeIds;

    private GetNodeVersionIdsByIdsCommand( final Builder builder )
    {
        super( builder );
        branch = builder.branch;
        nodeIds = builder.nodeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    NodeVersionIds execute()
    {
        final String branchName = branch.getName();

        if ( nodeIds.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final ImmutableSet<String> nodeIdsAsStrings = nodeIds.getAsStrings();

        final TermsQueryBuilder idsQuery = new TermsQueryBuilder( BranchIndexPath.NODE_ID.getPath(), nodeIdsAsStrings );
        final BoolQueryBuilder boolQueryBuilder = joinWithBranchQuery( branchName, idsQuery );

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveStorageIndexName( repositoryId ) ).
            indexType( IndexType.VERSION.getName() ).
            query( boolQueryBuilder ).
            from( 0 ).
            size( this.nodeIds.getSize() ).
            addSortBuilder( new FieldSortBuilder( VersionIndexPath.TIMESTAMP.getPath() ).order( SortOrder.DESC ) ).
            setReturnFields( ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.NODE_ID ) ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Map<String, SearchResultFieldValue> orderedResultMap =
            getSearchResultFieldsWithPreservedOrder( this.branch, nodeIdsAsStrings, searchResult );

        return fieldValuesToVersionIds( orderedResultMap.values() );
    }


    private Map<String, SearchResultFieldValue> getSearchResultFieldsWithPreservedOrder( final Branch branch,
                                                                                         final Set<String> nodeIdsAsStrings,
                                                                                         final SearchResult searchResult )
    {
        return Maps.asMap( nodeIdsAsStrings,
                           new NodeIdToSearchResultFieldMapper( searchResult, BranchIndexPath.VERSION_ID.getPath(), branch ) );
    }

    public static final class Builder
        extends AbstractBranchCommand.Builder<Builder>
    {
        private Branch branch;

        private NodeIds nodeIds;

        private Builder()
        {
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder nodeIds( final NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        public GetNodeVersionIdsByIdsCommand build()
        {
            return new GetNodeVersionIdsByIdsCommand( this );
        }
    }

    private final class NodeIdToSearchResultFieldMapper
        implements com.google.common.base.Function<String, SearchResultFieldValue>
    {
        private final SearchResult searchResult;

        private final String fieldName;

        private final Branch branch;

        private NodeIdToSearchResultFieldMapper( final SearchResult searchResult, final String fieldName, final Branch branch )
        {
            this.searchResult = searchResult;
            this.fieldName = fieldName;
            this.branch = branch;
        }

        @Override
        public SearchResultFieldValue apply( final String nodeId )
        {
            final BranchDocumentId branchDocumentId = new BranchDocumentId( NodeId.from( nodeId ), this.branch );

            final SearchResultEntry entry = this.searchResult.getEntry( branchDocumentId.toString() );
            return entry != null ? entry.getField( fieldName ) : null;
        }
    }
}
