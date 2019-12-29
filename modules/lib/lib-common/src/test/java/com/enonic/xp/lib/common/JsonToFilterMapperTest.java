package com.enonic.xp.lib.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonToFilterMapperTest
{

    @Test
    public void exists_filter()
        throws Exception
    {
        final Map<String, Object> existsFilter = createExistsFilter();

        final Filters filters = com.enonic.xp.lib.common.JsonToFilterMapper.create( existsFilter );

        assertTrue( filters.get( 0 ) instanceof ExistsFilter );
        assertEquals( "myField", ( (ExistsFilter) filters.get( 0 ) ).getFieldName() );
    }

    @Test
    public void notExists_filter()
        throws Exception
    {
        Map<String, Object> value = new HashMap<>();

        final HashMap<String, Object> notExistsFilter = new HashMap<>();
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

    @SuppressWarnings("unchecked")
    @Test
    public void boolean_filter()
        throws Exception
    {
        Map<String, Object> filter = new HashMap<>();

        final Map<String, Object> boolFilter = new HashMap<>();
        boolFilter.put( "must", List.of( createExistsFilter(), createExistsFilter() ) );
        boolFilter.put( "mustNot", List.of( createExistsFilter() ) );
        boolFilter.put( "should", List.of( createExistsFilter(), createExistsFilter(), createExistsFilter() ) );

        filter.put( "boolean", boolFilter );

        final Filters filters = com.enonic.xp.lib.common.JsonToFilterMapper.create( filter );

        assertTrue( filters.get( 0 ) instanceof BooleanFilter );
        final BooleanFilter booleanFilter = (BooleanFilter) filters.get( 0 );

        final ImmutableSet<Filter> mustFilters = booleanFilter.getMust();
        assertEquals( 2, mustFilters.size() );
        assertTrue( mustFilters.iterator().next() instanceof ExistsFilter );
        assertTrue( mustFilters.iterator().next() instanceof ExistsFilter );

        final ImmutableSet<Filter> mustNotFilters = booleanFilter.getMustNot();
        assertEquals( 1, mustNotFilters.size() );

        final ImmutableSet<Filter> shouldFilters = booleanFilter.getShould();
        assertEquals( 3, shouldFilters.size() );
    }

    @Test
    public void values_filter()
        throws Exception
    {
        Map<String, Object> value = new HashMap<>();

        List<String> values = List.of( "fisk", "ost", "løk" );
        final HashMap<String, Object> valueFilter = new HashMap<>();
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
        Map<String, Object> value = new HashMap<>();

        List<String> values = List.of( "fisk", "ost", "løk" );
        final HashMap<String, Object> valueFilter = new HashMap<>();
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
        Map<String, Object> value = new HashMap<>();
        value.put( "dummy", "ost" );

        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            com.enonic.xp.lib.common.JsonToFilterMapper.create( value );
        });
        assertEquals( "Unknown filter type [dummy]", ex.getMessage());
    }

    @Test
    public void array_on_root_single()
        throws Exception
    {
        final List<Map<String, Object>> existsFilters = new ArrayList<>();
        existsFilters.add( createExistsFilter() );

        final Filters filters = com.enonic.xp.lib.common.JsonToFilterMapper.create( existsFilters );

        assertTrue( filters.get( 0 ) instanceof ExistsFilter );
    }

    @Test
    public void array_on_root()
        throws Exception
    {
        final List<Map<String, Object>> existsFilters = new ArrayList<>();
        existsFilters.add( createExistsFilter() );
        existsFilters.add( createExistsFilter() );
        existsFilters.add( createExistsFilter() );

        final Filters filters = com.enonic.xp.lib.common.JsonToFilterMapper.create( existsFilters );

        assertTrue( filters.get( 0 ) instanceof BooleanFilter );
        final ImmutableSet<Filter> mustFilters = ( (BooleanFilter) filters.get( 0 ) ).getMust();
        assertEquals( 3, mustFilters.size() );
    }

    private Map<String, Object> createExistsFilter()
    {
        Map<String, Object> filter = new HashMap<>();
        final Map<String, Object> filterValues = new HashMap<>();
        filterValues.put( "field", "myField" );
        filter.put( "exists", filterValues );
        return filter;
    }
}

