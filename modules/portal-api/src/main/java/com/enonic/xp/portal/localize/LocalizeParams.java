package com.enonic.xp.portal.localize;

import java.util.Collection;
import java.util.Locale;

import com.google.common.base.MoreObjects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;

public class LocalizeParams
{
    private String key;

    private Locale locale;

    private PortalContext context;

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

    public void key( final String key )
    {
        this.key = key;
    }

    public ModuleKey getModuleKey()
    {
        return this.context.getModule();
    }

    public void locale( final String locale )
    {
        this.locale = new Locale( locale );
    }

    public LocalizeParams setAsMap( final Multimap<String, String> map )
    {
        key( singleValue( map, "_key" ) );
        locale( singleValue( map, "_locale" ) );
        getParams().putAll( map );
        return this;
    }

    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        helper.omitNullValues();
        helper.add( "params", this.params );
    }

    protected static String singleValue( final Multimap<String, String> map, final String name )
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

    public PortalContext getContext()
    {
        return context;
    }
}
