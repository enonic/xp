package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Collection;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.repository.StorageNameResolver;

abstract class AbstractWorkspaceCommand
{
    static final boolean DEFAULT_REFRESH = true;

    static final int DEFAULT_UNKNOWN_SIZE = 1000;

    private static final String BUILTIN_TIMESTAMP_FIELD = "_timestamp";

    final ElasticsearchDao elasticsearchDao;

    final Repository repository;

    AbstractWorkspaceCommand( final Builder builder )
    {
        this.elasticsearchDao = builder.elasticsearchDao;
        this.repository = builder.repository;
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

    QueryMetaData createGetBlobKeyQueryMetaData( final int numberOfHits, final Repository repository )
    {
        final SortBuilder fieldSortBuilder = new FieldSortBuilder( BUILTIN_TIMESTAMP_FIELD ).order( SortOrder.DESC );

        return QueryMetaData.create( StorageNameResolver.resolveStorageIndexName( repository ) ).
            indexTypeName( IndexType.WORKSPACE.getName() ).
            from( 0 ).
            size( numberOfHits ).
            addField( WorkspaceXContentBuilderFactory.ENTITY_ID_FIELD_NAME ).
            addField( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME ).
            addSort( fieldSortBuilder ).
            build();
    }

    protected TermQueryBuilder createWorkspaceQuery( final Workspace workspace )
    {
        return new TermQueryBuilder( WorkspaceXContentBuilderFactory.WORKSPACE_FIELD_NAME, workspace.getName() );
    }

    static abstract class Builder<B extends Builder>
    {
        private ElasticsearchDao elasticsearchDao;

        private Repository repository;

        @SuppressWarnings("unchecked")
        B elasticsearchDao( final ElasticsearchDao elasticsearchDao )
        {
            this.elasticsearchDao = elasticsearchDao;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B repository( final Repository repository )
        {
            this.repository = repository;
            return (B) this;
        }

    }

}
