package com.enonic.wem.core.content.type.formitem.fieldtype;


import java.util.LinkedHashMap;

import org.elasticsearch.common.base.Preconditions;

import com.enonic.wem.core.content.datatype.DataType;
import com.enonic.wem.core.content.datatype.DataTypes;

public class FieldTypes
{
    public static final FieldType COLOR = new Color();

    public static final FieldType DATE = new Date();

    public static final FieldType DECIMAL_NUMBER = new DecimalNumber();

    public static final FieldType DROPDOWN = new Dropdown();

    public static final FieldType GEO_LOCATION = new GeoLocation();

    public static final FieldType HTML_AREA = new HtmlArea();

    public static final FieldType PHONE = new Phone();

    public static final FieldType RADIO_BUTTONS = new RadioButtons();

    public static final FieldType TAGS = new Tags();

    public static final FieldType TEXT_LINE = new TextLine();

    public static final FieldType TEXT_AREA = new TextArea();

    public static final FieldType VIRTUAL = new Virtual();

    public static final FieldType WHOLE_NUMBER = new WholeNumber();

    public static final FieldType XML = new Xml();

    private static LinkedHashMap<String, FieldType> fieldTypeByName = new LinkedHashMap<String, FieldType>();

    private static LinkedHashMap<Integer, FieldType> fieldTypeByDataTypeKey = new LinkedHashMap<Integer, FieldType>();

    static
    {
        register( COLOR );
        register( DATE );
        register( DROPDOWN );
        register( GEO_LOCATION );
        register( PHONE );
        register( RADIO_BUTTONS );
        register( TAGS );
        register( TEXT_LINE );
        register( TEXT_AREA );
        register( VIRTUAL );
        register( XML );
        register( WHOLE_NUMBER );
        register( DECIMAL_NUMBER );

        registerDefaultFieldType( DataTypes.DATE, DATE );
        registerDefaultFieldType( DataTypes.STRING, TEXT_AREA );
        registerDefaultFieldType( DataTypes.XML, XML );
        registerDefaultFieldType( DataTypes.WHOLE_NUMBER, WHOLE_NUMBER );
        registerDefaultFieldType( DataTypes.DECIMAL_NUMBER, DECIMAL_NUMBER );
    }

    private static void register( FieldType fieldType )
    {
        Object previous = fieldTypeByName.put( fieldType.getName(), fieldType );
        Preconditions.checkState( previous == null, "FieldType already registered: " + fieldType.getName() );
    }

    private static void registerDefaultFieldType( DataType dataType, FieldType fieldType )
    {
        Object previousDataType = fieldTypeByDataTypeKey.put( dataType.getKey(), fieldType );
        Preconditions.checkState( previousDataType == null, "Default FieldType already registered for DataType: " + dataType );
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
