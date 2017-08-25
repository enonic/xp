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
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.site.Site;
import com.enonic.xp.testkit.mock.MockServiceRegistry;
import com.enonic.xp.testkit.portal.PortalTestSupport;

public class I18NScriptTest
    extends PortalTestSupport
{
    public I18NScriptTest()
    {
        setTestFiles( "test/*-test.js", "site/lib/xp/examples/i18n/*.js" );
    }

    @Override
    public void setupServices( final MockServiceRegistry registry )
    {
        super.setupServices( registry );

        final LocaleService localeService = Mockito.mock( LocaleService.class );

        final MessageBundle bundle = Mockito.mock( MessageBundle.class, (Answer) this::answer );
        Mockito.when(
            localeService.getBundle( Mockito.any( ApplicationKey.class ), Mockito.any( Locale.class ), Mockito.any( String[].class ) ) ).
            thenAnswer( mock -> bundle );

        registry.register( LocaleService.class, localeService );
    }

    @Override
    public void setupRequest( final PortalRequest request )
    {
        super.setupRequest( request );

        request.setSite( Site.create().
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
