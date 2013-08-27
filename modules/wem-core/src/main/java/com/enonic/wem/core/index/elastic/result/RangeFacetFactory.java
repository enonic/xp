package com.enonic.wem.core.index.elastic.result;

import java.util.List;

import com.enonic.wem.api.facet.RangeFacet;
import com.enonic.wem.api.facet.RangeFacetEntry;

public class RangeFacetFactory
{

    protected static RangeFacet create( final String facetName, final org.elasticsearch.search.facet.range.RangeFacet facet )
    {
        RangeFacet rangeFacet = new RangeFacet();

        rangeFacet.setName( facetName );

        final List<org.elasticsearch.search.facet.range.RangeFacet.Entry> entries = facet.getEntries();

        for ( org.elasticsearch.search.facet.range.RangeFacet.Entry entry : entries )
        {
            rangeFacet.addResult( createRangeFacetEntry( entry ) );
        }

        return rangeFacet;
    }

    private static RangeFacetEntry createRangeFacetEntry( org.elasticsearch.search.facet.range.RangeFacet.Entry entry )
    {
        RangeFacetEntry rangeFacetEntry = new RangeFacetEntry();

        rangeFacetEntry.setFrom( createRangeLimitValue( entry.getFromAsString() ) );
        rangeFacetEntry.setTo( createRangeLimitValue( entry.getToAsString() ) );
        rangeFacetEntry.setCount( entry.getCount() );

        if ( isNumericFacetEntry( entry ) )
        {
            rangeFacetEntry.setMin( entry.getMin() );
            rangeFacetEntry.setMean( entry.getMean() );
            rangeFacetEntry.setMax( entry.getMax() );
            rangeFacetEntry.setTotal( entry.getTotal() );
        }

        return rangeFacetEntry;
    }

    private static String createRangeLimitValue( final String value )
    {

        final Double fromAsNumber = getAsNumber( value );

        if ( fromAsNumber == null || !fromAsNumber.isInfinite() )
        {
            return value;
        }

        return null;
    }

    private static boolean isNumericFacetEntry( final org.elasticsearch.search.facet.range.RangeFacet.Entry entry )
    {
        if ( isNumericAndNotInifinte( entry.getFromAsString() ) || isNumericAndNotInifinte( entry.getToAsString() ) )
        {
            return true;
        }

        return false;
    }

    private static boolean isNumericAndNotInifinte( final String value )
    {
        final Double asNumber = getAsNumber( value );
        return asNumber != null && !asNumber.isInfinite();
    }

    public static Double getAsNumber( String value )
    {
        try
        {
            return new Double( value );
        }
        catch ( NumberFormatException e )
        {
            return null;
        }


    }


}


