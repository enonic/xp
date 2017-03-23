package com.enonic.xp.lib.content;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Value;
import com.enonic.xp.lib.common.ObjectToValueMapper;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;

public class FilterParamsFactory
{
    static Filters create( final Map<String, Object> value )
    {
        return createFilters( value );
    }

    private static Filters createFilters( final Map<String, Object> filterMap )
    {
        if ( filterMap == null )
        {
            return Filters.create().build();
        }

        final Filters.Builder filters = Filters.create();

        for ( final String key : filterMap.keySet() )
        {
            final Object filter = filterMap.get( key );

            if ( filter instanceof Collection )
            {
                for ( final Object filterValue : (Collection) filter )
                {
                    filters.add( doCreateFilter( key, filterValue ) );
                }
            }
            else
            {
                filters.add( doCreateFilter( key, filter ) );
            }
        }

        return filters.build();
    }

    private static Filter doCreateFilter( final String key, final Object filter )
    {
        switch ( key )
        {
            case "notExists":
            {
                return createNotExistsFilter( toMap( filter ) );
            }
            case "exists":
            {
                return createExistsFilter( toMap( filter ) );
            }
            case "boolean":
            {
                return createBoolFilter( toMap( filter ) );
            }
            case "hasValue":
            {
                return createValueFilter( toMap( filter ) );
            }
            case "ids":
            {
                return createIdsFilter( toMap( filter ) );
            }
            default:
                throw new IllegalArgumentException( "Unknown filter type [" + key + "]" );
        }
    }

    private static Filter createIdsFilter( final Map<String, Object> idsFilter )
    {
        requiredFields( idsFilter, "values" );

        return IdFilter.create().
            values( getStringValues( idsFilter.get( "values" ) ) ).
            build();
    }

    private static Filter createExistsFilter( final Map<String, Object> existsFilter )
    {
        requiredFields( existsFilter, "fieldName" );

        return ExistsFilter.create().
            fieldName( existsFilter.get( "fieldName" ).toString() ).
            build();
    }

    private static Filter createNotExistsFilter( final Map<String, Object> existsFilter )
    {
        requiredFields( existsFilter, "fieldName" );

        return BooleanFilter.create().
            mustNot( ExistsFilter.create().
                fieldName( existsFilter.get( "fieldName" ).toString() ).
                build() ).
            build();
    }

    private static Filter createValueFilter( final Map<String, Object> valueFilter )
    {
        requiredFields( valueFilter, "fieldName", "values" );

        final ValueFilter.Builder builder = ValueFilter.create().
            fieldName( valueFilter.get( "fieldName" ).toString() ).
            addAllValues( getValues( valueFilter.get( "values" ) ) );

        return builder.build();
    }

    private static Collection<Value> getValues( final Object values )
    {
        List<Value> valueList = Lists.newArrayList();

        if ( values instanceof Collection )
        {
            for ( final Object value : (Collection) values )
            {
                valueList.add( ObjectToValueMapper.map( value ) );
            }
        }
        else
        {
            valueList.add( ObjectToValueMapper.map( values ) );
        }

        return valueList;
    }

    private static Collection<String> getStringValues( final Object values )
    {
        List<String> valueList = Lists.newArrayList();

        if ( values instanceof Collection )
        {
            for ( final Object value : (Collection) values )
            {
                valueList.add( value.toString() );
            }
        }
        else
        {
            valueList.add( values.toString() );
        }

        return valueList;
    }

    private static void requiredFields( final Map<String, Object> map, final String... names )
    {
        for ( final String name : names )
        {
            if ( !map.containsKey( name ) )
            {
                throw new IllegalArgumentException( "Missing required field: [" + name + "] in filter" );
            }
        }
    }

    private static Filter createBoolFilter( final Map<String, Object> boolFilter )
    {
        final BooleanFilter.Builder filterBuilder = BooleanFilter.create();

        final Filters must = createFilters( toMap( boolFilter.get( "must" ) ) );
        final Filters mustNot = createFilters( toMap( boolFilter.get( "mustNot" ) ) );
        final Filters should = createFilters( toMap( boolFilter.get( "should" ) ) );

        for ( Filter filter : must )
        {
            filterBuilder.must( filter );
        }

        for ( Filter filter : mustNot )
        {
            filterBuilder.mustNot( filter );
        }

        for ( Filter filter : should )
        {
            filterBuilder.should( filter );
        }

        return filterBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap( final Object o )
    {
        try
        {
            return (Map<String, Object>) o;
        }
        catch ( ClassCastException e )
        {
            throw new IllegalArgumentException( "Filter not on expected format" );
        }
    }

}
