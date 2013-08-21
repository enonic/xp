package com.enonic.wem.admin.rest.resource.util;

import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest2;
import com.enonic.wem.core.locale.LocaleService;

public class LocaleResourceTest
    extends AbstractResourceTest2
{
    @Override
    protected Object getResourceInstance()
    {
        final LocaleService localeService = Mockito.mock( LocaleService.class );

        final Locale[] locales = new Locale[]{Locale.UK, Locale.ITALY, Locale.JAPAN};
        Mockito.when( localeService.getLocales() ).thenReturn( locales );

        final LocaleResource resource = new LocaleResource();
        resource.setLocaleService( localeService );

        return resource;
    }

    @Test
    public void testList()
        throws Exception
    {
        final String json = resource().path( "util/locale" ).get( String.class );
        assertJson( "locale_list.json", json );
    }
}
