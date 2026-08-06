package com.enonic.xp.repo.impl.search.dsl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;

/**
 * Renders the filter vocabulary, which has no form in the public query DSL and therefore
 * lives in the wire schema's superset. Field names are emitted post-{@code IndexPath} and
 * unpostfixed, as everywhere else; {@code ids} exists only as a filter, never as a query.
 */
final class FilterDslRenderer
{
    private FilterDslRenderer()
    {
    }

    static Map<String, Object> render( final Filter filter )
    {
        if ( filter instanceof ExistsFilter )
        {
            return renderExists( (ExistsFilter) filter );
        }
        if ( filter instanceof ValueFilter )
        {
            return renderValues( (ValueFilter) filter );
        }
        if ( filter instanceof RangeFilter )
        {
            return renderRange( (RangeFilter) filter );
        }
        if ( filter instanceof IdFilter )
        {
            return renderIds( (IdFilter) filter );
        }
        if ( filter instanceof BooleanFilter )
        {
            return renderBoolean( (BooleanFilter) filter );
        }
        throw new DslRenderException( String.format( "Filter of type %s is not supported", filter.getClass() ) );
    }

    private static Map<String, Object> renderExists( final ExistsFilter filter )
    {
        final Map<String, Object> exists = new LinkedHashMap<>();
        exists.put( "field", fieldName( filter.getFieldName() ) );
        return wrap( "exists", exists );
    }

    private static Map<String, Object> renderValues( final ValueFilter filter )
    {
        final List<Object> values = new ArrayList<>();
        boolean dated = false;
        for ( final Value value : filter.getValues() )
        {
            dated |= value.isDateType();
            values.add( scalar( value ) );
        }

        final Map<String, Object> expression = new LinkedHashMap<>();
        expression.put( "field", fieldName( filter.getFieldName() ) );
        if ( dated )
        {
            expression.put( "type", "dateTime" );
        }
        expression.put( "values", values );
        return wrap( "values", expression );
    }

    private static Map<String, Object> renderIds( final IdFilter filter )
    {
        final Map<String, Object> expression = new LinkedHashMap<>();
        expression.put( "field", fieldName( filter.getFieldName() ) );
        expression.put( "values", new ArrayList<Object>( filter.getValues() ) );
        return wrap( "ids", expression );
    }

    private static Map<String, Object> renderRange( final RangeFilter filter )
    {
        final Value from = filter.getFrom();
        final Value to = filter.getTo();

        final Map<String, Object> range = new LinkedHashMap<>();
        range.put( "field", fieldName( filter.getFieldName() ) );
        if ( ( from != null && from.isDateType() ) || ( to != null && to.isDateType() ) )
        {
            range.put( "type", "dateTime" );
        }
        if ( from != null )
        {
            range.put( filter.isIncludeLower() ? "gte" : "gt", scalar( from ) );
        }
        if ( to != null )
        {
            range.put( filter.isIncludeUpper() ? "lte" : "lt", scalar( to ) );
        }
        return wrap( "range", range );
    }

    private static Map<String, Object> renderBoolean( final BooleanFilter filter )
    {
        final Map<String, Object> expression = new LinkedHashMap<>();
        addClause( expression, "must", filter.getMust() );
        addClause( expression, "mustNot", filter.getMustNot() );
        addClause( expression, "should", filter.getShould() );
        return wrap( "boolean", expression );
    }

    private static void addClause( final Map<String, Object> expression, final String name, final Collection<Filter> filters )
    {
        if ( filters.isEmpty() )
        {
            return;
        }
        final List<Object> rendered = new ArrayList<>( filters.size() );
        for ( final Filter filter : filters )
        {
            rendered.add( render( filter ) );
        }
        expression.put( name, rendered );
    }

    private static Object scalar( final Value value )
    {
        if ( value.isDateType() )
        {
            return value.asInstant().toString();
        }
        if ( value.isNumericType() )
        {
            return value.asDouble();
        }
        if ( value.isGeoPoint() )
        {
            throw new DslRenderException( "geoPoint values are not supported in filters" );
        }
        return value.asString();
    }

    private static String fieldName( final String fieldName )
    {
        return IndexPath.from( fieldName ).getPath();
    }

    private static Map<String, Object> wrap( final String name, final Map<String, Object> expression )
    {
        final Map<String, Object> wrapper = new LinkedHashMap<>();
        wrapper.put( name, expression );
        return wrapper;
    }
}
