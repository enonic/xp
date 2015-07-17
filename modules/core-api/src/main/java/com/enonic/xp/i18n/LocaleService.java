package com.enonic.xp.i18n;

import java.util.Locale;

import com.enonic.xp.app.ApplicationKey;

public interface LocaleService
{
    MessageBundle getBundle( ApplicationKey applicationKey, Locale locale );
}
