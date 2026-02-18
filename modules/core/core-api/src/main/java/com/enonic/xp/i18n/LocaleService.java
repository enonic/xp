package com.enonic.xp.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;

@NullMarked
public interface LocaleService
{
    MessageBundle getBundle( ApplicationKey applicationKey, @Nullable Locale locale );

    MessageBundle getBundle( ApplicationKey applicationKey, @Nullable Locale locale, String... bundleNames );

    Set<Locale> getLocales( ApplicationKey applicationKey, String... bundleNames );

    @Nullable Locale getSupportedLocale( List<Locale> preferredLocales, ApplicationKey applicationKey, String... bundleNames );
}
