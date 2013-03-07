package com.enonic.wem.core.index.elastic.result;


import java.util.List;

import org.elasticsearch.search.facet.datehistogram.DateHistogramFacet;

import com.enonic.wem.api.query.DateHistogramFacetResultEntry;
import com.enonic.wem.api.query.DateHistogramFacetResultSet;

public class DateHistogramFacetResultSetFactory
    extends AbstractFacetResultSetFactory
{

    protected static DateHistogramFacetResultSet create( final String facetName, final DateHistogramFacet facet )
    {
        DateHistogramFacetResultSet dateHistogramFacetResultSet = new DateHistogramFacetResultSet();

        dateHistogramFacetResultSet.setName( facetName );

        final List<? extends DateHistogramFacet.Entry> entries = facet.getEntries();

        for ( DateHistogramFacet.Entry entry : entries )
        {
            DateHistogramFacetResultEntry result = new DateHistogramFacetResultEntry();
            result.setCount( entry.getCount() );
            result.setTime( entry.getTime() );
            result.setMax( getValueIfNumber( entry.getMax() ) );
            result.setMean( getValueIfNumber( entry.getMean() ) );
            result.setMin( getValueIfNumber( entry.getMin() ) );
            result.setTotal( getValueIfNumber( entry.getTotal() ) );

            dateHistogramFacetResultSet.addResult( result );
        }

        return dateHistogramFacetResultSet;
    }

}
