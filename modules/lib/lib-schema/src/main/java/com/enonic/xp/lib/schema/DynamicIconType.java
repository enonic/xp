package com.enonic.xp.lib.schema;

import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;

/**
 * The kinds of dynamic schema that have an icon: the content schemas, parts and macros.
 * The JS API names the kind by its type string; the handlers route to the matching service method.
 */
enum DynamicIconType
{
    CONTENT_TYPE( DynamicContentSchemaType.CONTENT_TYPE, null ),
    FORM_FRAGMENT( DynamicContentSchemaType.FORM_FRAGMENT, null ),
    MIXIN( DynamicContentSchemaType.MIXIN, null ),
    PART( null, DynamicComponentType.PART ),
    MACRO( null, null );

    private final DynamicContentSchemaType contentSchemaType;

    private final DynamicComponentType componentType;

    DynamicIconType( final DynamicContentSchemaType contentSchemaType, final DynamicComponentType componentType )
    {
        this.contentSchemaType = contentSchemaType;
        this.componentType = componentType;
    }

    static DynamicIconType from( final String type )
    {
        try
        {
            return valueOf( type );
        }
        catch ( IllegalArgumentException | NullPointerException e )
        {
            throw new IllegalArgumentException( "icons are not supported for type: " + type );
        }
    }

    DynamicContentSchemaType contentSchemaType()
    {
        return contentSchemaType;
    }

    DynamicComponentType componentType()
    {
        return componentType;
    }
}
