package com.enonic.wem.api.content.type.formitem.comptype;


import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;

public final class ComponentTypes
{
    public static final BaseComponentType COLOR = new Color();

    public static final BaseComponentType DATE = new Date();

    public static final BaseComponentType DECIMAL_NUMBER = new DecimalNumber();

    public static final BaseComponentType DROPDOWN = new Dropdown();

    public static final BaseComponentType GEO_LOCATION = new GeoLocation();

    public static final BaseComponentType HTML_AREA = new HtmlArea();

    public static final BaseComponentType PHONE = new Phone();

    public static final BaseComponentType RADIO_BUTTONS = new RadioButtons();

    public static final BaseComponentType TAGS = new Tags();

    public static final BaseComponentType TEXT_LINE = new TextLine();

    public static final BaseComponentType TEXT_AREA = new TextArea();

    public static final BaseComponentType VIRTUAL = new Virtual();

    public static final BaseComponentType WHOLE_NUMBER = new WholeNumber();

    public static final BaseComponentType XML = new Xml();

    private static LinkedHashMap<String, BaseComponentType> componentTypeByName = new LinkedHashMap<String, BaseComponentType>();

    private static LinkedHashMap<Integer, BaseComponentType> componentTypeByDataTypeKey = new LinkedHashMap<Integer, BaseComponentType>();

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

        registerDefaultComponentType( DataTypes.DATE, DATE );
        registerDefaultComponentType( DataTypes.TEXT, TEXT_AREA );
        registerDefaultComponentType( DataTypes.XML, XML );
        registerDefaultComponentType( DataTypes.WHOLE_NUMBER, WHOLE_NUMBER );
        registerDefaultComponentType( DataTypes.DECIMAL_NUMBER, DECIMAL_NUMBER );
    }

    private static void register( BaseComponentType baseComponentType )
    {
        Object previous = componentTypeByName.put( baseComponentType.getName(), baseComponentType );
        Preconditions.checkState( previous == null, "ComponentType already registered: " + baseComponentType.getName() );
    }

    private static void registerDefaultComponentType( DataType dataType, BaseComponentType componentType )
    {
        Object previousDataType = componentTypeByDataTypeKey.put( dataType.getKey(), componentType );
        Preconditions.checkState( previousDataType == null, "Default ComponentType already registered for DataType: " + dataType );
    }

    public static BaseComponentType parse( final String componentTypeName )
    {
        for ( BaseComponentType componentType : componentTypeByName.values() )
        {
            if ( componentType.getName().equals( componentTypeName ) )
            {
                return componentType;
            }
        }
        return null;
    }
}
