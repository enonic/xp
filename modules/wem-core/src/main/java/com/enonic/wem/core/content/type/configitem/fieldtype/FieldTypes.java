package com.enonic.wem.core.content.type.configitem.fieldtype;


import java.util.LinkedHashMap;

public class FieldTypes
{
    public static final FieldType date = new Date();

    public static final FieldType dropdown = new Dropdown();

    public static final FieldType phone = new Phone();

    public static final FieldType radioButtons = new RadioButtons();

    public static final FieldType tags = new Tags();

    public static final FieldType textline = new TextLine();

    public static final FieldType textarea = new TextArea();

    public static final FieldType xml = new Xml();

    private static LinkedHashMap<String, FieldType> fieldTypeByName = new LinkedHashMap<String, FieldType>();

    static
    {
        fieldTypeByName.put( date.getName(), date );
        fieldTypeByName.put( dropdown.getName(), dropdown );
        fieldTypeByName.put( phone.getName(), phone );
        fieldTypeByName.put( radioButtons.getName(), radioButtons );
        fieldTypeByName.put( tags.getName(), tags );
        fieldTypeByName.put( textline.getName(), textline );
        fieldTypeByName.put( textarea.getName(), textarea );
        fieldTypeByName.put( xml.getName(), xml );
    }

    public static FieldType parse( final String fieldTypeName )
    {
        for ( FieldType fieldType : fieldTypeByName.values() )
        {
            if ( fieldType.getName().equals( fieldTypeName ) )
            {
                return fieldType;
            }
        }
        return null;
    }
}
