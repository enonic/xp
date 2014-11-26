package com.enonic.wem.repo.internal.elasticsearch.workspace;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodeVersionIds;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.elasticsearch.ReturnFields;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.wem.repo.internal.workspace.WorkspaceDocumentId;

public class GetNodeVersionIdsByIdsCommand
    extends AbstractWorkspaceCommand
{
    private final Workspace workspace;

    private final NodeIds nodeIds;

    private GetNodeVersionIdsByIdsCommand( final Builder builder )
    {
        super( builder );
        workspace = builder.workspace;
        nodeIds = builder.nodeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    NodeVersionIds execute()
    {
        final String workspaceName = workspace.getName();

        if ( nodeIds.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final ImmutableSet<String> nodeIdsAsStrings = nodeIds.getAsStrings();

        final TermsQueryBuilder idsQuery = new TermsQueryBuilder( WorkspaceIndexPath.NODE_ID.getPath(), nodeIdsAsStrings );
        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( workspaceName, idsQuery );

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( StorageNameResolver.resolveStorageIndexName( repositoryId ) ).
            indexType( IndexType.VERSION.getName() ).
            query( boolQueryBuilder ).
            from( 0 ).
            size( this.nodeIds.getSize() ).
            addSortBuilder( new FieldSortBuilder( VersionIndexPath.TIMESTAMP.getPath() ).order( SortOrder.DESC ) ).
            setReturnFields( ReturnFields.from( WorkspaceIndexPath.NODE_ID, WorkspaceIndexPath.VERSION_ID ) ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Map<String, SearchResultFieldValue> orderedResultMap =
            getSearchResultFieldsWithPreservedOrder( this.workspace, nodeIdsAsStrings, searchResult );

        return fieldValuesToVersionIds( orderedResultMap.values() );
    }


    private Map<String, SearchResultFieldValue> getSearchResultFieldsWithPreservedOrder( final Workspace workspace,
                                                                                         final Set<String> nodeIdsAsStrings,
                                                                                         final SearchResult searchResult )
    {
        return Maps.asMap( nodeIdsAsStrings,
                           new NodeIdToSearchResultFieldMapper( searchResult, WorkspaceIndexPath.VERSION_ID.getPath(), workspace ) );
    }

    private final class NodeIdToSearchResultFieldMapper
        implements com.google.common.base.Function<String, SearchResultFieldValue>
    {
        private final SearchResult searchResult;

        private final String fieldName;

        private final Workspace workspace;

        private NodeIdToSearchResultFieldMapper( final SearchResult searchResult, final String fieldName, final Workspace workspace )
        {
            this.searchResult = searchResult;
            this.fieldName = fieldName;
            this.workspace = workspace;
        }

        @Override
        public SearchResultFieldValue apply( final String nodeId )
        {
            final WorkspaceDocumentId workspaceDocumentId = new WorkspaceDocumentId( NodeId.from( nodeId ), this.workspace );

            final SearchResultEntry entry = this.searchResult.getEntry( workspaceDocumentId.toString() );
            return entry != null ? entry.getField( fieldName ) : null;
        }
    }


    public static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private Workspace workspace;

        private NodeIds nodeIds;

        private Builder()
        {
        }

        public Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
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
}
