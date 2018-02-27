package com.enonic.xp.lib.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;

public class JsonToFilterMapper
{
    private static final String FIELDNAME_FIELD = "field";

    private static final String NOT_EXISTS_FILTER = "notExists";

    private static final String EXISTS_FILTER = "exists";

    private static final String BOOLEAN_FILTER = "boolean";

    private static final String HAS_VALUE_FILTER = "hasValue";

    private static final String IDS_FILTER = "ids";

    private static final String VALUES_FIELD = "values";

    public static Filters create( final List<Map<String, Object>> value )
    {
        if ( value != null && value.size() > 1 )
        {
            return wrapInBooleanMust( createFilters( value ) );
        }

        return createFilters( value );
    }

    public static Filters create( final Map<String, Object> value )
    {
        return createFilters( value );
    }

    private static Filters wrapInBooleanMust( final Filters filters )
    {
        final BooleanFilter.Builder builder = BooleanFilter.create();

        filters.forEach( builder::must );

        return Filters.create().
            add( builder.build() ).
            build();
    }

    @SuppressWarnings("unchecked")
    private static Filters createFilters( final Object object )
    {
        if ( object == null )
        {
            return Filters.empty();
        }

        if ( object instanceof Map )
        {
            return createFilters( (Map<String, Object>) object );
        }

        if ( object instanceof Collection )
        {
            return createFilters( (Collection<Object>) object );
        }

        throw new IllegalArgumentException( "Filter not on expected format, expected Collection, got " + object.getClass().getName() );
    }

    private static Filters createFilters( final Collection<Object> collection )
    {
        final Filters.Builder builder = Filters.create();

        for ( final Object o : collection )
        {
            builder.addAll( createFilters( o ).getList() );
        }

        return builder.build();
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
            case NOT_EXISTS_FILTER:
            {
                return createNotExistsFilter( toMap( filter ) );
            }
            case EXISTS_FILTER:
            {
                return createExistsFilter( toMap( filter ) );
            }
            case BOOLEAN_FILTER:
            {
                return createBoolFilter( toMap( filter ) );
            }
            case HAS_VALUE_FILTER:
            {
                return createValueFilter( toMap( filter ) );
            }
            case IDS_FILTER:
            {
                return createIdsFilter( toMap( filter ) );
            }
            default:
                throw new IllegalArgumentException( "Unknown filter type [" + key + "]" );
        }
    }

    private static Filter createIdsFilter( final Map<String, Object> idsFilter )
    {
        requiredFields( idsFilter, VALUES_FIELD );

        return IdFilter.create().
            values( getStringValues( idsFilter.get( VALUES_FIELD ) ) ).
            build();
    }

    private static Filter createExistsFilter( final Map<String, Object> existsFilter )
    {
        requiredFields( existsFilter, FIELDNAME_FIELD );

        return ExistsFilter.create().
            fieldName( existsFilter.get( FIELDNAME_FIELD ).toString() ).
            build();
    }

    private static Filter createNotExistsFilter( final Map<String, Object> existsFilter )
    {
        requiredFields( existsFilter, FIELDNAME_FIELD );

        return BooleanFilter.create().
            mustNot( ExistsFilter.create().
                fieldName( existsFilter.get( FIELDNAME_FIELD ).toString() ).
                build() ).
            build();
    }

    private static Filter createValueFilter( final Map<String, Object> valueFilter )
    {
        requiredFields( valueFilter, FIELDNAME_FIELD, VALUES_FIELD );

        final ValueFilter.Builder builder = ValueFilter.create().
            fieldName( valueFilter.get( FIELDNAME_FIELD ).toString() ).
            addAllValues( getValues( valueFilter.get( VALUES_FIELD ) ) );

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

        for ( Filter filter : createFilters( boolFilter.get( "must" ) ) )
        {
            filterBuilder.must( filter );
        }

        for ( Filter filter : createFilters( boolFilter.get( "mustNot" ) ) )
        {
            filterBuilder.mustNot( filter );
        }

        for ( Filter filter : createFilters( boolFilter.get( "should" ) ) )
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
            throw new IllegalArgumentException( "Filter not on expected format, expected Map, got " + o.getClass().getName() );
        }
    }

}
