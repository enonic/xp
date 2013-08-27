package com.enonic.wem.api.schema.content.form.inputtype;


import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;

public final class InputTypes
{
    public static final Color COLOR = new Color();

    public static final Date DATE = new Date();

    public static final DecimalNumber DECIMAL_NUMBER = new DecimalNumber();

    public static final GeoLocation GEO_LOCATION = new GeoLocation();

    public static final HtmlArea HTML_AREA = new HtmlArea();

    public static final Image IMAGE = new Image();

    public static final ImageSelector IMAGE_SELECTOR = new ImageSelector();

    public static final Money MONEY = new Money();

    public static final Phone PHONE = new Phone();

    public static final Relationship RELATIONSHIP = new Relationship();

    public static final SingleSelector SINGLE_SELECTOR = new SingleSelector();

    public static final Tags TAGS = new Tags();

    public static final TextLine TEXT_LINE = new TextLine();

    public static final TextArea TEXT_AREA = new TextArea();

    public static final WholeNumber WHOLE_NUMBER = new WholeNumber();

    public static final Xml XML = new Xml();

    private static LinkedHashMap<String, BaseInputType> inputTypeByName = new LinkedHashMap<String, BaseInputType>();

    private static LinkedHashMap<Integer, BaseInputType> inputTypeByDataTypeKey = new LinkedHashMap<Integer, BaseInputType>();

    static
    {
        register( COLOR );
        register( DATE );
        register( DECIMAL_NUMBER );
        register( GEO_LOCATION );
        register( HTML_AREA );
        register( IMAGE );
        register( IMAGE_SELECTOR );
        register( MONEY );
        register( PHONE );
        register( RELATIONSHIP );
        register( SINGLE_SELECTOR );
        register( TAGS );
        register( TEXT_LINE );
        register( TEXT_AREA );
        register( WHOLE_NUMBER );
        register( XML );

        registerDefaultInputType( ValueTypes.DATE_MIDNIGHT, DATE );
        registerDefaultInputType( ValueTypes.TEXT, TEXT_AREA );
        registerDefaultInputType( ValueTypes.XML, XML );
        registerDefaultInputType( ValueTypes.WHOLE_NUMBER, WHOLE_NUMBER );
        registerDefaultInputType( ValueTypes.DECIMAL_NUMBER, DECIMAL_NUMBER );
    }

    private static void register( BaseInputType inputType )
    {
        Object previous = inputTypeByName.put( inputType.getName(), inputType );
        Preconditions.checkState( previous == null, "InputType already registered: " + inputType.getName() );
    }

    private static void registerDefaultInputType( ValueType valueType, BaseInputType inputType )
    {
        Object previousDataType = inputTypeByDataTypeKey.put( valueType.getKey(), inputType );
        Preconditions.checkState( previousDataType == null, "Default InputType already registered for ValueType: " + valueType );
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
