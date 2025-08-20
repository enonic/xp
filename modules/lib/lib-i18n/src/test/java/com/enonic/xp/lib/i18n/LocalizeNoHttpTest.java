package com.enonic.xp.lib.i18n;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.testing.ScriptRunnerSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class LocalizeNoHttpTest
    extends ScriptRunnerSupport
{
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
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        final LocaleService localeService = Mockito.mock( LocaleService.class );

        final Set<Locale> locales = new LinkedHashSet<>();
        locales.add( Locale.of( "en" ) );
        Mockito.when( localeService.getLocales( eq( applicationKey ), any() ) ).thenReturn( locales );

        final MessageBundle bundle = Mockito.mock( MessageBundle.class, this::answer );
        Mockito.when( localeService.getBundle( eq( applicationKey ), any(), any() ) ).
            thenReturn( bundle );

        addService( LocaleService.class, localeService );
    }

    private Object answer( final InvocationOnMock invocation )
    {
        final Object[] arguments = invocation.getArguments();
        if ( invocation.getMethod().getName().equals( "localize" ) )
        {
            final Map<String, String> map = new HashMap<>();
            map.put( "myKey", "value-1" );
            map.put( "myKey2", "value-2" );
            return map.get( invocation.getArgument( 0 ) );
        }

        return null;
    }

    protected PortalRequest createPortalRequest()
    {
        return null;
    }
}
