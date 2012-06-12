package com.enonic.wem.web.rest2.locale;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.web.rest2.AbstractResourceTest;

import com.enonic.cms.core.locale.LocaleService;

public class LocaleResourceTest
    extends AbstractResourceTest
{
    private LocaleResource resource;

    private LocaleService localeService;

    @Before
    public void setUp()
    {
        this.localeService = Mockito.mock( LocaleService.class );
        this.resource = new LocaleResource();
        this.resource.setLocaleService( this.localeService );
    }

    @Test
    public void testGetAll_empty()
        throws Exception
    {
        Mockito.when( this.localeService.getLocales() ).thenReturn( new Locale[0] );
        final LocaleResult result = this.resource.getAll();
        assertJsonResult( "getAll_empty.json", result );
    }

    @Test
    public void testGetAll_list()
        throws Exception
    {
        Mockito.when( this.localeService.getLocales() ).thenReturn( new Locale[]{Locale.ENGLISH, Locale.GERMAN} );
        final LocaleResult result = this.resource.getAll();
        assertJsonResult( "getAll_list.json", result );
    }
}
