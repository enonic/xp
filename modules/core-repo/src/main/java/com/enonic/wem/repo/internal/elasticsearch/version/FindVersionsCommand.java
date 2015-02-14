package com.enonic.wem.repo.internal.elasticsearch.version;

import com.enonic.xp.core.node.FindNodeVersionsResult;
import com.enonic.xp.core.node.NodeVersions;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.version.GetVersionsQuery;

class FindVersionsCommand
    extends AbstractVersionsCommand
{
    private final GetVersionsQuery query;

    private FindVersionsCommand( final Builder builder )
    {
        super( builder );
        this.query = builder.query;
    }

    FindNodeVersionsResult execute()
    {
        final SearchResult searchResults = doGetFromNodeId( query.getNodeId(), query.getFrom(), query.getSize(), repositoryId );

        final FindNodeVersionsResult.Builder findEntityVersionResultBuilder = FindNodeVersionsResult.create();

        findEntityVersionResultBuilder.hits( searchResults.getResults().getSize() );
        findEntityVersionResultBuilder.totalHits( searchResults.getResults().getTotalHits() );
        findEntityVersionResultBuilder.from( query.getFrom() );
        findEntityVersionResultBuilder.to( query.getSize() );

        final NodeVersions nodeVersions = buildEntityVersions( query, searchResults );

        findEntityVersionResultBuilder.entityVersions( nodeVersions );

        return findEntityVersionResultBuilder.build();
    }

    private NodeVersions buildEntityVersions( final GetVersionsQuery query, final SearchResult searchResults )
    {
        final NodeVersions.Builder entityVersionsBuilder = NodeVersions.create( query.getNodeId() );

        for ( final SearchResultEntry searchResult : searchResults.getResults() )
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
        private GetVersionsQuery query;

        Builder()
        {
            super();
        }

        Builder query( final GetVersionsQuery query )
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
