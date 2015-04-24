package com.enonic.xp.portal.impl.view;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.module.ModuleKey;

import static org.junit.Assert.*;

public class LocalizeFunctionTest
    extends AbstractUrlViewFunctionTest
{
    private LocaleService localeService = Mockito.mock( LocaleService.class );

    private MessageBundle messageBundle = Mockito.mock( MessageBundle.class );

    @Before
    public final void setupTest()
    {
        Site site = Site.newSite().
            description( "This is my site" ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            language( Locale.ENGLISH ).
            build();

        this.context.setSite( site );

        Mockito.when( localeService.getBundle( Mockito.eq( this.context.getModule() ),
                                               Mockito.eq( this.context.getSite().getLanguage() ) ) ).thenReturn( messageBundle );
        Mockito.when( messageBundle.localize( Mockito.eq( "testKey" ) ) ).thenReturn( "localizedString" );
    }

    @Override
    protected void setupFunction()
        throws Exception
    {
        final LocalizeFunction function = new LocalizeFunction();
        function.setLocaleService( localeService );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "localize", "key=testKey" );
        assertEquals( "localizedString", result );

        Mockito.verify( localeService, Mockito.times( 1 ) ).getBundle( Mockito.eq( this.context.getModule() ),
                                                                       Mockito.eq( this.context.getSite().getLanguage() ) );
        Mockito.verify( messageBundle, Mockito.times( 1 ) ).localize( Mockito.eq( "testKey" ) );
    }
}
