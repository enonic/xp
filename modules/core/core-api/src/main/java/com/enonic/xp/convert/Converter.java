package com.enonic.xp.convert;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface Converter<T>
{
    Class<T> getType();

    T convert( Object value );
}
