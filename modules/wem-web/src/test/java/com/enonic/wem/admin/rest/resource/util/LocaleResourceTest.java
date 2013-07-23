package com.enonic.wem.admin.rest.resource.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.admin.rest.resource.util.model.LocaleJson;
import com.enonic.wem.admin.rest.resource.util.model.LocaleListJson;
import com.enonic.wem.api.Client;
import com.enonic.wem.core.locale.LocaleService;

import static org.junit.Assert.*;

public class LocaleResourceTest
{

    private Client client;
    private LocaleService localeService;

    @Before
    public void setup()
    {
        client = Mockito.mock( Client.class );
        localeService = Mockito.mock( LocaleService.class );

        Locale[] locales = new Locale[] { Locale.UK, Locale.ITALY, Locale.JAPAN };
        Mockito.when( localeService.getLocales() ).thenReturn( locales );
    }

    @Test
    public void testSpaceDeleteExistedSpaces()
        throws Exception
    {
        final LocaleResource localeResource = new LocaleResource();
        localeResource.setClient( client );
        localeResource.setLocaleService( localeService );

        LocaleListJson result = localeResource.list();

        assertNotNull( result );
        assertEquals( 3, result.getTotal() );

        List<String> names = new ArrayList<>( 3 );
        for ( final LocaleJson model : result.getLocales() )
        {
            names.add( model.getDisplayName() );
        }

        assertUnorderedArrayArrayEquals( new String[]{"English (United Kingdom)", "Italian (Italy)", "Japanese (Japan)"}, names.toArray() );
    }

    private static void assertUnorderedArrayArrayEquals(Object[] a1, Object[] a2) {
        Object[] b1 = a1.clone();
        Object[] b2 = a2.clone();

        Arrays.sort( b1 );
        Arrays.sort(b2);

        assertArrayEquals( b1, b2 );
    }

    private static void assertArrayEquals( final Object[] a1, final Object[] a2 ) {
        Assert.assertEquals( arrayToString( a1 ), arrayToString( a2 ) );
    }

    private static String arrayToString( final Object[] a ) {
        StringBuilder result = new StringBuilder( "[" );

        for ( int i = 0; i < a.length; i++ ) {
            result.append( i ).append( ": " ).append( a[i] );
            if ( i < a.length - 1 )
            {
                result.append( ", " );
            }
        }

        result.append( "]" );

        return result.toString();
    }
}
