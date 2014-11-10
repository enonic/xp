package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.QueryProperties;
import com.enonic.wem.core.elasticsearch.xcontent.WorkspaceXContentBuilderFactory;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeIds;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.workspace.WorkspaceDocumentId;

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

        final TermsQueryBuilder idsQuery = new TermsQueryBuilder( WorkspaceXContentBuilderFactory.NODE_ID_FIELD_NAME, nodeIdsAsStrings );
        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( workspaceName, idsQuery );
        final QueryProperties queryProperties = createGetBlobKeyQueryMetaData( nodeIdsAsStrings.size(), this.repositoryId );

        final SearchResult searchResult = elasticsearchDao.search( queryProperties, boolQueryBuilder );

        if ( searchResult.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Map<String, SearchResultField> orderedResultMap =
            getSearchResultFieldsWithPreservedOrder( this.workspace, nodeIdsAsStrings, searchResult );

        return fieldValuesToVersionIds( orderedResultMap.values() );
    }


    private Map<String, SearchResultField> getSearchResultFieldsWithPreservedOrder( final Workspace workspace,
                                                                                    final Set<String> nodeIdsAsStrings,
                                                                                    final SearchResult searchResult )
    {
        return Maps.asMap( nodeIdsAsStrings,
                           new NodeIdToSearchResultFieldMapper( searchResult, WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME,
                                                                workspace ) );
    }

    private final class NodeIdToSearchResultFieldMapper
        implements com.google.common.base.Function<String, SearchResultField>
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
        public SearchResultField apply( final String nodeId )
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
