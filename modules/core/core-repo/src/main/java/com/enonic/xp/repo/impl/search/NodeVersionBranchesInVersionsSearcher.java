package com.enonic.xp.repo.impl.search;

import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

@Component
public class NodeVersionBranchesInVersionsSearcher
{
    private SearchDao searchDao;

    public NodeVersionBranchesInVersionsSearcher( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }

    public SearchResult find( final NodeVersionDiffQuery query, final SearchSource source )
    {
        query.setSize( -1 );

        query.setDeleted( false );

        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            returnFields( ReturnFields.from( VersionIndexPath.NODE_ID ) ).
            query( query ).
            build();

        final SearchResult results = searchDao.search( searchRequest );

        final Set<String> nodeIds = results.
            getHits().stream().
            map( ( hit -> (String) hit.getField( "nodeid" ).getSingleValue() ) ).
            collect( Collectors.toSet() );

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

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
