package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

// TODO: This should be done automatically based on XML -> JSON algorithm.
public final class InputTypeConfigSerializer2
{
    public static ObjectNode toJson( final InputTypeName type, final Element xml )
    {
        if ( xml == null )
        {
            return JsonNodeFactory.instance.objectNode();
        }

        if ( type == InputTypeName.COMBOBOX )
        {
            return toCheckboxJson( xml );
        }

        if ( type == InputTypeName.CONTENT_SELECTOR )
        {
            return toContentSelectorJson( xml );
        }

        if ( type == InputTypeName.DATE )
        {
            return toDateJson( xml );
        }

        if ( type == InputTypeName.DATE_TIME )
        {
            return toDateTimeJson( xml );
        }

        if ( type == InputTypeName.IMAGE_SELECTOR )
        {
            return toImageSelectorJson( xml );
        }

        if ( type == InputTypeName.RADIO_BUTTONS )
        {
            return toRadioButtonsJson( xml );
        }

        return JsonNodeFactory.instance.objectNode();
    }

    private static ObjectNode toCheckboxJson( final Element xml )
    {
        return null;
    }

    private static ObjectNode toContentSelectorJson( final Element xml )
    {
        return null;
    }

    private static ObjectNode toDateJson( final Element xml )
    {
        return null;
    }

    private static ObjectNode toDateTimeJson( final Element xml )
    {
        return null;
    }

    private static ObjectNode toImageSelectorJson( final Element xml )
    {
        return null;
    }

    private static ObjectNode toRadioButtonsJson( final Element xml )
    {
        return null;
    }
}
