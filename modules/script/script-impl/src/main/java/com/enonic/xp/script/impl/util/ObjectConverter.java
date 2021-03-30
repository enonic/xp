package com.enonic.xp.script.impl.util;

import java.util.Map;

public interface ObjectConverter
{
    Object toJs( Object value );

    Object[] toJsArray( Object[] values );

    Object fromJs( Object value );

    Map<String, Object> toMap( Object source );
}
