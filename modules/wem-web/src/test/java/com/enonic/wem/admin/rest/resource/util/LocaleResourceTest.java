package com.enonic.wem.admin.rest.resource.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.TestUtil;
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

        final Locale[] locales = new Locale[] { Locale.UK, Locale.ITALY, Locale.JAPAN };
        Mockito.when( localeService.getLocales() ).thenReturn( locales );
    }

    @Test
    public void testSpaceDeleteExistedSpaces()
        throws Exception
    {
        final LocaleResource resource = new LocaleResource();
        resource.setClient( client );
        resource.setLocaleService( localeService );

        LocaleListJson result = resource.list();

        Mockito.verify( localeService, Mockito.times( 1 ) ).getLocales();

        assertNotNull( result );
        assertEquals( 3, result.getTotal() );

        List<String> names = new ArrayList<>( 3 );
        for ( final LocaleJson model : result.getLocales() )
        {
            names.add( model.getDisplayName() );
        }

        TestUtil.assertUnorderedArraysEquals( new String[]{"English (United Kingdom)", "Italian (Italy)", "Japanese (Japan)"}, names.toArray() );
    }
}
