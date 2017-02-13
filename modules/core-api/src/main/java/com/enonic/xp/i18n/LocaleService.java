package com.enonic.xp.i18n;

import java.util.Locale;

import com.enonic.xp.module.ModuleKey;

public interface LocaleService
{
    MessageBundle getBundle( ModuleKey module, Locale locale );
}
