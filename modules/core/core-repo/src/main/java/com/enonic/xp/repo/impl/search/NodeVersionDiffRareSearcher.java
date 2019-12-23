package com.enonic.xp.repo.impl.search;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeVersionsQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.RareTermsAggregationQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

@Component
public class NodeVersionDiffRareSearcher
{

    private SearchDao searchDao;

    public NodeVersionDiffRareSearcher( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }

    public SearchResult find( final NodeVersionDiffQuery query, final SearchSource source )
    {
        final Set<String> versionIds = new HashSet<>();

        versionIds.addAll( fetchVersionIds( query, source, true ) );
        versionIds.addAll( fetchVersionIds( query, source, false ) );

        if ( versionIds.size() > 0 )
        {
            if ( query.getVersionsSize() == 0 )
            {
                return SearchResult.create().hits( SearchHits.create().build() ).totalHits( versionIds.size() ).build();
            }
            else
            {
                return searchDao.search( SearchRequest.create().
                    searchSource( SingleRepoStorageSource.create( ContextAccessor.current().getRepositoryId(),
                                                                  SingleRepoStorageSource.Type.VERSION ) ).
                    returnFields( ReturnFields.from( VersionIndexPath.NODE_ID ) ).
                    query( NodeVersionsQuery.create().
                        addIds( versionIds ).
                        size( versionIds.size() ).
                        batchSize( query.getBatchSize() ).
                        build() ).
                    build() );
            }
        }
        else
        {
            return SearchResult.create().hits( SearchHits.create().build() ).build();
        }
    }

    private Set<String> fetchVersionIds( final NodeVersionDiffQuery query, final SearchSource source, final boolean deleted )
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

        final Set<String> versionIds = new HashSet<>();

        final long branchesSize = result.getTotalHits();

        final int partitionsCount = (int) Math.ceil( branchesSize / 4000f );

        for ( int currPartition = 0; currPartition < partitionsCount; currPartition++ )
        {
            query.setAggregations( AggregationQueries.create().
                add( RareTermsAggregationQuery.create( "versions" ).
                    maxDocCount( 1 ).
                    fieldName( BranchIndexPath.VERSION_ID.toString() ).
                    numOfPartitions( partitionsCount ).
                    partition( currPartition ).
                    build() ).
                build() );

            result = searchDao.search( searchRequest );

            versionIds.addAll( ( (BucketAggregation) result.getAggregations().get( "versions" ) ).
                getBuckets().stream().
                map( Bucket::getKey ).
                collect( Collectors.toSet() ) );
        }

        return versionIds;
    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
