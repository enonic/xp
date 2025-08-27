package com.enonic.xp.core.impl.schema.mapper;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.enonic.xp.inputtype.InputTypeName;

public final class InputRegistry
{
    private static final Map<Class<? extends InputYml>, InputTypeName> FACTORIES = new ConcurrentHashMap<>();

    static
    {
        register( RadioButtonYml.class, InputTypeName.RADIO_BUTTON );
        register( TextLineYml.class, InputTypeName.TEXT_LINE );
        register( DoubleYml.class, InputTypeName.DOUBLE );
        register( ContentSelectorYml.class, InputTypeName.CONTENT_SELECTOR );
    }

    public static Map<Class<? extends InputYml>, InputTypeName> getFactories()
    {
        return Collections.unmodifiableMap( FACTORIES );
    }

    public static <T extends InputYml> void register( final Class<T> type, final InputTypeName typeName )
    {
        FACTORIES.put( type, typeName );
    }
}
