package com.enonic.xp.lib.i18n;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.SequencedSet;

import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.ScriptRunnerSupport;

import static org.mockito.ArgumentMatchers.any;

class GetSupportedLocalesScriptTest
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

        final SequencedSet<Locale> locales =
            new LinkedHashSet<>( List.of( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "es" ), Locale.forLanguageTag( "ca" ) ) );

        Mockito.when( localeService.getLocales( any( ApplicationKey.class ), any( String[].class ) ) ).thenReturn( locales );

        addService( LocaleService.class, localeService );

        this.portalRequest.setSite(
            Site.create().name( ContentName.from( "test" ) ).parentPath( ContentPath.ROOT ).language( Locale.ENGLISH ).build() );
    }
}
