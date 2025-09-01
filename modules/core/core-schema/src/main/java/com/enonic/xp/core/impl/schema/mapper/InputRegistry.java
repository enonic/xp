package com.enonic.xp.core.impl.schema.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.enonic.xp.inputtype.InputTypeName;

public final class InputRegistry
{
    private static final Map<InputTypeName, Class<? extends InputYml>> TYPES = new ConcurrentHashMap<>();

    static
    {
        register( RadioButtonYml.INPUT_TYPE_NAME, RadioButtonYml.class );
        register( TextLineYml.INPUT_TYPE_NAME, TextLineYml.class );
        register( DoubleYml.INPUT_TYPE_NAME, DoubleYml.class );
        register( ContentSelectorYml.INPUT_TYPE_NAME, ContentSelectorYml.class );
        register( CustomSelectorYml.INPUT_TYPE_NAME, CustomSelectorYml.class );
        register( HtmlAreaYml.INPUT_TYPE_NAME, HtmlAreaYml.class );
        register( TextAreaYml.INPUT_TYPE_NAME, TextAreaYml.class );
        register( DateYml.INPUT_TYPE_NAME, DateYml.class );
        register( DateTimeYml.INPUT_TYPE_NAME, DateTimeYml.class );
        register( TimeYml.INPUT_TYPE_NAME, TimeYml.class );
        register( CheckBoxYml.INPUT_TYPE_NAME, CheckBoxYml.class );
        register( ComboBoxYml.INPUT_TYPE_NAME, ComboBoxYml.class );
        register( AttachmentUploaderYml.INPUT_TYPE_NAME, AttachmentUploaderYml.class );
        register( ImageSelectorYml.INPUT_TYPE_NAME, ImageSelectorYml.class );
        register( MediaSelectorYml.INPUT_TYPE_NAME, MediaSelectorYml.class );
    }

    @SuppressWarnings("unchecked")
    public static <T extends InputYml> Class<T> getInputType( final String typeName )
    {
        return (Class<T>) TYPES.get( InputTypeName.from( typeName ) );
    }

    public static <T extends InputYml> void register( final InputTypeName typeName, final Class<T> type )
    {
        TYPES.put( typeName, type );
    }
}
