package com.enonic.xp.portal.url;

import java.util.Objects;
import java.util.stream.StreamSupport;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;


@PublicApi
public abstract class AbstractUrlParams<T extends AbstractUrlParams>
{
    private String type = UrlTypeConstants.SERVER_RELATIVE;

    private final ListMultimap<String, String> params;

    public AbstractUrlParams()
    {
        this.params = MultimapBuilder.linkedHashKeys().arrayListValues().build();
    }

    public String getType()
    {
        return type;
    }

    public final ListMultimap<String, String> getParams()
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
        if ( value instanceof Iterable<?> values )
        {
            this.params.putAll( name, StreamSupport.stream( values.spliterator(), false ).map( Objects::toString ).toList() );
        }
        else
        {
            this.params.put( name, value.toString() );
        }
        return typecastThis();
    }

    @SuppressWarnings("unchecked")
    private T typecastThis()
    {
        return (T) this;
    }
}
