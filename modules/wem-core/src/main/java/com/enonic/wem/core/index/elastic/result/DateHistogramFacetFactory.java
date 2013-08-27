package com.enonic.wem.core.index.elastic.result;


import java.util.List;

import com.enonic.wem.api.facet.DateHistogramFacet;
import com.enonic.wem.api.facet.DateHistogramFacetEntry;

public class DateHistogramFacetFactory
{

    protected static DateHistogramFacet create( final String facetName,
                                                final org.elasticsearch.search.facet.datehistogram.DateHistogramFacet facet )
    {
        DateHistogramFacet dateHistogramFacet = new DateHistogramFacet();

        dateHistogramFacet.setName( facetName );

        final List<? extends org.elasticsearch.search.facet.datehistogram.DateHistogramFacet.Entry> entries = facet.getEntries();

        for ( org.elasticsearch.search.facet.datehistogram.DateHistogramFacet.Entry entry : entries )
        {
            DateHistogramFacetEntry result = new DateHistogramFacetEntry();
            result.setCount( entry.getCount() );
            result.setTime( entry.getTime() );
            result.setMax( getValueIfNumber( entry.getMax() ) );
            result.setMean( getValueIfNumber( entry.getMean() ) );
            result.setMin( getValueIfNumber( entry.getMin() ) );
            result.setTotal( getValueIfNumber( entry.getTotal() ) );

            dateHistogramFacet.addResult( result );
        }

        return dateHistogramFacet;
    }

    protected static Double getValueIfNumber( final double entry )
    {
        return Double.isNaN( entry ) ? null : entry;
    }

}
