package com.enonic.wem.repo.internal.elasticsearch.version;

import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.xp.core.index.IndexType;
import com.enonic.xp.core.node.NodeVersion;
import com.enonic.xp.core.node.NodeVersionId;
import com.enonic.xp.core.repository.RepositoryId;
import com.enonic.wem.repo.internal.elasticsearch.ReturnFields;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.version.VersionIndexPath;

class GetVersionCommand
    extends AbstractVersionsCommand
{
    private final NodeVersionId nodeVersionId;

    private GetVersionCommand( final Builder builder )
    {
        super( builder );
        nodeVersionId = builder.nodeVersionId;
    }

    static Builder create()
    {
        return new Builder();
    }

    NodeVersion execute()
    {
        final SearchResult searchResult = doGetFromVersionIdNew( nodeVersionId, repositoryId );

        final SearchResultEntry searchHit = searchResult.getResults().getFirstHit();

        return createVersionEntry( searchHit );
    }

    private SearchResult doGetFromVersionIdNew( final NodeVersionId nodeVersionId, final RepositoryId repositoryId )
    {
        final TermQueryBuilder blobKeyQuery = new TermQueryBuilder( VersionIndexPath.VERSION_ID.getPath(), nodeVersionId.toString() );

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveStorageIndexName( repositoryId ) ).
            indexType( IndexType.VERSION.getName() ).
            query( blobKeyQuery ).
            from( 0 ).
            size( 1 ).
            addSortBuilder( new FieldSortBuilder( VersionIndexPath.TIMESTAMP.getPath() ).order( SortOrder.DESC ) ).
            setReturnFields( ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP ) ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            throw new RuntimeException( "Did not find version entry with blobKey: " + nodeVersionId );
        }
        return searchResult;
    }

    static final class Builder
        extends AbstractVersionsCommand.Builder<Builder>
    {
        private NodeVersionId nodeVersionId;

        private Builder()
        {
        }

        Builder nodeVersionId( NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        GetVersionCommand build()
        {
            return new GetVersionCommand( this );
        }
    }
}
