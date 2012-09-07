package com.enonic.wem.core.content.type.formitem.comptype;


import java.util.LinkedHashMap;

import org.elasticsearch.common.base.Preconditions;

import com.enonic.wem.core.content.datatype.DataType;
import com.enonic.wem.core.content.datatype.DataTypes;

public class ComponentTypes
{
    public static final ComponentType COLOR = new Color();

    public static final ComponentType DATE = new Date();

    public static final ComponentType DECIMAL_NUMBER = new DecimalNumber();

    public static final ComponentType DROPDOWN = new Dropdown();

    public static final ComponentType GEO_LOCATION = new GeoLocation();

    public static final ComponentType HTML_AREA = new HtmlArea();

    public static final ComponentType PHONE = new Phone();

    public static final ComponentType RADIO_BUTTONS = new RadioButtons();

    public static final ComponentType TAGS = new Tags();

    public static final ComponentType TEXT_LINE = new TextLine();

    public static final ComponentType TEXT_AREA = new TextArea();

    public static final ComponentType VIRTUAL = new Virtual();

    public static final ComponentType WHOLE_NUMBER = new WholeNumber();

    public static final ComponentType XML = new Xml();

    private static LinkedHashMap<String, ComponentType> componentTypeByName = new LinkedHashMap<String, ComponentType>();

    private static LinkedHashMap<Integer, ComponentType> componentTypeByDataTypeKey = new LinkedHashMap<Integer, ComponentType>();

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
        registerDefaultComponentType( DataTypes.STRING, TEXT_AREA );
        registerDefaultComponentType( DataTypes.XML, XML );
        registerDefaultComponentType( DataTypes.WHOLE_NUMBER, WHOLE_NUMBER );
        registerDefaultComponentType( DataTypes.DECIMAL_NUMBER, DECIMAL_NUMBER );
    }

    private static void register( ComponentType componentType )
    {
        Object previous = componentTypeByName.put( componentType.getName(), componentType );
        Preconditions.checkState( previous == null, "ComponentType already registered: " + componentType.getName() );
    }

    private static void registerDefaultComponentType( DataType dataType, ComponentType componentType )
    {
        Object previousDataType = componentTypeByDataTypeKey.put( dataType.getKey(), componentType );
        Preconditions.checkState( previousDataType == null, "Default ComponentType already registered for DataType: " + dataType );
    }

    public static ComponentType parse( final String componentTypeName )
    {
        for ( ComponentType componentType : componentTypeByName.values() )
        {
            if ( componentType.getName().equals( componentTypeName ) )
            {
                return componentType;
            }
        }
        return null;
    }
}
