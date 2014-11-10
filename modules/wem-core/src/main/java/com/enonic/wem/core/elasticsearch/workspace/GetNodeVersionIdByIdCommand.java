package com.enonic.wem.core.elasticsearch.workspace;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchDataException;
import com.enonic.wem.core.elasticsearch.QueryProperties;
import com.enonic.wem.core.elasticsearch.xcontent.WorkspaceXContentBuilderFactory;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.repository.StorageNameResolver;

class GetNodeVersionIdByIdCommand
    extends AbstractWorkspaceCommand
{
    private final NodeId nodeId;

    private final Workspace workspace;

    private GetNodeVersionIdByIdCommand( final Builder builder )
    {
        super( builder );
        nodeId = builder.nodeId;
        workspace = builder.workspace;
    }

    public NodeVersionId execute()
    {
        final SearchResultEntry searchResultEntry = executeQuery();

        if ( searchResultEntry == null )
        {
            return null;
        }

        final SearchResultField field = searchResultEntry.getField( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME );

        if ( field == null || field.getValue() == null )
        {
            throw new ElasticsearchDataException(
                "Field " + WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME + " not found on node with id " +
                    nodeId +
                    " in workspace " + workspace.getName() );
        }

        return NodeVersionId.from( field.getValue().toString() );
    }

    private SearchResultEntry executeQuery()
    {
        final TermQueryBuilder idQuery = new TermQueryBuilder( WorkspaceXContentBuilderFactory.NODE_ID_FIELD_NAME, nodeId.toString() );

        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( workspace.getName(), idQuery );

        final QueryProperties queryProperties = QueryProperties.create( StorageNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            indexTypeName( IndexType.WORKSPACE.getName() ).
            from( 0 ).
            size( 1 ).
            addFields( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME ).
            build();

        final SearchResult searchResult = elasticsearchDao.search( queryProperties, boolQueryBuilder );

        if ( searchResult.isEmpty() )
        {
            return null;
        }

        return searchResult.getResults().getFirstHit();
    }

    public static Builder create()
    {
        return new Builder();
    }

    static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private Workspace workspace;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public GetNodeVersionIdByIdCommand build()
        {
            return new GetNodeVersionIdByIdCommand( this );
        }
    }
}
