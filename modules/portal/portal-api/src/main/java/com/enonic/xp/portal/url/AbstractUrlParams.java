package com.enonic.xp.portal.url;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.portal.PortalRequest;

import static com.google.common.base.Strings.isNullOrEmpty;


@PublicApi
public abstract class AbstractUrlParams<T extends AbstractUrlParams>
{
    private String type = UrlTypeConstants.SERVER_RELATIVE;

    private final Multimap<String, String> params;

    public AbstractUrlParams()
    {
        this.params = LinkedListMultimap.create();
    }

    public String getType()
    {
        return type;
    }

    public final Multimap<String, String> getParams()
    {
        return this.params;
    }

    public final T type( final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            this.type = UrlTypeConstants.SERVER_RELATIVE;
        }
        else
        {
            this.type = value;
        }
        return typecastThis();
    }

    public final T param( final String name, final Object value )
    {
        final String strValue = value != null ? value.toString() : null;
        this.params.put( name, strValue );
        return typecastThis();
    }

    @Deprecated(since = "8")
    public final T portalRequest( final PortalRequest portalRequest )
    {
        return typecastThis();
    }

    @SuppressWarnings("unchecked")
    private T typecastThis()
    {
        return (T) this;
    }
}
