package com.enonic.xp.script.impl.util;

import java.util.function.Function;

public interface JavascriptHelper<T>
{
    T newJsArray();

    T newJsObject();

    Object newFunction( Function<?, ?> function);

    T parseJson( String text );

    Object eval(String script);

    ObjectConverter objectConverter();
}
