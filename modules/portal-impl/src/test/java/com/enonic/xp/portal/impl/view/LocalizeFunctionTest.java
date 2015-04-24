package com.enonic.xp.portal.impl.view;

import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.module.ModuleKey;

import static org.junit.Assert.*;

public class LocalizeFunctionTest
    extends AbstractUrlViewFunctionTest
{
    private LocaleService localeService = Mockito.mock( LocaleService.class );

    private MessageBundle messageBundle = Mockito.mock( MessageBundle.class );

    @Override
    protected void setupFunction()
        throws Exception
    {
        final LocalizeFunction function = new LocalizeFunction();
        function.setLocaleService( localeService );
        register( function );
        Mockito.when( localeService.getBundle( Mockito.any( ModuleKey.class ), Mockito.any( Locale.class ) ) ).thenReturn( messageBundle );
        Mockito.when( messageBundle.localize( Mockito.any( String.class ) ) ).thenReturn( "localizedString" );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "localize", "key=testKey" );
        assertEquals( "localizedString", result );
    }
}
