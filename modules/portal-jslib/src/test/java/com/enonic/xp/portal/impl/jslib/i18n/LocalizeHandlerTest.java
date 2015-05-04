package com.enonic.xp.portal.impl.jslib.i18n;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.impl.jslib.AbstractHandlerTest;
import com.enonic.xp.portal.impl.jslib.locale.LocalizeHandler;
import com.enonic.xp.portal.script.command.CommandHandler;


public class LocalizeHandlerTest
    extends AbstractHandlerTest
{

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public static final Locale OVERRIDE_LOCAL = Locale.US;

    private LocaleService localeService;

    @Before
    public void setUp()
        throws Exception
    {
        final PortalContext context = new PortalContext();
        context.setSite( Site.newSite().
            name( ContentName.from( "test" ) ).
            parentPath( ContentPath.ROOT ).
            language( DEFAULT_LOCALE ).
            build() );

        PortalContextAccessor.set( context );
    }

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.localeService = Mockito.mock( LocaleService.class );

        final LocalizeHandler handler = new LocalizeHandler();
        handler.setLocaleService( this.localeService );

        return handler;
    }

    @Test
    public void localize_with_locale()
        throws Exception
    {
        final MessageBundle bundle = Mockito.mock( MessageBundle.class, (Answer) this::answer );

        setServiceAndRun( bundle, "localize_with_locale", OVERRIDE_LOCAL );
    }

    @Test
    public void localize_without_locale()
        throws Exception
    {
        final MessageBundle bundle = Mockito.mock( MessageBundle.class, (Answer) this::answer );

        setServiceAndRun( bundle, "localize_without_locale", DEFAULT_LOCALE );
    }

    @Test
    public void localize_without_params()
        throws Exception
    {
        final MessageBundle bundle = Mockito.mock( MessageBundle.class, (Answer) this::answer );

        setServiceAndRun( bundle, "localize_without_params", DEFAULT_LOCALE );
    }

    private void setServiceAndRun( final MessageBundle bundle, final String exportName, final Locale expectedLocale )
        throws Exception
    {
        Mockito.when( this.localeService.getBundle( Mockito.any( ModuleKey.class ), Mockito.eq( expectedLocale ) ) ).
            thenAnswer( mock -> bundle );

        execute( exportName );
    }

    private Object answer( final InvocationOnMock invocation )
    {
        final Object[] arguments = invocation.getArguments();

        return Arrays.toString( arguments );
    }
}
