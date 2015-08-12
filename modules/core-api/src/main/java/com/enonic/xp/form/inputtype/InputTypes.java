package com.enonic.xp.form.inputtype;

import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

final class InputTypes
{
    public static final InputType COMBO_BOX = ComboBoxType.INSTANCE;

    public static final InputType DATE = DateType.INSTANCE;

    public static final InputType TIME = TimeType.INSTANCE;

    public static final InputType DATE_TIME = DateTimeType.INSTANCE;

    public static final InputType CHECKBOX = CheckboxType.INSTANCE;

    public static final InputType DOUBLE = DoubleType.INSTANCE;

    public static final InputType GEO_POINT = GeoPointType.INSTANCE;

    public static final InputType HTML_AREA = new HtmlAreaType();

    public static final InputType TINY_MCE = new TinyMCEType();

    public static final InputType IMAGE_UPLOADER = ImageUploaderType.INSTANCE;

    public static final InputType IMAGE_SELECTOR = ImageSelectorType.INSTANCE;

    public static final InputType FILE_UPLOADER = FileUploaderType.INSTANCE;

    public static final InputType CONTENT_SELECTOR = ContentSelectorType.INSTANCE;

    public static final InputType RADIO_BUTTONS = RadioButtonsType.INSTANCE;

    public static final InputType TAG = TagType.INSTANCE;

    public static final InputType TEXT_LINE = TextLineType.INSTANCE;

    public static final InputType TEXT_AREA = TextAreaType.INSTANCE;

    public static final InputType LONG = LongType.INSTANCE;

    public static final InputType PAGE_CONTROLLER = PageControllerType.INSTANCE;

    public static final InputType CONTENT_TYPE_FILTER = ContentTypeFilterType.INSTANCE;

    public static final InputType SITE_CONFIGURATOR = SiteConfiguratorType.INSTANCE;

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
        add( FILE_UPLOADER ).
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

    static
    {
        INPUT_TYPES.forEach( com.enonic.xp.form.inputtype.InputTypes::register );
    }

    private static void register( InputType inputType )
    {
        Object previous = INPUT_TYPE_BY_NAME.put( inputType.getName(), inputType );
        Preconditions.checkState( previous == null, "InputType already registered: " + inputType.getName() );
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
}
