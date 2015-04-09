package com.enonic.xp.portal.url;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalContext;

@Beta
public abstract class AbstractUrlParams<T extends AbstractUrlParams>
{
    private PortalContext context;

    private final Multimap<String, String> params;

    public AbstractUrlParams()
    {
        this.params = HashMultimap.create();
    }

    public final Multimap<String, String> getParams()
    {
        return this.params;
    }

    public final PortalContext getContext()
    {
        return this.context;
    }

    public final T param( final String name, final Object value )
    {
        final String strValue = value != null ? value.toString() : null;
        this.params.put( name, strValue );
        return typecastThis();
    }

    public final T context( final PortalContext value )
    {
        this.context = value;
        return typecastThis();
    }

    public abstract T setAsMap( Multimap<String, String> map );

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

    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        helper.omitNullValues();
        helper.add( "params", this.params );
    }

    @SuppressWarnings("unchecked")
    private T typecastThis()
    {
        return (T) this;
    }

    @Override
    public final String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        buildToString( helper );
        return helper.toString();
    }
}
