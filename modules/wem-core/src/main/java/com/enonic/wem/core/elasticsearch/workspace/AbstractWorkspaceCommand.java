package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Collection;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.elasticsearch.xcontent.WorkspaceXContentBuilderFactory;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.repository.StorageNameResolver;

abstract class AbstractWorkspaceCommand
{
    static final boolean DEFAULT_REFRESH = true;

    static final int DEFAULT_UNKNOWN_SIZE = 1000;

    private static final String BUILTIN_TIMESTAMP_FIELD = "_timestamp";

    final ElasticsearchDao elasticsearchDao;

    final RepositoryId repositoryId;

    AbstractWorkspaceCommand( final Builder builder )
    {
        this.elasticsearchDao = builder.elasticsearchDao;
        this.repositoryId = builder.repositoryId;
    }

    NodeVersionIds fieldValuesToVersionIds( final Collection<SearchResultField> fieldValues )
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();

        for ( final SearchResultField searchResultField : fieldValues )
        {
            if ( searchResultField == null )
            {
                continue;
            }

            builder.add( NodeVersionId.from( searchResultField.getValue().toString() ) );
        }
        return builder.build();
    }

    BoolQueryBuilder joinWithWorkspaceQuery( final String workspaceName, final QueryBuilder specificQuery )
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        final TermQueryBuilder workspaceQuery = new TermQueryBuilder( WorkspaceXContentBuilderFactory.WORKSPACE_FIELD_NAME, workspaceName );
        boolQueryBuilder.must( specificQuery );
        boolQueryBuilder.must( workspaceQuery );

        return boolQueryBuilder;
    }

    BoolQueryBuilder join( final QueryBuilder query1, final QueryBuilder query2 )
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must( query1 );
        boolQueryBuilder.must( query2 );

        return boolQueryBuilder;
    }

    QueryMetaData createGetBlobKeyQueryMetaData( final int numberOfHits, final RepositoryId repositoryId )
    {
        final SortBuilder fieldSortBuilder = new FieldSortBuilder( BUILTIN_TIMESTAMP_FIELD ).order( SortOrder.DESC );

        return QueryMetaData.create( StorageNameResolver.resolveStorageIndexName( repositoryId ) ).
            indexTypeName( IndexType.WORKSPACE.getName() ).
            from( 0 ).
            size( numberOfHits ).
            addField( WorkspaceXContentBuilderFactory.NODE_ID_FIELD_NAME ).
            addField( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME ).
            addSort( fieldSortBuilder ).
            build();
    }

    TermQueryBuilder createWorkspaceQuery( final Workspace workspace )
    {
        return new TermQueryBuilder( WorkspaceXContentBuilderFactory.WORKSPACE_FIELD_NAME, workspace.getName() );
    }

    static abstract class Builder<B extends Builder>
    {
        private ElasticsearchDao elasticsearchDao;

        private RepositoryId repositoryId;

        @SuppressWarnings("unchecked")
        B elasticsearchDao( final ElasticsearchDao elasticsearchDao )
        {
            this.elasticsearchDao = elasticsearchDao;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B repository( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return (B) this;
        }

    }

}
