package com.enonic.xp.lib.i18n;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.ScriptRunnerSupport;

import static org.mockito.Matchers.any;

public class GetSupportedLocalesScriptTest
    extends ScriptRunnerSupport
{
    @Override
    public String getScriptTestFile()
    {
        return "test/getSupportedLocales-test.js";
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

        addService( LocaleService.class, localeService );

        getPortalRequest().setSite( Site.create().
            name( ContentName.from( "test" ) ).
            parentPath( ContentPath.ROOT ).
            language( Locale.ENGLISH ).
            build() );
    }

}
