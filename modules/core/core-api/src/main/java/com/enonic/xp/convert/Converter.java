package com.enonic.xp.convert;

import com.google.common.annotations.Beta;

@Beta
public interface Converter<T>
{
    Class<T> getType();

    T convert( Object value );
}
