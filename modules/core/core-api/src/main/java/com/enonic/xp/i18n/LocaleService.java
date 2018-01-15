package com.enonic.xp.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.enonic.xp.app.ApplicationKey;

public interface LocaleService
{
    MessageBundle getBundle( ApplicationKey applicationKey, Locale locale );

    MessageBundle getBundle( ApplicationKey applicationKey, Locale locale, String... bundleNames );

    Set<Locale> getLocales( ApplicationKey applicationKey, String... bundleNames );

    Locale getSupportedLocale( List<Locale> preferredLocales, ApplicationKey applicationKey, String... bundleNames );
}
