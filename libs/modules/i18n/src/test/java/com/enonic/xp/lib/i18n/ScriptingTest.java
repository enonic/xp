package com.enonic.xp.lib.i18n;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class ScriptingTest
    extends ScriptTestSupport
{
    @Before
    public void setUp()
    {
        final PortalRequest request = new PortalRequest();
        request.setSite( Site.newSite().
            name( ContentName.from( "test" ) ).
            parentPath( ContentPath.ROOT ).
            language( Locale.ENGLISH ).
            build() );

        PortalRequestAccessor.set( request );

        final LocaleService localeService = Mockito.mock( LocaleService.class );

        final LocaleScriptBean bean = new LocaleScriptBean();
        bean.setLocaleService( localeService );

        final MessageBundle bundle = Mockito.mock( MessageBundle.class, (Answer) this::answer );
        Mockito.when( localeService.getBundle( Mockito.any( ModuleKey.class ), Mockito.any( Locale.class ) ) ).
            thenAnswer( mock -> bundle );

        addBean( "com.enonic.xp.lib.i18n.LocaleScriptBean", bean );
    }

    @Test
    public void testLocalize()
        throws Exception
    {
        final ScriptExports exports = runTestScript( "test/localize-test.js" );
        exports.executeMethod( "localize" );
    }

    @Test
    public void testLocalize_withLocale()
        throws Exception
    {
        final ScriptExports exports = runTestScript( "test/localize-test.js" );
        exports.executeMethod( "localize_with_locale" );
    }

    @Test
    public void testLocalize_withPlaceholders()
        throws Exception
    {
        final ScriptExports exports = runTestScript( "test/localize-test.js" );
        exports.executeMethod( "localize_with_placeholders" );
    }

    private Object answer( final InvocationOnMock invocation )
    {
        final Object[] arguments = invocation.getArguments();
        return Arrays.toString( arguments );
    }
}
