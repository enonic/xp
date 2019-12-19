package com.enonic.xp.repo.impl.search;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.CompositeBucket;
import com.enonic.xp.aggregation.CompositeBucketAggregation;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.CompositeAggregationQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

@Component
public class NodeVersionDiffCompositeSearcher
{
    private SearchDao searchDao;

    public NodeVersionDiffCompositeSearcher( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }

    public SearchResult find( final NodeVersionDiffQuery query, final SearchSource source )
    {
        query.setSize( 0 );

        final Set<String> nodeIds = new HashSet<>();

        nodeIds.addAll( fetchNodeIds( query, source, false ) );
        nodeIds.addAll( fetchNodeIds( query, source, true ) );

        if ( nodeIds.size() > 0 )
        {
            if ( query.getVersionsSize() == 0 )
            {
                return SearchResult.create().
                    hits( SearchHits.create().build() ).
                    totalHits( nodeIds.size() ).
                    build();
            }
            else
            {
                return SearchResult.create().
                    hits( SearchHits.create().
                        addAll( nodeIds.stream().
                            map( nodeId -> SearchHit.create().
                                returnValues( ReturnValues.create().
                                    add( VersionIndexPath.NODE_ID.toString(), nodeId ).
                                    build() ).
                                id( nodeId ).
                                build() ).
                            collect( Collectors.toSet() ) ).
                        build() ).
                    totalHits( nodeIds.size() ).
                    build();
            }
        }
        else
        {
            return SearchResult.create().hits( SearchHits.create().build() ).build();
        }
    }

    private Set<String> fetchNodeIds( final NodeVersionDiffQuery query, final SearchSource source, final boolean deleted )
    {
        query.setDeleted( deleted );

        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            returnFields( ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID ) ).
            query( query ).
            build();

        final Set<String> nodeIds = new HashSet<>();

        Map<String, Object> after = null;

        while ( true )
        {
            query.setAggregations( AggregationQueries.create().
                add( CompositeAggregationQuery.create( "versions" ).
                    fieldName( BranchIndexPath.VERSION_ID.toString() ).
                    after( after ).
                    size( 10 ).
                    build() ).
                build() );

            SearchResult result = searchDao.search( searchRequest );

            CompositeBucketAggregation versionAgg = (CompositeBucketAggregation) result.getAggregations().get( "versions" );

            after = versionAgg.getAfter();

            Stream<Bucket> versionStream = versionAgg.getBuckets().stream();

            Set<String> currNodeIds = ( versionStream.
                filter( bucket -> bucket.getDocCount() == 1 ).
                map( bucket -> ( (CompositeBucket) bucket ).getKeys().get( "nodeid" ) ).
                collect( Collectors.toSet() ) );

            nodeIds.addAll( currNodeIds );

            if ( after == null )
            {
                break;
            }
        }

        return nodeIds;
    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
