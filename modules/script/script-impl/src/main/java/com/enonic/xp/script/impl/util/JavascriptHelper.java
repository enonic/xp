package com.enonic.xp.script.impl.util;

public interface JavascriptHelper<T>
{
    T newJsArray();

    T newJsObject();

    T parseJson( String text );

    Object eval(String script);

    ObjectConverter objectConverter();
}
