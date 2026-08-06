package com.enonic.xp.storage.nodb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.nodb.proto.v1.SearchSourceRef;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.storage.spi.MultiRepoSearchSource;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.storage.spi.SearchDsl;
import com.enonic.xp.storage.spi.SearchRequest;
import com.enonic.xp.storage.spi.SearchSource;
import com.enonic.xp.storage.spi.SingleRepoSearchSource;

/**
 * Serializes a {@link SearchRequest} into the wire envelope. This class is the whole of the
 * client's contribution to the query path, and it deliberately contains no query-language
 * knowledge: the canonical DSL arrives already rendered on {@link SearchRequest#getSearchDsl()}
 * and is written out verbatim as JSON. If it is absent, this fails fast rather than trying to
 * reconstruct it.
 * <p>
 * <b>Every collection here is ordered.</b> XP's aggregation/suggestion/source sets are
 * {@code HashSet}s, whose iteration order varies across JVM runs; a wire format built on them
 * would produce a different request on every boot, which defeats wire-level golden files and
 * any request caching downstream. Sources, principals and return fields are therefore sorted
 * into a total order rather than merely copied.
 */
final class SearchEnvelopeSerializer
{
    /** v1 of the search wire envelope. */
    static final int FORMAT_VERSION = 1;

    private static final ObjectMapper JSON = new ObjectMapper();

    private static final Comparator<SingleRepoSearchSource> SOURCE_ORDER =
        Comparator.comparing( ( SingleRepoSearchSource source ) -> source.getRepositoryId().toString() )
            .thenComparing( source -> source.getBranch().getValue() );

    private SearchEnvelopeSerializer()
    {
    }

    static com.enonic.nodb.proto.v1.SearchRequest serialize( final SearchRequest request )
    {
        final SearchDsl dsl = request.getSearchDsl();
        if ( dsl == null )
        {
            throw new UnsupportedOperationException(
                "Query of type " + ( request.getQuery() == null ? "null" : request.getQuery().getClass().getName() ) +
                    " carries no canonical query DSL; the nodb search backend does not translate query languages" );
        }

        final com.enonic.nodb.proto.v1.SearchRequest.Builder builder =
            com.enonic.nodb.proto.v1.SearchRequest.newBuilder().setFormatVersion( FORMAT_VERSION );

        for ( final SingleRepoSearchSource source : sources( request.getSearchSource() ) )
        {
            builder.addSources( SearchSourceRef.newBuilder()
                                    .setRepoId( source.getRepositoryId().toString() )
                                    .setBranch( source.getBranch().getValue() )
                                    .addAllPrincipals( principals( source.getAcl() ) )
                                    .build() );
        }

        builder.setQuery( toJson( dsl.getQuery() ) );
        for ( final Map<String, Object> filter : dsl.getQueryFilters() )
        {
            builder.addQueryFilters( toJson( filter ) );
        }
        for ( final Map<String, Object> filter : dsl.getPostFilters() )
        {
            builder.addPostFilters( toJson( filter ) );
        }
        for ( final Map<String, Object> sort : dsl.getSort() )
        {
            builder.addSort( toJson( sort ) );
        }

        // One opaque document each, not a repeated field: a suggest section is a map keyed by
        // suggester name and a highlight block is a single settings-plus-fields object. Left as the
        // proto default (empty string) when absent, which is how the server tells "not requested"
        // from "requested empty" without a presence flag.
        if ( !dsl.getSuggest().isEmpty() )
        {
            builder.setSuggest( toJson( dsl.getSuggest() ) );
        }
        if ( !dsl.getHighlight().isEmpty() )
        {
            builder.setHighlight( toJson( dsl.getHighlight() ) );
        }
        // Also one opaque document: an aggregation section is a map keyed by aggregation name, and
        // the response comes back keyed the same way. The renderer already sorted it.
        if ( !dsl.getAggregations().isEmpty() )
        {
            builder.setAggregations( toJson( dsl.getAggregations() ) );
        }

        return builder.setFrom( dsl.getFrom() )
            .setSize( dsl.getSize() )
            .setBatchSize( dsl.getBatchSize() )
            .setExplain( dsl.isExplain() )
            .setSearchOptimizer( dsl.getSearchOptimizer() )
            .addAllReturnFields( returnFields( request.getReturnFields() ) )
            .build();
    }

    private static List<SingleRepoSearchSource> sources( final SearchSource searchSource )
    {
        final List<SingleRepoSearchSource> sources = new ArrayList<>();

        if ( searchSource instanceof SingleRepoSearchSource )
        {
            sources.add( (SingleRepoSearchSource) searchSource );
        }
        else if ( searchSource instanceof MultiRepoSearchSource )
        {
            ( (MultiRepoSearchSource) searchSource ).forEach( sources::add );
        }
        else
        {
            throw new UnsupportedOperationException(
                "Search source of type " + ( searchSource == null ? "null" : searchSource.getClass().getName() ) +
                    " has no search-index form; branch, version and commit queries are answered from the system of record" );
        }

        sources.sort( SOURCE_ORDER );
        return sources;
    }

    /**
     * Empty principals stay fail-closed — {@code user:system:anonymous}, never match-all. The
     * admin role is NOT special-cased away as the ES path did: the indexer injects an admin
     * read key into every document, so the filter is always applied.
     */
    private static List<String> principals( final PrincipalKeys acl )
    {
        if ( acl.isEmpty() )
        {
            return List.of( PrincipalKey.ofAnonymous().toString() );
        }

        final TreeSet<String> ordered = new TreeSet<>();
        for ( final PrincipalKey key : acl )
        {
            ordered.add( key.toString() );
        }
        return new ArrayList<>( ordered );
    }

    private static List<String> returnFields( final ReturnFields returnFields )
    {
        if ( returnFields == null )
        {
            return List.of();
        }
        return new ArrayList<>( new TreeSet<>( List.of( returnFields.getReturnFieldNames() ) ) );
    }

    private static String toJson( final Map<String, Object> value )
    {
        try
        {
            return JSON.writeValueAsString( value );
        }
        catch ( JsonProcessingException e )
        {
            throw new NodbClientException( "Cannot serialize the canonical query DSL", e );
        }
    }
}
