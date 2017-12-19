package com.enonic.xp.lib.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

public class JsonToFilterMapperTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void exists_filter()
        throws Exception
    {
        Map<String, Object> value = Maps.newHashMap();

        final HashMap<String, Object> existsFilter = Maps.newHashMap();
        existsFilter.put( "field", "myField" );

        value.put( "exists", existsFilter );

        final Filters filters = com.enonic.xp.lib.common.JsonToFilterMapper.create( value );

        assertTrue( filters.get( 0 ) instanceof ExistsFilter );
        assertEquals( "myField", ( (ExistsFilter) filters.get( 0 ) ).getFieldName() );
    }

    @Test
    public void notExists_filter()
        throws Exception
    {
        Map<String, Object> value = Maps.newHashMap();

        final HashMap<String, Object> notExistsFilter = Maps.newHashMap();
        notExistsFilter.put( "field", "myField" );

        value.put( "notExists", notExistsFilter );

        final Filters filters = com.enonic.xp.lib.common.JsonToFilterMapper.create( value );

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
        existsFilter.put( "field", "myField" );
        final HashMap<String, Object> existsFilterWrapper = Maps.newHashMap();
        existsFilterWrapper.put( "exists", existsFilter );
        final HashMap<String, Object> existsFilterWrapper2 = Maps.newHashMap();
        existsFilterWrapper2.put( "exists", existsFilter );

        final HashMap<String, Object> mustFilter = Maps.newHashMap();
        List<HashMap<String, Object>> existsFilters = Lists.newArrayList();
        existsFilters.add( existsFilterWrapper );
        existsFilters.add( existsFilterWrapper2 );

        mustFilter.put( "must", existsFilters );
        value.put( "boolean", mustFilter );

        final Filters filters = com.enonic.xp.lib.common.JsonToFilterMapper.create( value );

        assertTrue( filters.get( 0 ) instanceof BooleanFilter );
        final BooleanFilter booleanFilter = (BooleanFilter) filters.get( 0 );
        final ImmutableSet<Filter> mustFilters = booleanFilter.getMust();
        assertEquals( 2, mustFilters.size() );
        assertTrue( mustFilters.iterator().next() instanceof ExistsFilter );
        assertTrue( mustFilters.iterator().next() instanceof ExistsFilter );

        final ImmutableSet<Filter> mustNotFilter = booleanFilter.getMustNot();
        assertEquals( 0, mustNotFilter.size() );
    }

    @Test
    public void values_filter()
        throws Exception
    {
        Map<String, Object> value = Maps.newHashMap();

        List<String> values = Lists.newArrayList( "fisk", "ost", "løk" );
        final HashMap<String, Object> valueFilter = Maps.newHashMap();
        valueFilter.put( "field", "myField" );
        valueFilter.put( "values", values );

        value.put( "hasValue", valueFilter );

        final Filters filters = com.enonic.xp.lib.common.JsonToFilterMapper.create( value );

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

        final Filters filters = com.enonic.xp.lib.common.JsonToFilterMapper.create( value );

        assertTrue( filters.get( 0 ) instanceof IdFilter );
        assertEquals( 3, ( (IdFilter) filters.get( 0 ) ).getValues().size() );
    }

    @Test
    public void invalid_filter_type()
        throws IllegalArgumentException
    {
        Map<String, Object> value = Maps.newHashMap();
        value.put( "dummy", "ost" );

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "Unknown filter type [dummy]" );

        com.enonic.xp.lib.common.JsonToFilterMapper.create( value );
    }

    @Test
    public void invalid_filter_body()
        throws IllegalArgumentException
    {
        Map<String, Object> value = Maps.newHashMap();
        value.put( "exists", "ost" );

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "Filter not on expected format, expected Map, got java.lang.String" );

        com.enonic.xp.lib.common.JsonToFilterMapper.create( value );
    }
}

