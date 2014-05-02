package com.enonic.wem.api.data.type;

public interface ValueType<T>
{
    public int getKey();

    public String getName();

    public Class<T> getJavaType();

    public T convert( Object object );
}
