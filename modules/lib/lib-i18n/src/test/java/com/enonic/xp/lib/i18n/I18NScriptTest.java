package com.enonic.xp.lib.i18n;

import java.util.Arrays;
import java.util.Locale;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.script.ScriptRunnerSupport;

public class I18NScriptTest
    extends ScriptRunnerSupport
{
    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

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

    @Override
    public String getScriptTestFile()
    {
        return "/site/test/localize-test.js";
    }

    private Object answer( final InvocationOnMock invocation )
    {
        final Object[] arguments = invocation.getArguments();
        return Arrays.toString( arguments );
    }
}
