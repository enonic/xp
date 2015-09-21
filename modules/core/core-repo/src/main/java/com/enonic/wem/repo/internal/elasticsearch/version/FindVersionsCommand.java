package com.enonic.wem.repo.internal.elasticsearch.version;

import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.result.SearchHit;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.wem.repo.internal.version.FindVersionsQuery;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repository.RepositoryId;

class FindVersionsCommand
    extends AbstractVersionsCommand
{
    private final FindVersionsQuery query;

    private FindVersionsCommand( final Builder builder )
    {
        super( builder );
        this.query = builder.query;
    }

    FindNodeVersionsResult execute()
    {
        final SearchResult searchResults = doGetFromNodeId( query.getNodeId(), query.getFrom(), query.getSize(), repositoryId );

        final FindNodeVersionsResult.Builder findNodeVersionsResult = FindNodeVersionsResult.create();

        findNodeVersionsResult.hits( searchResults.getResults().getSize() );
        findNodeVersionsResult.totalHits( searchResults.getResults().getTotalHits() );
        findNodeVersionsResult.from( query.getFrom() );
        findNodeVersionsResult.to( query.getSize() );

        final NodeVersions nodeVersions = buildEntityVersions( query, searchResults );

        findNodeVersionsResult.entityVersions( nodeVersions );

        return findNodeVersionsResult.build();
    }

    private SearchResult doGetFromNodeId( final NodeId id, final int from, final int size, final RepositoryId repositoryId )
    {
        final TermQueryBuilder nodeIdQuery = new TermQueryBuilder( VersionIndexPath.NODE_ID.getPath(), id.toString() );

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveStorageIndexName( repositoryId ) ).
            indexType( IndexType.VERSION.getName() ).
            query( nodeIdQuery ).
            from( from ).
            size( size ).
            addSortBuilder( new FieldSortBuilder( VersionIndexPath.TIMESTAMP.getPath() ).order( SortOrder.DESC ) ).
            setReturnFields( ReturnFields.from( VersionIndexPath.TIMESTAMP, VersionIndexPath.VERSION_ID, VersionIndexPath.NODE_ID ) ).
            build();

        final SearchResult searchResults = elasticsearchDao.find( query );

        if ( searchResults.isEmpty() )
        {
            throw new NodeNotFoundException( "Did not find version entry with id: " + id );
        }
        return searchResults;
    }

    private NodeVersions buildEntityVersions( final FindVersionsQuery query, final SearchResult searchResults )
    {
        final NodeVersions.Builder entityVersionsBuilder = NodeVersions.create( query.getNodeId() );

        for ( final SearchHit searchResult : searchResults.getResults() )
        {
            entityVersionsBuilder.add( createVersionEntry( searchResult ) );
        }

        return entityVersionsBuilder.build();
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends AbstractVersionsCommand.Builder<Builder>
    {
        private FindVersionsQuery query;

        Builder()
        {
            super();
        }

        Builder query( final FindVersionsQuery query )
        {
            this.query = query;
            return this;
        }

        FindVersionsCommand build()
        {
            return new FindVersionsCommand( this );
        }
    }
}
