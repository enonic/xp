package com.enonic.wem.core.content.type.configitem.fieldtype;


import java.util.LinkedHashMap;

public class FieldTypes
{
    public static final FieldType DATE = new Date();

    public static final FieldType DROPDOWN = new Dropdown();

    public static final FieldType HTML_AREA = new HtmlArea();

    public static final FieldType PHONE = new Phone();

    public static final FieldType RADIO_BUTTONS = new RadioButtons();

    public static final FieldType TAGS = new Tags();

    public static final FieldType TEXT_LINE = new TextLine();

    public static final FieldType TEXT_AREA = new TextArea();

    public static final FieldType VIRTUAL = new Virtual();

    public static final FieldType XML = new Xml();

    private static LinkedHashMap<String, FieldType> fieldTypeByName = new LinkedHashMap<String, FieldType>();

    static
    {
        fieldTypeByName.put( DATE.getName(), DATE );
        fieldTypeByName.put( DROPDOWN.getName(), DROPDOWN );
        fieldTypeByName.put( PHONE.getName(), PHONE );
        fieldTypeByName.put( RADIO_BUTTONS.getName(), RADIO_BUTTONS );
        fieldTypeByName.put( TAGS.getName(), TAGS );
        fieldTypeByName.put( TEXT_LINE.getName(), TEXT_LINE );
        fieldTypeByName.put( TEXT_AREA.getName(), TEXT_AREA );
        fieldTypeByName.put( XML.getName(), XML );
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
