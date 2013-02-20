package com.enonic.wem.api.content.schema.type.form.inputtype;


import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;

public final class InputTypes
{
    public static final BaseInputType COLOR = new Color();

    public static final BaseInputType DATE = new Date();

    public static final BaseInputType DECIMAL_NUMBER = new DecimalNumber();

    public static final BaseInputType GEO_LOCATION = new GeoLocation();

    public static final BaseInputType HTML_AREA = new HtmlArea();

    public static final BaseInputType MONEY = new Money();

    public static final BaseInputType PHONE = new Phone();

    public static final BaseInputType SINGLE_SELECTOR = new SingleSelector();

    public static final BaseInputType TAGS = new Tags();

    public static final BaseInputType TEXT_LINE = new TextLine();

    public static final BaseInputType TEXT_AREA = new TextArea();

    public static final BaseInputType WHOLE_NUMBER = new WholeNumber();

    public static final BaseInputType XML = new Xml();

    private static LinkedHashMap<String, BaseInputType> inputTypeByName = new LinkedHashMap<String, BaseInputType>();

    private static LinkedHashMap<Integer, BaseInputType> inputTypeByDataTypeKey = new LinkedHashMap<Integer, BaseInputType>();

    static
    {
        register( COLOR );
        register( DATE );
        register( DECIMAL_NUMBER );
        register( GEO_LOCATION );
        register( HTML_AREA );
        register( MONEY );
        register( PHONE );
        register( SINGLE_SELECTOR );
        register( TAGS );
        register( TEXT_LINE );
        register( TEXT_AREA );
        register( WHOLE_NUMBER );
        register( XML );

        registerDefaultInputType( DataTypes.DATE, DATE );
        registerDefaultInputType( DataTypes.TEXT, TEXT_AREA );
        registerDefaultInputType( DataTypes.XML, XML );
        registerDefaultInputType( DataTypes.WHOLE_NUMBER, WHOLE_NUMBER );
        registerDefaultInputType( DataTypes.DECIMAL_NUMBER, DECIMAL_NUMBER );
    }

    private static void register( BaseInputType inputType )
    {
        Object previous = inputTypeByName.put( inputType.getName(), inputType );
        Preconditions.checkState( previous == null, "InputType already registered: " + inputType.getName() );
    }

    private static void registerDefaultInputType( DataType dataType, BaseInputType inputType )
    {
        Object previousDataType = inputTypeByDataTypeKey.put( dataType.getKey(), inputType );
        Preconditions.checkState( previousDataType == null, "Default InputType already registered for DataType: " + dataType );
    }

    public static int size()
    {
        return inputTypeByName.size();
    }

    public static BaseInputType parse( final String inputTypeName )
    {
        for ( BaseInputType inputType : inputTypeByName.values() )
        {
            if ( inputType.getName().equals( inputTypeName ) )
            {
                return inputType;
            }
        }
        return null;
    }
}
