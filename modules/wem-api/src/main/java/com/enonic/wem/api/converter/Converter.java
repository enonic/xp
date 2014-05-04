package com.enonic.wem.api.converter;

// https://github.com/spring-projects/spring-framework/tree/master/spring-core/src/main/java/org/springframework/core/convert/support
public interface Converter<S, T>
{
    public T convert( S source );
}
