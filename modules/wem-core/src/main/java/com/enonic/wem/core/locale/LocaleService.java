package com.enonic.wem.core.locale;

import java.util.Locale;

import com.google.inject.ImplementedBy;

@ImplementedBy(LocaleServiceImpl.class)
public interface LocaleService
{
    Locale[] getLocales();
}
