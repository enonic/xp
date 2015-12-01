package com.enonic.xp.lib.cache;

import org.junit.Test;

import static org.junit.Assert.*;

public class CacheBeanBuilderTest
{
    @Test
    public void testBuild()
        throws Exception
    {
        final CacheBeanBuilder builder = new CacheBeanBuilder();
        builder.setExpire( 60 );
        builder.setSize( 100 );

        final CacheBean bean = builder.build();
        final Object value = bean.get( "key1", () -> "value1" );
        assertEquals( "value1", value );
        assertEquals( 1, bean.getSize() );

        bean.clear();
        assertEquals( 0, bean.getSize() );
    }
}
