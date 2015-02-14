package com.enonic.xp.portal.impl.jslib.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.aggregation.BucketAggregationQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.DateRange;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.xp.query.aggregation.HistogramAggregationQuery;
import com.enonic.xp.query.aggregation.NumericRange;
import com.enonic.xp.query.aggregation.NumericRangeAggregationQuery;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;

import static com.enonic.xp.query.aggregation.HistogramAggregationQuery.Order.COUNT_ASC;
import static com.enonic.xp.query.aggregation.HistogramAggregationQuery.Order.COUNT_DESC;
import static com.enonic.xp.query.aggregation.HistogramAggregationQuery.Order.KEY_ASC;
import static com.enonic.xp.query.aggregation.HistogramAggregationQuery.Order.KEY_DESC;
import static com.enonic.xp.query.aggregation.TermsAggregationQuery.Direction.ASC;
import static com.enonic.xp.query.aggregation.TermsAggregationQuery.Direction.DESC;
import static java.util.Collections.emptyList;

@SuppressWarnings("unchecked")
final class QueryAggregationParams
{
    QueryAggregationParams()
    {
    }

    public Set<AggregationQuery> getAggregations( final Map<String, Object> aggregationsMap )
    {
        if ( aggregationsMap == null )
        {
            return Collections.emptySet();
        }

        final Set<AggregationQuery> aggregations = new HashSet<>();
        for ( String name : aggregationsMap.keySet() )
        {
            final Map<String, Object> aggregationQueryMap = (Map<String, Object>) aggregationsMap.get( name );
            final AggregationQuery aggregationQuery = aggregationQueryFromParams( name, aggregationQueryMap );
            if ( aggregationQuery != null )
            {
                aggregations.add( aggregationQuery );
            }
        }

        return aggregations;
    }

    private AggregationQuery aggregationQueryFromParams( final String name, final Map<String, Object> aggregationQueryMap )
    {
        if ( aggregationQueryMap.containsKey( "terms" ) )
        {
            final Map<String, Object> termsParamsMap = (Map<String, Object>) aggregationQueryMap.get( "terms" );
            final TermsAggregationQuery.Builder termsAggregationQuery = termsAggregationFromParams( name, termsParamsMap );
            addSubAggregations( termsAggregationQuery, aggregationQueryMap );
            return termsAggregationQuery.build();

        }
        else if ( aggregationQueryMap.containsKey( "histogram" ) )
        {
            final Map<String, Object> histogramParamsMap = (Map<String, Object>) aggregationQueryMap.get( "histogram" );
            final HistogramAggregationQuery.Builder termsAggregationQuery = histogramAggregationFromParams( name, histogramParamsMap );
            addSubAggregations( termsAggregationQuery, aggregationQueryMap );
            return termsAggregationQuery.build();
        }
        else if ( aggregationQueryMap.containsKey( "date_histogram" ) )
        {
            final Map<String, Object> dateHistogramParamsMap = (Map<String, Object>) aggregationQueryMap.get( "date_histogram" );
            final DateHistogramAggregationQuery.Builder dateAggregationQuery =
                dateHistogramAggregationFromParams( name, dateHistogramParamsMap );
            addSubAggregations( dateAggregationQuery, aggregationQueryMap );
            return dateAggregationQuery.build();
        }
        else if ( aggregationQueryMap.containsKey( "range" ) )
        {
            final Map<String, Object> numericRangeParamsMap = (Map<String, Object>) aggregationQueryMap.get( "range" );
            final NumericRangeAggregationQuery.Builder rangeAggregationQuery =
                numericRangeAggregationFromParams( name, numericRangeParamsMap );
            addSubAggregations( rangeAggregationQuery, aggregationQueryMap );
            return rangeAggregationQuery.build();
        }
        else if ( aggregationQueryMap.containsKey( "date_range" ) )
        {
            final Map<String, Object> dateRangeParamsMap = (Map<String, Object>) aggregationQueryMap.get( "date_range" );
            final DateRangeAggregationQuery.Builder dateRangeAggregationQuery = dateRangeAggregationFromParams( name, dateRangeParamsMap );
            addSubAggregations( dateRangeAggregationQuery, aggregationQueryMap );
            return dateRangeAggregationQuery.build();
        }
        else if ( aggregationQueryMap.containsKey( "stats" ) )
        {
            final Map<String, Object> dateRangeParamsMap = (Map<String, Object>) aggregationQueryMap.get( "stats" );
            final StatsAggregationQuery.Builder statsRangeAggregationQuery = statsAggregationFromParams( name, dateRangeParamsMap );
            return statsRangeAggregationQuery.build();
        }

        return null;
    }

    private void addSubAggregations( final BucketAggregationQuery.Builder aggregationQuery, final Map<String, Object> aggregationQueryMap )
    {
        if ( aggregationQueryMap.containsKey( "aggregations" ) )
        {
            final Map<String, Object> aggregationsMap = (Map<String, Object>) aggregationQueryMap.get( "aggregations" );
            final Set<AggregationQuery> aggregation = getAggregations( aggregationsMap );
            aggregationQuery.addSubQueries( aggregation );
        }
    }

    private TermsAggregationQuery.Builder termsAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        final String orderExpr = ( (String) paramsMap.getOrDefault( "order", "" ) ).trim();
        final int size = (int) paramsMap.getOrDefault( "size", 10 );

        final String orderTypeStr = StringUtils.substringBefore( orderExpr, " " );
        final String orderDir = StringUtils.substringAfter( orderExpr, " " );
        final TermsAggregationQuery.Direction orderDirection = "desc".equalsIgnoreCase( orderDir ) ? DESC : ASC;
        final TermsAggregationQuery.Type orderType;
        if ( "_term".equals( orderTypeStr ) )
        {
            orderType = TermsAggregationQuery.Type.TERM;
        }
        else if ( "_count".equals( orderTypeStr ) )
        {
            orderType = TermsAggregationQuery.Type.DOC_COUNT;
        }
        else
        {
            orderType = null;
        }

        return TermsAggregationQuery.create( name ).
            fieldName( fieldName ).
            size( size ).
            orderType( orderType ).
            orderDirection( orderDirection );
    }

    private HistogramAggregationQuery.Builder histogramAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        final String orderExpr = ( (String) paramsMap.getOrDefault( "order", "" ) ).trim();
        final Long interval = getLong( paramsMap, "interval" );
        final Long extendedBoundMin = getLong( paramsMap, "extendedBoundMin" );
        final Long extendedBoundMax = getLong( paramsMap, "extendedBoundMax" );
        final Long mindDocCount = getLong( paramsMap, "minDocCount" );

        HistogramAggregationQuery.Order histogramOrder = KEY_ASC;
        if ( !orderExpr.isEmpty() )
        {
            final String orderTypeStr = StringUtils.substringBefore( orderExpr, " " );
            final String orderDir = StringUtils.substringAfter( orderExpr, " " );

            if ( "_key".equals( orderTypeStr ) )
            {
                histogramOrder = "desc".equalsIgnoreCase( orderDir ) ? KEY_DESC : KEY_ASC;
            }
            else if ( "_count".equals( orderTypeStr ) )
            {
                histogramOrder = "desc".equalsIgnoreCase( orderDir ) ? COUNT_DESC : COUNT_ASC;
            }
        }

        return HistogramAggregationQuery.create( name ).
            fieldName( fieldName ).
            interval( interval ).
            extendedBoundMin( extendedBoundMin ).
            extendedBoundMax( extendedBoundMax ).
            minDocCount( mindDocCount ).
            order( histogramOrder );
    }

    private DateHistogramAggregationQuery.Builder dateHistogramAggregationFromParams( final String name,
                                                                                      final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        final String interval = (String) paramsMap.getOrDefault( "interval", "" );
        final Long mindDocCount = getLong( paramsMap, "minDocCount" );

        return DateHistogramAggregationQuery.create( name ).
            fieldName( fieldName ).
            interval( interval ).
            minDocCount( mindDocCount );
    }

    private NumericRangeAggregationQuery.Builder numericRangeAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        final List<Map<String, Object>> rangeListParams = (List<Map<String, Object>>) paramsMap.getOrDefault( "ranges", emptyList() );
        final List<NumericRange> ranges = new ArrayList<>();
        for ( Map<String, Object> rangeParams : rangeListParams )
        {
            final Double from = getDouble( rangeParams, "from" );
            final Double to = getDouble( rangeParams, "to" );
            final NumericRange range = NumericRange.create().from( from ).to( to ).build();
            ranges.add( range );
        }

        return NumericRangeAggregationQuery.create( name ).
            fieldName( fieldName ).
            setRanges( ranges );
    }

    private DateRangeAggregationQuery.Builder dateRangeAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        final List<Map<String, Object>> rangeListParams = (List<Map<String, Object>>) paramsMap.getOrDefault( "ranges", emptyList() );
        final List<DateRange> ranges = new ArrayList<>();
        for ( Map<String, Object> rangeParams : rangeListParams )
        {
            final String from = (String) rangeParams.getOrDefault( "from", null );
            final String to = (String) rangeParams.getOrDefault( "to", null );
            final DateRange range = DateRange.create().from( from ).to( to ).build();
            ranges.add( range );
        }

        return DateRangeAggregationQuery.create( name ).
            fieldName( fieldName ).
            setRanges( ranges );
    }

    private StatsAggregationQuery.Builder statsAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        return StatsAggregationQuery.create( name ).
            fieldName( fieldName );
    }

    private Long getLong( final Map<String, Object> map, final String key )
    {
        final Object value = map.get( key );
        return value != null && value instanceof Number ? ( (Number) value ).longValue() : null;
    }

    private Double getDouble( final Map<String, Object> map, final String key )
    {
        final Object value = map.get( key );
        return value != null && value instanceof Number ? ( (Number) value ).doubleValue() : null;
    }
}
