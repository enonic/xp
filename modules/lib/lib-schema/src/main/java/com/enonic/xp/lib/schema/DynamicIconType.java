package com.enonic.xp.lib.schema;

/**
 * The kinds of dynamic schema that have an icon: the content schemas, parts and macros.
 * The JS API names the kind by its type string; the handlers route to the matching service method.
 */
enum DynamicIconType
{
    CONTENT_TYPE, FORM_FRAGMENT, MIXIN, PART, MACRO;

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
}
