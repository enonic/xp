package com.enonic.xp.admin.impl.rest.resource.schema.content;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocaleMessageResolverTest
{
    private LocaleMessageResolver localeMessageResolver;

    private LocaleService localeService;

    private MessageBundle messageBundle;

    @BeforeEach
    public void init()
    {

        Locale.setDefault( new Locale( "es", "ES" ) );

        messageBundle = Mockito.mock( MessageBundle.class );
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

    @Test
    public void test_valid_key_with_invalid_value()
    {
        Mockito.when( messageBundle.localize( "key.valid" ) ).thenThrow(new IllegalArgumentException() );
        Mockito.when( messageBundle.getMessage( "key.valid" ) ).thenReturn( "invalid value" );

        Locale.setDefault( new Locale( "es", "ES" ) );
        final String result = localeMessageResolver.localizeMessage( "key.valid", "defaultValue" );
        assertEquals( "invalid value", result );
    }
}
