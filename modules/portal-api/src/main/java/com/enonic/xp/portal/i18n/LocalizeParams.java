package com.enonic.xp.portal.i18n;

import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;

public class LocalizeParams
{
    private String key;

    private Locale locale;

    private Object[] args;

    private Pattern ARGS_PATTERN = Pattern.compile( "\\{([^,]*)(,([^,]*))*\\}" );

    private final PortalContext context;

    private final Multimap<String, String> params;

    public final Multimap<String, String> getParams()
    {
        return this.params;
    }

    public LocalizeParams( final PortalContext context )
    {
        this.params = HashMultimap.create();
        this.context = context;
    }

    private void key( final String key )
    {
        this.key = key;
    }

    public ModuleKey getModuleKey()
    {
        return this.context.getModule();
    }

    private void locale( final String locale )
    {
        this.locale = Strings.isNullOrEmpty( locale ) ? null : Locale.forLanguageTag( locale );
    }

    private void args( final String argsAsString )
    {

    }

    public LocalizeParams setAsMap( final Multimap<String, String> map )
    {
        key( singleValue( map, "_key" ) );
        locale( singleValue( map, "_locale" ) );
        args( singleValue( map, "_args" ) );
        getParams().putAll( map );
        return this;
    }

    private static String singleValue( final Multimap<String, String> map, final String name )
    {
        final Collection<String> values = map.removeAll( name );
        if ( values == null )
        {
            return null;
        }

        if ( values.isEmpty() )
        {
            return null;
        }

        return values.iterator().next();
    }

    public String getKey()
    {
        return key;
    }

    public Locale getLocale()
    {
        return this.locale != null ? this.locale : this.getContext().getSite().getLanguage();
    }

    private PortalContext getContext()
    {
        return context;
    }
}
