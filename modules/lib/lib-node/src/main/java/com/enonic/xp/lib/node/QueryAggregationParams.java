package com.enonic.xp.lib.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.aggregation.BucketAggregationQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.DateRange;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.xp.query.aggregation.DistanceRange;
import com.enonic.xp.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.xp.query.aggregation.HistogramAggregationQuery;
import com.enonic.xp.query.aggregation.NumericRange;
import com.enonic.xp.query.aggregation.NumericRangeAggregationQuery;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MinAggregationQuery;
import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.ValueCountAggregationQuery;
import com.enonic.xp.util.GeoPoint;

import static com.enonic.xp.query.aggregation.HistogramAggregationQuery.Order.COUNT_ASC;
import static com.enonic.xp.query.aggregation.HistogramAggregationQuery.Order.COUNT_DESC;
import static com.enonic.xp.query.aggregation.HistogramAggregationQuery.Order.KEY_ASC;
import static com.enonic.xp.query.aggregation.HistogramAggregationQuery.Order.KEY_DESC;
import static com.enonic.xp.query.aggregation.TermsAggregationQuery.Direction.ASC;
import static com.enonic.xp.query.aggregation.TermsAggregationQuery.Direction.DESC;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@SuppressWarnings("unchecked")
final class QueryAggregationParams
{
    QueryAggregationParams()
    {
    }

    public AggregationQueries getAggregations( final Map<String, Object> aggregationsMap )
    {
        if ( aggregationsMap == null )
        {
            return AggregationQueries.empty();
        }

        final AggregationQueries.Builder aggregations = AggregationQueries.create();
        aggregationsMap.forEach( ( name, aggregationQueryMap ) -> {
            final AggregationQuery aggregationQuery = aggregationQueryFromParams( name, (Map<String, Object>) aggregationQueryMap );
            if ( aggregationQuery != null )
            {
                aggregations.add( aggregationQuery );
            }
        } );

        return aggregations.build();
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
        else if ( aggregationQueryMap.containsKey( "dateHistogram" ) )
        {
            final Map<String, Object> dateHistogramParamsMap = (Map<String, Object>) aggregationQueryMap.get( "dateHistogram" );
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
        else if ( aggregationQueryMap.containsKey( "dateRange" ) )
        {
            final Map<String, Object> dateRangeParamsMap = (Map<String, Object>) aggregationQueryMap.get( "dateRange" );
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
        else if ( aggregationQueryMap.containsKey( "geoDistance" ) )
        {
            final Map<String, Object> geoDistanceParamsMap = (Map<String, Object>) aggregationQueryMap.get( "geoDistance" );
            final GeoDistanceAggregationQuery.Builder geoDistanceAggregationQuery =
                geoDistanceAggregationFromParams( name, geoDistanceParamsMap );
            return geoDistanceAggregationQuery.build();
        }
        else if ( aggregationQueryMap.containsKey( "min" ) )
        {
            final Map<String, Object> minParamsMap = (Map<String, Object>) aggregationQueryMap.get( "min" );
            final MinAggregationQuery.Builder minAggregationQuery = minAggregationFromParams( name, minParamsMap );
            return minAggregationQuery.build();
        }
        else if ( aggregationQueryMap.containsKey( "max" ) )
        {
            final Map<String, Object> maxParamsMap = (Map<String, Object>) aggregationQueryMap.get( "max" );
            final MaxAggregationQuery.Builder maxAggregationQuery = maxAggregationFromParams( name, maxParamsMap );
            return maxAggregationQuery.build();
        }
        else if ( aggregationQueryMap.containsKey( "count" ) )
        {
            final Map<String, Object> valueCountParamsMap = (Map<String, Object>) aggregationQueryMap.get( "count" );
            final ValueCountAggregationQuery.Builder valueCountAggregationQuery =
                valueCountAggregationFromParams( name, valueCountParamsMap );
            return valueCountAggregationQuery.build();
        }

        return null;
    }

    private void addSubAggregations( final BucketAggregationQuery.Builder aggregationQuery, final Map<String, Object> aggregationQueryMap )
    {
        if ( aggregationQueryMap.containsKey( "aggregations" ) )
        {
            final Map<String, Object> aggregationsMap = (Map<String, Object>) aggregationQueryMap.get( "aggregations" );
            final AggregationQueries aggregation = getAggregations( aggregationsMap );
            aggregationQuery.addSubQueries( aggregation );
        }
    }

    private TermsAggregationQuery.Builder termsAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        final String orderExpr = ( (String) paramsMap.getOrDefault( "order", "" ) ).trim();
        final Integer size = Optional.ofNullable( (Number) paramsMap.get( "size" ) ).
            map( Number::intValue ).
            orElse( 10 );
        final Long minDocCount = getLong( paramsMap, "minDocCount" );

        final int index = orderExpr.indexOf( " " );
        final String orderTypeStr = index == -1 ? orderExpr : orderExpr.substring( 0, index );
        final String orderDir = index == -1 ? "" : orderExpr.substring( index + 1 );

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

        final TermsAggregationQuery.Builder builder = TermsAggregationQuery.create( name ).
            fieldName( fieldName ).
            size( size ).
            orderType( orderType ).
            orderDirection( orderDirection );

        if ( minDocCount != null )
        {
            builder.minDoccount( minDocCount );
        }

        return builder;
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
            final int index = orderExpr.indexOf( " " );
            final String orderTypeStr = index == -1 ? orderExpr : orderExpr.substring( 0, index );
            final String orderDir = index == -1 ? "" : orderExpr.substring( index + 1 );

            if ( "_key".equals( orderTypeStr ) )
            {
                histogramOrder = "desc".equalsIgnoreCase( orderDir ) ? KEY_DESC : KEY_ASC;
            }
            else if ( "_count".equals( orderTypeStr ) )
            {
                histogramOrder = "desc".equalsIgnoreCase( orderDir ) ? COUNT_DESC : COUNT_ASC;
            }
        }

        final HistogramAggregationQuery.Builder histogramBuilder = HistogramAggregationQuery.create( name ).
            fieldName( fieldName ).
            interval( interval ).
            minDocCount( mindDocCount ).
            order( histogramOrder );
        if ( extendedBoundMax != null )
        {
            histogramBuilder.extendedBoundMax( extendedBoundMax );
        }
        if ( extendedBoundMin != null )
        {
            histogramBuilder.extendedBoundMin( extendedBoundMin );
        }

        return histogramBuilder;
    }

    private DateHistogramAggregationQuery.Builder dateHistogramAggregationFromParams( final String name,
                                                                                      final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        final String interval = (String) paramsMap.getOrDefault( "interval", "" );
        final Long mindDocCount = getLong( paramsMap, "minDocCount" );
        final String format = (String) paramsMap.get( "format" );

        return DateHistogramAggregationQuery.create( name ).
            fieldName( fieldName ).
            interval( interval ).
            minDocCount( mindDocCount ).
            format( format );
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
        final String format = (String) paramsMap.get( "format" );
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
            setRanges( ranges ).
            format( format );
    }

    private GeoDistanceAggregationQuery.Builder geoDistanceAggregationFromParams( final String name, final Map<String, Object> params )
    {
        final String fieldName = (String) params.get( "field" );
        final String unit = (String) params.get( "unit" );
        final Map<String, Object> originCoordinates = (Map<String, Object>) params.getOrDefault( "origin", emptyMap() );
        final double lat = Double.parseDouble( (String) originCoordinates.get( "lat" ) );
        final double lon = Double.parseDouble( (String) originCoordinates.get( "lon" ) );
        final GeoPoint origin = new GeoPoint( lat, lon );
        final List<Map<String, Object>> rangeListParams = (List<Map<String, Object>>) params.getOrDefault( "ranges", emptyList() );
        final List<DistanceRange> ranges = new ArrayList<>();
        for ( Map<String, Object> rangeParams : rangeListParams )
        {
            final Double from = getDouble( rangeParams, "from" );
            final Double to = getDouble( rangeParams, "to" );

            final DistanceRange range = DistanceRange.create().from( from ).to( to ).build();
            ranges.add( range );
        }

        return GeoDistanceAggregationQuery.create( name ).
            fieldName( fieldName ).
            origin( origin ).
            unit( unit ).
            setRanges( ranges );
    }

    private StatsAggregationQuery.Builder statsAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        return StatsAggregationQuery.create( name ).
            fieldName( fieldName );
    }

    private MinAggregationQuery.Builder minAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        return MinAggregationQuery.create( name ).
            fieldName( fieldName );
    }

    private MaxAggregationQuery.Builder maxAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        return MaxAggregationQuery.create( name ).
            fieldName( fieldName );
    }

    private ValueCountAggregationQuery.Builder valueCountAggregationFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String fieldName = (String) paramsMap.get( "field" );
        return ValueCountAggregationQuery.create( name ).
            fieldName( fieldName );
    }

    private Long getLong( final Map<String, Object> map, final String key )
    {
        final Object value = map.get( key );
        return value instanceof Number ? ( (Number) value ).longValue() : null;
    }

    private Double getDouble( final Map<String, Object> map, final String key )
    {
        final Object value = map.get( key );
        return value instanceof Number ? ( (Number) value ).doubleValue() : null;
    }
}

