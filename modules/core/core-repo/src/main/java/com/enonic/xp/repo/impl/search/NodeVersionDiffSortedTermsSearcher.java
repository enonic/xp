package com.enonic.xp.repo.impl.search;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
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
public class NodeVersionDiffSortedTermsSearcher
{

    private SearchDao searchDao;

    public NodeVersionDiffSortedTermsSearcher( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }

    public SearchResult find( final NodeVersionDiffQuery query, final SearchSource source )
    {
        final Set<String> nodeIds = new HashSet<>();

        nodeIds.addAll( fetchNodeIds( query, source, true ) );
        nodeIds.addAll( fetchNodeIds( query, source, false ) );

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

        query.setSearchMode( SearchMode.COUNT );

        SearchResult result = searchDao.search( searchRequest );

        query.setSearchMode( SearchMode.SEARCH );
        query.setSize( 0 );

        final Set<String> nodeIds = new HashSet<>();

        final long branchesSize = result.getTotalHits();

        final int partitionsCount = (int) Math.ceil( branchesSize / 4000f );

        boolean done = false;

        for ( int currPartition = 0; currPartition < partitionsCount; currPartition++ )
        {
            query.setAggregations( AggregationQueries.create().
                add( TermsAggregationQuery.create( "versions" ).
                    size( 4000 ).
                    orderType( TermsAggregationQuery.Type.DOC_COUNT ).
                    orderDirection( TermsAggregationQuery.Direction.ASC ).
                    numOfPartitions( partitionsCount ).
                    partition( currPartition ).
                    fieldName( BranchIndexPath.VERSION_ID.toString() ).
                    addSubQuery( TermsAggregationQuery.create( "nodeIds" ).
                        fieldName( BranchIndexPath.NODE_ID.toString() ).
                        size( 1 ).
                        build() ).
                    build() ).
                build() );

            result = searchDao.search( searchRequest );

            BucketAggregation versionAgg = (BucketAggregation) result.getAggregations().get( "versions" );

            if ( versionAgg.getBuckets().getSize() == 0 )
            {
                break;
            }

            Stream<Bucket> versionStream = versionAgg.getBuckets().stream();

            Set<String> currNodeIds = ( versionStream.
                filter( bucket -> bucket.getDocCount() == 1 ).
                map( bucket -> Objects.requireNonNull(
                    ( (BucketAggregation) bucket.getSubAggregations().get( "nodeIds" ) ).getBuckets().first() ).getKey() ).
                collect( Collectors.toSet() ) );

            nodeIds.addAll( currNodeIds );

        }

        return nodeIds;
    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
