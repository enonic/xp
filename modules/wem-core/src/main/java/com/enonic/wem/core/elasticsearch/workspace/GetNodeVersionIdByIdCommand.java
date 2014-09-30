package com.enonic.wem.core.elasticsearch.workspace;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchDataException;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.repository.StorageNameResolver;

class GetNodeVersionIdByIdCommand
    extends AbstractWorkspaceCommand
{
    private final EntityId entityId;

    private final Workspace workspace;

    private GetNodeVersionIdByIdCommand( final Builder builder )
    {
        super( builder );
        entityId = builder.entityId;
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
                    entityId +
                    " in workspace " + workspace.getName() );
        }

        return NodeVersionId.from( field.getValue().toString() );
    }

    private SearchResultEntry executeQuery()
    {
        final TermQueryBuilder idQuery = new TermQueryBuilder( WorkspaceXContentBuilderFactory.ENTITY_ID_FIELD_NAME, entityId.toString() );

        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( workspace.getName(), idQuery );

        final QueryMetaData queryMetaData = QueryMetaData.create( StorageNameResolver.resolveStorageIndexName( repository ) ).
            indexTypeName( IndexType.WORKSPACE.getName() ).
            from( 0 ).
            size( 1 ).
            addFields( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME ).
            build();

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, boolQueryBuilder );

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
        private EntityId entityId;

        private Workspace workspace;

        private Builder()
        {
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
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
