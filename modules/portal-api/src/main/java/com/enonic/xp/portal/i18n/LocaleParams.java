package com.enonic.xp.portal.i18n;

import java.util.List;

import com.google.common.base.Strings;

public class LocaleParams
{
    private String key;

    private String locale;

    private List<Object> params;

    public String getKey()
    {
        return key;
    }

    public String getLocale()
    {
        return locale;
    }

    public List<Object> getParams()
    {
        return params;
    }

    public LocaleParams setKey( final String key )
    {
        this.key = Strings.emptyToNull( key );
        return this;
    }

    public LocaleParams setLocale( final String locale )
    {
        this.locale = Strings.emptyToNull( locale );
        return this;
    }

    public LocaleParams setParams( final List<Object> params )
    {
        this.params = params;
        return this;
    }
}
