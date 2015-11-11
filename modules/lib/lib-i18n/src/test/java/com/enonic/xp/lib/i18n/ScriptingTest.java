package com.enonic.xp.lib.i18n;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class ScriptingTest
    extends OldScriptTestSupport
{
    @Before
    public void setUp()
    {
        setupRequest();

        this.portalRequest.setSite( Site.create().
            name( ContentName.from( "test" ) ).
            parentPath( ContentPath.ROOT ).
            language( Locale.ENGLISH ).
            build() );

        final LocaleService localeService = Mockito.mock( LocaleService.class );

        final MessageBundle bundle = Mockito.mock( MessageBundle.class, (Answer) this::answer );
        Mockito.when( localeService.getBundle( Mockito.any( ApplicationKey.class ), Mockito.any( Locale.class ) ) ).
            thenAnswer( mock -> bundle );

        addService( LocaleService.class, localeService );
    }

    @Test
    public void testLocalize()
        throws Exception
    {
        runTestFunction( "test/localize-test.js", "localize" );
    }

    @Test
    public void testLocalize_withLocale()
        throws Exception
    {
        runTestFunction( "test/localize-test.js", "localize_with_locale" );
    }

    @Test
    public void testLocalize_withPlaceholders()
        throws Exception
    {
        runTestFunction( "test/localize-test.js", "localize_with_placeholders" );
    }

    private Object answer( final InvocationOnMock invocation )
    {
        final Object[] arguments = invocation.getArguments();
        return Arrays.toString( arguments );
    }
}
