package com.enonic.wem.script.internal.bean;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class BeanFieldMapperTest
{
    @Test
    public void testEmptyMap()
    {
        final TestBean bean = new TestBean();
        final Map<String, Object> map = Maps.newHashMap();

        BeanFieldMapper.mapToBean( bean, map );
        assertNull( bean.name );
    }

    @Test
    public void testSimpleBean()
    {
        final TestBean bean = new TestBean();
        final Map<String, Object> map = Maps.newHashMap();
        map.put( "name", "test" );

        BeanFieldMapper.mapToBean( bean, map );
        assertEquals( "test", bean.name );
    }

    @Test
    public void testIntProperty()
    {
        final TestBean bean = new TestBean();
        final Map<String, Object> map = Maps.newHashMap();
        map.put( "age", "33" );

        BeanFieldMapper.mapToBean( bean, map );
        assertEquals( 33, bean.age );
    }

    @Test
    public void testMapProperty()
    {
        final TestBean bean = new TestBean();
        final Map<String, Object> map = Maps.newHashMap();
        map.put( "properties", Maps.newHashMap() );

        BeanFieldMapper.mapToBean( bean, map );
        assertNotNull( bean.properties );
    }
}
