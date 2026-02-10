package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.GetNodeVersionsResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

public class GetNodeVersionsCommand
{
    private final GetNodeVersionsParams params;

    private final NodeSearchService nodeSearchService;

    private GetNodeVersionsCommand( final Builder builder )
    {
        this.params = builder.params;
        this.nodeSearchService = builder.nodeSearchService;
    }

    public GetNodeVersionsResult execute()
    {
        final NodeVersionQuery.Builder queryBuilder = NodeVersionQuery.create()
            .size( params.getSize() )
            .nodeId( params.getNodeId() )
            .addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) )
            .addOrderBy( FieldOrderExpr.create( VersionIndexPath.VERSION_ID, OrderExpr.Direction.ASC ) );

        final String cursor = params.getCursor();
        if ( cursor != null )
        {
            final VersionCursorHelper.CursorData cursorData = VersionCursorHelper.decodeCursor( cursor );

            queryBuilder.addQueryFilter( BooleanFilter.create()
                                             .should( RangeFilter.create()
                                                          .fieldName( VersionIndexPath.TIMESTAMP.getPath() )
                                                          .lt( ValueFactory.newDateTime( cursorData.ts() ) )
                                                          .build() )
                                             .should( BooleanFilter.create()
                                                          .must( ValueFilter.create()
                                                                     .fieldName( VersionIndexPath.TIMESTAMP.getPath() )
                                                                     .addValue( ValueFactory.newDateTime( cursorData.ts() ) )
                                                                     .build() )
                                                          .must( RangeFilter.create()
                                                                     .fieldName( VersionIndexPath.VERSION_ID.getPath() )
                                                                     .gt( ValueFactory.newString( cursorData.id().toString() ) )
                                                                     .build() )
                                                          .build() )
                                             .build() );
        }

        final NodeVersionQueryResult queryResult =
            FindNodeVersionsCommand.create().query( queryBuilder.build() ).searchService( this.nodeSearchService ).build().execute();

        final NodeVersions versions = queryResult.getNodeVersions();

        final String nextCursor;
        if ( versions.getSize() > 0 && queryResult.getTotalHits() > versions.getSize() )
        {
            final NodeVersion last = Objects.requireNonNull( versions.last() );
            nextCursor = VersionCursorHelper.encodeCursor( new VersionCursorHelper.CursorData( last.getTimestamp(), last.getNodeVersionId()) );
        }
        else
        {
            nextCursor = null;
        }

        return GetNodeVersionsResult.create()
            .entityVersions( versions )
            .totalHits( queryResult.getTotalHits() )
            .cursor( nextCursor )
            .build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private GetNodeVersionsParams params;

        private NodeSearchService nodeSearchService;

        private Builder()
        {
        }

        public Builder params( final GetNodeVersionsParams params )
        {
            this.params = params;
            return this;
        }

        public Builder searchService( final NodeSearchService nodeSearchService )
        {
            this.nodeSearchService = nodeSearchService;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( this.nodeSearchService );
            Objects.requireNonNull( this.params, "params is required" );
        }

        public GetNodeVersionsCommand build()
        {
            this.validate();
            return new GetNodeVersionsCommand( this );
        }
    }
}
