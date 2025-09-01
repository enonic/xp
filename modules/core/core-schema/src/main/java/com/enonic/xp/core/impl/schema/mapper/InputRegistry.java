package com.enonic.xp.core.impl.schema.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.enonic.xp.inputtype.InputTypeName;

public final class InputRegistry
{
    private static final Map<InputTypeName, Class<? extends InputYml>> TYPES = new ConcurrentHashMap<>();

    static
    {
        register( InputTypeName.RADIO_BUTTON, RadioButtonYml.class );
        register( InputTypeName.TEXT_LINE, TextLineYml.class );
        register( InputTypeName.DOUBLE, DoubleYml.class );
        register( InputTypeName.CONTENT_SELECTOR, ContentSelectorYml.class );
        register( InputTypeName.CUSTOM_SELECTOR, CustomSelectorYml.class );
        register( InputTypeName.HTML_AREA, HtmlAreaYml.class );
        register( InputTypeName.TEXT_AREA, TextAreaYml.class );
        register( InputTypeName.DATE, DateYml.class );
        register( InputTypeName.DATE_TIME, DateTimeYml.class );
        register( InputTypeName.TIME, TimeYml.class );
        register( InputTypeName.CHECK_BOX, CheckBoxYml.class );
        register( InputTypeName.COMBO_BOX, ComboBoxYml.class );
        register( InputTypeName.ATTACHMENT_UPLOADER, AttachmentUploaderYml.class );
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
