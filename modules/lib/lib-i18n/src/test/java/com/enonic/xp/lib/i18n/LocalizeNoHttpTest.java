package com.enonic.xp.lib.i18n;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Ignore;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.testing.ScriptRunnerSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@Ignore("Concourse issue")
public class LocalizeNoHttpTest
    extends ScriptRunnerSupport
{
    private PortalRequest portalRequest;

    @Override
    public String getScriptTestFile()
    {
        return "test/localize-nohttp-test.js";
    }

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        final LocaleService localeService = Mockito.mock( LocaleService.class );

        final Set<Locale> locales = new LinkedHashSet<>();
        locales.add( new Locale( "en" ) );
        Mockito.when(
            localeService.getLocales( eq( ApplicationKey.from( "com.enonic.myapplication" ) ), any( String[].class ) ) ).thenReturn(
            locales );

        final MessageBundle bundle = Mockito.mock( MessageBundle.class, this::answer );
        Mockito.when( localeService.getBundle( eq( ApplicationKey.from( "com.enonic.myapplication" ) ), Mockito.any( Locale.class ),
                                               any() ) ).
            thenReturn( bundle );

        addService( LocaleService.class, localeService );
    }

    @After
    public void tearDown()
    {
        if ( this.portalRequest != null )
        {
            PortalRequestAccessor.set( this.portalRequest );
        }
    }

    private Object answer( final InvocationOnMock invocation )
    {
        final Object[] arguments = invocation.getArguments();
        if ( invocation.getMethod().getName().equals( "localize" ) )
        {
            final Map<String, String> map = new HashMap<>();
            map.put( "myKey", "value-1" );
            map.put( "myKey2", "value-2" );
            return map.get( arguments[0] );
        }

        return null;
    }

    public void removeRequest()
    {
        this.portalRequest = PortalRequestAccessor.get();
        PortalRequestAccessor.remove();
    }

}
