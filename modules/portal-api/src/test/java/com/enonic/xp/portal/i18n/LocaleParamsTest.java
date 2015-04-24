package com.enonic.xp.portal.i18n;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class LocaleParamsTest

{
    @Test
    public void testKey()
    {
        final LocaleParams params = new LocaleParams();
        assertNull( params.getKey() );

        params.setKey( "" );
        assertNull( params.getKey() );

        params.setKey( "123456" );
        assertEquals( "123456", params.getKey() );
    }

    @Test
    public void testLocale()
    {
        final LocaleParams params = new LocaleParams();
        assertNull( params.getLocale() );

        params.setLocale( "" );
        assertNull( params.getLocale() );

        params.setLocale( "en" );
        assertEquals( "en", params.getLocale() );
    }

    @Test
    public void testParams()
    {
        final LocaleParams params = new LocaleParams();
        assertNull( params.getParams() );

        params.setParams( null );
        assertNull( params.getParams() );

        List<Object> list = Lists.newArrayList( 1, "a" );

        params.setParams( list );
        assertEquals( list, params.getParams() );
    }


}
