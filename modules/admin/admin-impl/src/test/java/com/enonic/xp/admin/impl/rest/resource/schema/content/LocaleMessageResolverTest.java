package com.enonic.xp.admin.impl.rest.resource.schema.content;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;

import static org.junit.Assert.*;

public class LocaleMessageResolverTest
{
    private LocaleMessageResolver localeMessageResolver;

    private LocaleService localeService;

    @Before
    public void init()
    {

        Locale.setDefault( new Locale( "es", "ES" ) );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.valid" ) ).thenReturn( "translated" );

        this.localeService = Mockito.mock( LocaleService.class );
        Mockito.when( this.localeService.getBundle( Mockito.any(), Mockito.any() ) ).thenReturn( messageBundle );

        this.localeMessageResolver = new LocaleMessageResolver( localeService, ApplicationKey.from( "myApplication" ) );
    }

    @Test
    public void test_invalid()
    {
        Locale.setDefault( new Locale( "es", "ES" ) );
        final String result = localeMessageResolver.localizeMessage( "key.invalid", null );
        assertEquals( result, null );
    }

    @Test
    public void test_invalid_with_defaultValue()
    {
        Locale.setDefault( new Locale( "es", "ES" ) );
        final String result = localeMessageResolver.localizeMessage( "key.invalid", "defaultValue" );
        assertEquals( "defaultValue", result );
    }

    @Test
    public void test_valid()
    {
        Locale.setDefault( new Locale( "es", "ES" ) );
        final String result = localeMessageResolver.localizeMessage( "key.valid", "defaultValue" );
        assertEquals( "translated", result );
    }
}
