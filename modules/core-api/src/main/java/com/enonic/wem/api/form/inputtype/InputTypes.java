package com.enonic.wem.api.form.inputtype;


import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.data.ValueType;
import com.enonic.wem.api.data.ValueTypes;

public final class InputTypes
{
    public static final InputType COLOR = new Color();

    public static final InputType COMBO_BOX = new ComboBox();

    public static final InputType DATE = new Date();

    public static final InputType TIME = new Time();

    public static final InputType DATE_TIME = new DateTime();

    public static final InputType CHECKBOX = new Checkbox();

    public static final InputType DOUBLE = new Double();

    public static final InputType GEO_POINT = new GeoPoint();

    public static final InputType HTML_AREA = new HtmlArea();

    public static final InputType IMAGE_UPLOADER = new ImageUploader();

    public static final InputType IMAGE_SELECTOR = new ImageSelector();

    public static final InputType FILE_UPLOADER = new FileUploader();

    public static final InputType MONEY = new Money();

    public static final InputType PHONE = new Phone();

    public static final InputType CONTENT_SELECTOR = new ContentSelector();

    public static final InputType SINGLE_SELECTOR = new SingleSelector();

    public static final InputType TAG = new Tag();

    public static final InputType TEXT_LINE = new TextLine();

    public static final InputType TEXT_AREA = new TextArea();

    public static final InputType LONG = new Long();

    public static final InputType XML = new Xml();

    public static final InputType PAGE_CONTROLLER = new PageController();

    public static final InputType CONTENT_TYPE_FILTER = new ContentTypeFilter();

    public static final InputType MODULE_CONFIGURATOR = new ModuleConfigurator();

    private static final ImmutableList<InputType> inputTypes = new ImmutableList.Builder<InputType>().
        add( COLOR ).
        add( COMBO_BOX ).
        add( DATE ).
        add( TIME ).
        add( DATE_TIME ).
        add( CHECKBOX ).
        add( DOUBLE ).
        add( GEO_POINT ).
        add( HTML_AREA ).
        add( IMAGE_UPLOADER ).
        add( IMAGE_SELECTOR ).
        add( MONEY ).
        add( PHONE ).
        add( CONTENT_SELECTOR ).
        add( SINGLE_SELECTOR ).
        add( TAG ).
        add( TEXT_AREA ).
        add( TEXT_LINE ).
        add( LONG ).
        add( XML ).
        add( PAGE_CONTROLLER ).
        add( CONTENT_TYPE_FILTER ).
        add( MODULE_CONFIGURATOR ).
        build();

    private static LinkedHashMap<String, InputType> inputTypeByName = new LinkedHashMap<>();

    private static LinkedHashMap<String, InputType> inputTypeBySimpleClassName = new LinkedHashMap<>();

    static
    {
        for ( InputType inputType : inputTypes )
        {
            register( inputType );
        }

        registerDefaultInputType( ValueTypes.LOCAL_DATE, DATE );
        registerDefaultInputType( ValueTypes.LOCAL_TIME, TIME );
        registerDefaultInputType( ValueTypes.BOOLEAN, CHECKBOX );
        registerDefaultInputType( ValueTypes.STRING, TEXT_AREA );
        registerDefaultInputType( ValueTypes.XML, XML );
        registerDefaultInputType( ValueTypes.LONG, LONG );
        registerDefaultInputType( ValueTypes.DOUBLE, DOUBLE );
    }

    private static void register( InputType inputType )
    {
        Object previous = inputTypeByName.put( inputType.getName(), inputType );
        Preconditions.checkState( previous == null, "InputType already registered: " + inputType.getName() );
    }

    private static void registerDefaultInputType( ValueType valueType, InputType inputType )
    {
        Object previousDataType = inputTypeBySimpleClassName.put( valueType.getClass().getSimpleName(), inputType );
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

        return new CustomInputType( simpleClassName );
    }

    public static ImmutableList<InputType> list()
    {
        return inputTypes;
    }
}
