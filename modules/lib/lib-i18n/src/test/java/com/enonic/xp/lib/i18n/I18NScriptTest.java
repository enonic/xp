package com.enonic.xp.lib.i18n;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.ScriptRunnerSupport;

import static org.mockito.Matchers.any;

public class I18NScriptTest
    extends ScriptRunnerSupport
{
    @Override
    public String getScriptTestFile()
    {
        return "test/localize-test.js";
    }

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        final LocaleService localeService = Mockito.mock( LocaleService.class );

        final Set<Locale> locales = new LinkedHashSet<>();
        locales.add( new Locale( "en" ) );
        locales.add( new Locale( "es" ) );
        locales.add( new Locale( "ca" ) );
        Mockito.when( localeService.getLocales( any( ApplicationKey.class ), any( String[].class ) ) ).thenReturn( locales );

        final MessageBundle bundle = Mockito.mock( MessageBundle.class, (Answer) this::answer );
        Mockito.when(
            localeService.getBundle( Mockito.any( ApplicationKey.class ), Mockito.any( Locale.class ), Matchers.<String>anyVararg() ) ).
            thenReturn( bundle );

        addService( LocaleService.class, localeService );

        getPortalRequest().setSite( Site.create().
            name( ContentName.from( "test" ) ).
            parentPath( ContentPath.ROOT ).
            language( Locale.ENGLISH ).
            build() );
    }

    private Object answer( final InvocationOnMock invocation )
    {
        final Object[] arguments = invocation.getArguments();
        if ( invocation.getMethod().getName().equals( "asMap" ) )
        {
            final Map<String, String> map = Maps.newHashMap();
            map.put( "a", "1" );
            map.put( "b", "2" );
            return map;
        }

        return Arrays.toString( arguments );
    }
}
