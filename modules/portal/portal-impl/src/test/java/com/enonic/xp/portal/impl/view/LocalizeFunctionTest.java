package com.enonic.xp.portal.impl.view;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.site.Site;

import static org.junit.Assert.*;

public class LocalizeFunctionTest
    extends AbstractUrlViewFunctionTest
{
    private final LocaleService localeService = Mockito.mock( LocaleService.class );

    private final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );

    @Before
    public final void setupTest()
    {
        Site site = Site.create().
            description( "This is my site" ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            language( Locale.ENGLISH ).
            build();

        this.portalRequest.setSite( site );

        PortalRequestAccessor.set( this.portalRequest );
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
    public void no_bundle()
    {
        Mockito.when(
            localeService.getBundle( Mockito.eq( this.portalRequest.getApplicationKey() ), Mockito.eq( new Locale( "en", "US" ) ) ) ).
            thenReturn( null );

        final Object result = execute( "i18n.localize", "_key=myPhrase", "_locale=en-US  ", "a=5", "b=2" );
        assertEquals( "no localization bundle found in application 'myapplication'", result );
    }

    @Test
    public void array_params()
    {
        Mockito.when(
            localeService.getBundle( Mockito.eq( this.portalRequest.getApplicationKey() ), Mockito.eq( new Locale( "en", "US" ) ) ) ).
            thenReturn( messageBundle );

        Mockito.when( messageBundle.localize( Mockito.eq( "myPhrase" ), Mockito.anyVararg() ) ).
            thenReturn( "localizedString" );

        final Object result = execute( "i18n.localize", "_key=myPhrase", "_locale=en-US", "_values={a,1,date('2015-10-10T10:00Z')}" );

        assertEquals( "localizedString", result );
    }

    @Test
    public void all_params()
    {
        Mockito.when(
            localeService.getBundle( Mockito.eq( this.portalRequest.getApplicationKey() ), Mockito.eq( new Locale( "en", "US" ) ) ) ).
            thenReturn( messageBundle );

        Mockito.when( messageBundle.localize( Mockito.eq( "myPhrase" ), Matchers.<String>anyVararg() ) ).thenReturn( "localizedString" );

        final Object result = execute( "i18n.localize", "_key=myPhrase", "_locale=en-US  ", "a=5", "b=2" );
        assertEquals( "localizedString", result );

    }

    @Test
    public void no_locale()
    {
        Mockito.when( localeService.getBundle( Mockito.eq( this.portalRequest.getApplicationKey() ), Mockito.eq( new Locale( "en" ) ) ) ).
            thenReturn( messageBundle );

        Mockito.when( messageBundle.localize( Mockito.eq( "myPhrase" ), Matchers.<String>anyVararg() ) ).thenReturn( "localizedString" );

        final Object result = execute( "i18n.localize", "_key=myPhrase", "a=5", "b=2" );
        assertEquals( "localizedString", result );

    }

    @Test
    public void no_params()
    {
        Mockito.when( localeService.getBundle( Mockito.eq( this.portalRequest.getApplicationKey() ), Mockito.eq( new Locale( "en" ) ) ) ).
            thenReturn( messageBundle );

        Mockito.when( messageBundle.localize( Mockito.eq( "myPhrase" ), Matchers.<String>anyVararg() ) ).thenReturn( "localizedString" );

        final Object result = execute( "i18n.localize", "_key=myPhrase" );
        assertEquals( "localizedString", result );

    }

    @Test
    public void not_in_request_context()
    {
        final PortalRequest savedPortalRequest = PortalRequestAccessor.get();
        PortalRequestAccessor.set( null );

        try
        {
            Mockito.when( localeService.getBundle( Mockito.eq( ApplicationKey.from( "com.enonic.myapp" ) ),
                                                   Mockito.eq( new Locale( "en", "US" ) ) ) ).
                thenReturn( messageBundle );

            Mockito.when( messageBundle.localize( Mockito.eq( "myPhrase" ), Matchers.<String>anyVararg() ) ).thenReturn(
                "localizedString" );

            final Object result =
                execute( "i18n.localize", "_key=myPhrase", "_locale=en-US  ", "_application=com.enonic.myapp", "a=5", "b=2" );
            assertEquals( "localizedString", result );
        }
        finally
        {
            PortalRequestAccessor.set( savedPortalRequest );
        }
    }
}
