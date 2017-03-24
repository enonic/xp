package com.enonic.xp.lib.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;


public class FilterParamsFactoryTest
{

    @Test
    public void exists_filter()
        throws Exception
    {
        Map<String, Object> value = Maps.newHashMap();

        final HashMap<String, Object> existsFilter = Maps.newHashMap();
        existsFilter.put( "fieldName", "myField" );

        value.put( "exists", existsFilter );

        final Filters filters = FilterParamsFactory.create( value );

        assertTrue( filters.get( 0 ) instanceof ExistsFilter );
        assertEquals( "myField", ( (ExistsFilter) filters.get( 0 ) ).getFieldName() );
    }

    @Test
    public void notExists_filter()
        throws Exception
    {
        Map<String, Object> value = Maps.newHashMap();

        final HashMap<String, Object> notExistsFilter = Maps.newHashMap();
        notExistsFilter.put( "fieldName", "myField" );

        value.put( "notExists", notExistsFilter );

        final Filters filters = FilterParamsFactory.create( value );

        assertTrue( filters.get( 0 ) instanceof BooleanFilter );
        final BooleanFilter booleanFilter = (BooleanFilter) filters.get( 0 );
        final ImmutableSet<Filter> mustNotFilters = booleanFilter.getMustNot();
        assertEquals( 1, mustNotFilters.size() );
        final Filter mustNotFilter = mustNotFilters.iterator().next();
        assertTrue( mustNotFilter instanceof ExistsFilter );
    }

    @Test
    public void boolean_filter()
        throws Exception
    {
        Map<String, Object> value = Maps.newHashMap();

        final HashMap<String, Object> existsFilter = Maps.newHashMap();
        existsFilter.put( "fieldName", "myField" );

        final HashMap<String, Object> existsFilterWrapper = Maps.newHashMap();
        existsFilterWrapper.put( "exists", existsFilter );

        final HashMap<String, Object> mustFilter = Maps.newHashMap();
        mustFilter.put( "must", existsFilterWrapper );

        value.put( "boolean", mustFilter );

        final Filters filters = FilterParamsFactory.create( value );

        assertTrue( filters.get( 0 ) instanceof BooleanFilter );
        final BooleanFilter booleanFilter = (BooleanFilter) filters.get( 0 );
        final ImmutableSet<Filter> mustFilters = booleanFilter.getMust();
        assertEquals( 1, mustFilters.size() );
        final Filter mustNotFilter = mustFilters.iterator().next();
        assertTrue( mustNotFilter instanceof ExistsFilter );
    }

    @Test
    public void values_filter()
        throws Exception
    {
        Map<String, Object> value = Maps.newHashMap();

        List<String> values = Lists.newArrayList( "fisk", "ost", "løk" );
        final HashMap<String, Object> valueFilter = Maps.newHashMap();
        valueFilter.put( "fieldName", "myField" );
        valueFilter.put( "values", values );

        value.put( "hasValue", valueFilter );

        final Filters filters = FilterParamsFactory.create( value );

        assertTrue( filters.get( 0 ) instanceof ValueFilter );
        assertEquals( "myField", ( (ValueFilter) filters.get( 0 ) ).getFieldName() );
        assertEquals( 3, ( (ValueFilter) filters.get( 0 ) ).getValues().size() );
    }

    @Test
    public void ids_filter()
        throws Exception
    {
        Map<String, Object> value = Maps.newHashMap();

        List<String> values = Lists.newArrayList( "fisk", "ost", "løk" );
        final HashMap<String, Object> valueFilter = Maps.newHashMap();
        valueFilter.put( "values", values );

        value.put( "ids", valueFilter );

        final Filters filters = FilterParamsFactory.create( value );

        assertTrue( filters.get( 0 ) instanceof IdFilter );
        assertEquals( 3, ( (IdFilter) filters.get( 0 ) ).getValues().size() );
    }

}

