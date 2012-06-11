package com.enonic.wem.web.jsp.site;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class SiteBeanImplTest
{
    @Test
    public void testSimple()
    {
        final SiteBeanImpl bean = new SiteBeanImpl();

        bean.setKey( 11 );
        assertEquals( 11, bean.getKey() );

        bean.setName( "Hello" );
        assertEquals( "Hello", bean.getName() );
    }

    @Test
    public void testCompare()
    {
        final SiteBeanImpl bean1 = new SiteBeanImpl();
        bean1.setKey( 11 );
        bean1.setName( "Hello" );

        final SiteBeanImpl bean2 = new SiteBeanImpl();
        bean2.setKey( 1 );
        bean2.setName( "World" );

        final List<SiteBeanImpl> beans = Lists.newArrayList(bean2, bean1);

        assertSame( bean2, beans.get( 0 ) );
        assertSame( bean1, beans.get( 1 ) );

        Collections.sort(beans);

        assertSame( bean1, beans.get( 0 ) );
        assertSame( bean2, beans.get( 1 ) );
    }
}
