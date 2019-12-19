package com.enonic.xp.repo.impl.search;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
public class NodeVersionDiffInMemorySearcher
{
    private SearchDao searchDao;

    public NodeVersionDiffInMemorySearcher( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }

    public SearchResult find( final NodeVersionDiffQuery query, final SearchSource source )
    {
        query.setSize( -1 );

        query.setDeleted( false );

        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            returnFields( ReturnFields.from( BranchIndexPath.VERSION_ID, BranchIndexPath.NODE_ID ) ).
            query( query ).
            build();

        SearchResult results = searchDao.search( searchRequest );

        final Set<String> nodeIds = new HashSet<String>();

        final HashSet<String> versions = new HashSet<String>();

        results.
            getHits().
            forEach( hit -> {
                final String versionId = (String) hit.getField( "versionid" ).getSingleValue();
                if ( versions.contains( versionId ) )
                {
                    nodeIds.remove( (String) hit.getField( "nodeid" ).getSingleValue() );
                }
                else
                {
                    versions.add( versionId );
                    nodeIds.add( (String) hit.getField( "nodeid" ).getSingleValue() );
                }
            } );

        query.setDeleted( true );
        results = searchDao.search( searchRequest );

        final HashSet<String> deletedVersions = new HashSet<String>();

        results.
            getHits().
            forEach( hit -> {
                final String versionId = (String) hit.getField( "versionid" ).getSingleValue();
                if ( deletedVersions.contains( versionId ) )
                {
                    nodeIds.remove( (String) hit.getField( "nodeid" ).getSingleValue() );
                }
                else
                {
                    deletedVersions.add( versionId );
                    nodeIds.add( (String) hit.getField( "nodeid" ).getSingleValue() );
                }
            } );

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
