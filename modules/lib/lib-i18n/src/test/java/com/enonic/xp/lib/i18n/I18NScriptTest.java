package com.enonic.xp.lib.i18n;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

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

        final MessageBundle bundle = Mockito.mock( MessageBundle.class, (Answer) this::answer );
        Mockito.when(
            localeService.getBundle( Mockito.any( ApplicationKey.class ), Mockito.any( Locale.class ), Mockito.any( String[].class ) ) ).
            thenAnswer( mock -> bundle );

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
