package com.enonic.xp.form.inputtype;

import java.util.LinkedHashMap;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;

@Beta
public final class InputTypes
{
    public static final InputType COMBO_BOX = ComboBoxType.INSTANCE;

    public static final InputType DATE = DateType.INSTANCE;

    public static final InputType TIME = new TimeType();

    public static final InputType DATE_TIME = DateTimeType.INSTANCE;

    public static final InputType CHECKBOX = CheckboxType.INSTANCE;

    public static final InputType DOUBLE = new DoubleType();

    public static final InputType GEO_POINT = new GeoPointType();

    public static final InputType HTML_AREA = new HtmlAreaType();

    public static final InputType TINY_MCE = new TinyMCEType();

    public static final InputType IMAGE_UPLOADER = new ImageUploaderType();

    public static final InputType IMAGE_SELECTOR = new ImageSelectorType();

    public static final InputType FILE_UPLOADER = new FileUploaderType();

    public static final InputType CONTENT_SELECTOR = ContentSelectorType.INSTANCE;

    public static final InputType RADIO_BUTTONS = RadioButtonsType.INSTANCE;

    public static final InputType TAG = new TagType();

    public static final InputType TEXT_LINE = new TextLineType();

    public static final InputType TEXT_AREA = new TextAreaType();

    public static final InputType LONG = new LongType();

    public static final InputType PAGE_CONTROLLER = new PageControllerType();

    public static final InputType CONTENT_TYPE_FILTER = ContentTypeFilterType.INSTANCE;

    public static final InputType SITE_CONFIGURATOR = new SiteConfiguratorType();

    private static final ImmutableList<InputType> INPUT_TYPES = new ImmutableList.Builder<InputType>().
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
        add( CONTENT_SELECTOR ).
        add( RADIO_BUTTONS ).
        add( TAG ).
        add( TEXT_AREA ).
        add( TEXT_LINE ).
        add( LONG ).
        add( PAGE_CONTROLLER ).
        add( CONTENT_TYPE_FILTER ).
        add( SITE_CONFIGURATOR ).
        add( TINY_MCE ).
        build();

    private static final LinkedHashMap<String, InputType> INPUT_TYPE_BY_NAME = new LinkedHashMap<>();

    private static final LinkedHashMap<String, InputType> INPUT_TYPE_BY_SIMPLE_CLASS_NAME = new LinkedHashMap<>();

    static
    {
        INPUT_TYPES.forEach( com.enonic.xp.form.inputtype.InputTypes::register );

        registerDefaultInputType( ValueTypes.LOCAL_DATE, DATE );
        registerDefaultInputType( ValueTypes.LOCAL_TIME, TIME );
        registerDefaultInputType( ValueTypes.BOOLEAN, CHECKBOX );
        registerDefaultInputType( ValueTypes.STRING, TEXT_AREA );
        registerDefaultInputType( ValueTypes.LONG, LONG );
        registerDefaultInputType( ValueTypes.DOUBLE, DOUBLE );
    }

    private static void register( InputType inputType )
    {
        Object previous = INPUT_TYPE_BY_NAME.put( inputType.getName(), inputType );
        Preconditions.checkState( previous == null, "InputType already registered: " + inputType.getName() );
    }

    private static void registerDefaultInputType( ValueType valueType, InputType inputType )
    {
        Object previousDataType = INPUT_TYPE_BY_SIMPLE_CLASS_NAME.put( valueType.getClass().getSimpleName(), inputType );
        Preconditions.checkState( previousDataType == null, "Default InputType already registered for ValueType: " + valueType );
    }

    public static int size()
    {
        return INPUT_TYPE_BY_NAME.size();
    }

    public static InputType find( final String name )
    {
        for ( final InputType inputType : INPUT_TYPE_BY_NAME.values() )
        {
            if ( inputType.getName().equals( name ) )
            {
                return inputType;
            }
        }

        return null;
    }

    public static ImmutableList<InputType> list()
    {
        return INPUT_TYPES;
    }
}
