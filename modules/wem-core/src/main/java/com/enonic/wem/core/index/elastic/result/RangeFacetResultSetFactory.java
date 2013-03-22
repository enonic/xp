package com.enonic.wem.core.index.elastic.result;

import java.util.List;

import org.elasticsearch.search.facet.range.RangeFacet;

import com.enonic.wem.api.query.RangeFacetResultEntry;
import com.enonic.wem.api.query.RangeFacetResultSet;

public class RangeFacetResultSetFactory
    extends AbstractFacetResultSetFactory
{

    protected static RangeFacetResultSet create( final String facetName, final RangeFacet facet )
    {
        RangeFacetResultSet rangeFacetResultSet = new RangeFacetResultSet();

        rangeFacetResultSet.setName( facetName );

        final List<RangeFacet.Entry> entries = facet.getEntries();

        for ( RangeFacet.Entry entry : entries )
        {
            rangeFacetResultSet.addResult( createRangeFacetResultEntry( entry ) );
        }

        return rangeFacetResultSet;
    }

    private static RangeFacetResultEntry createRangeFacetResultEntry( RangeFacet.Entry entry )
    {
        RangeFacetResultEntry rangeFacetResultEntry = new RangeFacetResultEntry();

        rangeFacetResultEntry.setFrom( createRangeLimitValue( entry.getFromAsString() ) );
        rangeFacetResultEntry.setTo( createRangeLimitValue( entry.getToAsString() ) );
        rangeFacetResultEntry.setCount( entry.getCount() );

        boolean isNumericFacetResult = isNumericFacetResult( entry );

        if ( isNumericFacetResult )
        {
            rangeFacetResultEntry.setMin( entry.getMin() );
            rangeFacetResultEntry.setMean( entry.getMean() );
            rangeFacetResultEntry.setMax( entry.getMax() );
            rangeFacetResultEntry.setTotal( entry.getTotal() );
        }

        return rangeFacetResultEntry;
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

    private static boolean isNumericFacetResult( final RangeFacet.Entry entry )
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


