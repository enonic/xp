package com.enonic.wem.api.schema.content.form.inputtype;


import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;

public final class InputTypes
{
    public static final InputType COLOR = new Color();

    public static final InputType COMBO_BOX = new ComboBox();

    public static final InputType DATE = new Date();

    public static final InputType DECIMAL_NUMBER = new DecimalNumber();

    public static final InputType GEO_LOCATION = new GeoLocation();

    public static final InputType HTML_AREA = new HtmlArea();

    public static final InputType IMAGE = new Image();

    public static final InputType IMAGE_SELECTOR = new ImageSelector();

    public static final InputType MONEY = new Money();

    public static final InputType PHONE = new Phone();

    public static final InputType RELATIONSHIP = new Relationship();

    public static final InputType SINGLE_SELECTOR = new SingleSelector();

    public static final InputType TAGS = new Tags();

    public static final InputType TEXT_LINE = new TextLine();

    public static final InputType TEXT_AREA = new TextArea();

    public static final InputType WHOLE_NUMBER = new WholeNumber();

    public static final InputType XML = new Xml();

    private static final ImmutableList<InputType> inputTypes = new ImmutableList.Builder<InputType>().
        add( COLOR ).
        add( COMBO_BOX ).
        add( DATE ).
        add( DECIMAL_NUMBER ).
        add( GEO_LOCATION ).
        add( HTML_AREA ).
        add( IMAGE ).
        add( IMAGE_SELECTOR ).
        add( MONEY ).
        add( PHONE ).
        add( RELATIONSHIP ).
        add( SINGLE_SELECTOR ).
        add( TAGS ).
        add( TEXT_AREA ).
        add( TEXT_LINE ).
        add( WHOLE_NUMBER ).
        add( XML ).
        build();

    private static LinkedHashMap<String, InputType> inputTypeByName = new LinkedHashMap<>();

    private static LinkedHashMap<Integer, InputType> inputTypeByDataTypeKey = new LinkedHashMap<>();

    static
    {
        for ( InputType inputType : inputTypes )
        {
            register( inputType );
        }

        registerDefaultInputType( ValueTypes.DATE_MIDNIGHT, DATE );
        registerDefaultInputType( ValueTypes.STRING, TEXT_AREA );
        registerDefaultInputType( ValueTypes.XML, XML );
        registerDefaultInputType( ValueTypes.LONG, WHOLE_NUMBER );
        registerDefaultInputType( ValueTypes.DOUBLE, DECIMAL_NUMBER );
    }

    private static void register( InputType inputType )
    {
        Object previous = inputTypeByName.put( inputType.getName(), inputType );
        Preconditions.checkState( previous == null, "InputType already registered: " + inputType.getName() );
    }

    private static void registerDefaultInputType( ValueType valueType, InputType inputType )
    {
        Object previousDataType = inputTypeByDataTypeKey.put( valueType.getKey(), inputType );
        Preconditions.checkState( previousDataType == null, "Default InputType already registered for ValueType: " + valueType );
    }

    public static int size()
    {
        return inputTypeByName.size();
    }

    public static InputType parse( final String simpleClassName )
    {
        for ( InputType inputType : inputTypeByName.values() )
        {
            if ( inputType.getClass().getSimpleName().equals( simpleClassName ) )
            {
                return inputType;
            }
        }
        return null;
    }

    public static ImmutableList<InputType> list()
    {
        return inputTypes;
    }
}
