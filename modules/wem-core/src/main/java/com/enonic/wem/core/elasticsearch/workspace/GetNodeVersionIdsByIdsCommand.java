package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.elasticsearch.xcontent.WorkspaceXContentBuilderFactory;
import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.EntityIds;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.workspace.WorkspaceDocumentId;

public class GetNodeVersionIdsByIdsCommand
    extends AbstractWorkspaceCommand
{
    private final Workspace workspace;

    private final EntityIds entityIds;

    private GetNodeVersionIdsByIdsCommand( final Builder builder )
    {
        super( builder );
        workspace = builder.workspace;
        entityIds = builder.entityIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    NodeVersionIds execute()
    {
        final String workspaceName = workspace.getName();

        if ( entityIds.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final ImmutableSet<String> entityIdsAsStrings = entityIds.getAsStrings();

        final TermsQueryBuilder idsQuery =
            new TermsQueryBuilder( WorkspaceXContentBuilderFactory.ENTITY_ID_FIELD_NAME, entityIdsAsStrings );
        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( workspaceName, idsQuery );
        final QueryMetaData queryMetaData = createGetBlobKeyQueryMetaData( entityIdsAsStrings.size(), this.repositoryId );

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, boolQueryBuilder );

        if ( searchResult.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Map<String, SearchResultField> orderedResultMap =
            getSearchResultFieldsWithPreservedOrder( this.workspace, entityIdsAsStrings, searchResult );

        return fieldValuesToVersionIds( orderedResultMap.values() );
    }


    private Map<String, SearchResultField> getSearchResultFieldsWithPreservedOrder( final Workspace workspace,
                                                                                    final Set<String> entityIdsAsStrings,
                                                                                    final SearchResult searchResult )
    {
        return Maps.asMap( entityIdsAsStrings,
                           new EntityIdToSearchResultFieldMapper( searchResult, WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME,
                                                                  workspace ) );
    }

    private final class EntityIdToSearchResultFieldMapper
        implements com.google.common.base.Function<String, SearchResultField>
    {
        private final SearchResult searchResult;

        private final String fieldName;

        private final Workspace workspace;

        private EntityIdToSearchResultFieldMapper( final SearchResult searchResult, final String fieldName, final Workspace workspace )
        {
            this.searchResult = searchResult;
            this.fieldName = fieldName;
            this.workspace = workspace;
        }

        @Override
        public SearchResultField apply( final String entityId )
        {
            final WorkspaceDocumentId workspaceDocumentId = new WorkspaceDocumentId( EntityId.from( entityId ), this.workspace );

            final SearchResultEntry entry = this.searchResult.getEntry( workspaceDocumentId.toString() );
            return entry != null ? entry.getField( fieldName ) : null;
        }
    }


    public static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private Workspace workspace;

        private EntityIds entityIds;

        private Builder()
        {
        }

        public Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder entityIds( final EntityIds entityIds )
        {
            this.entityIds = entityIds;
            return this;
        }

        public GetNodeVersionIdsByIdsCommand build()
        {
            return new GetNodeVersionIdsByIdsCommand( this );
        }
    }
}
