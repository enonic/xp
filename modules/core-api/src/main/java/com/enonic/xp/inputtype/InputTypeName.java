package com.enonic.xp.inputtype;

import java.util.Objects;

import com.google.common.annotations.Beta;

@Beta
public final class InputTypeName
{
    public final static InputTypeName CHECKBOX = InputTypeName.from( "Checkbox" );

    public final static InputTypeName COMBOBOX = InputTypeName.from( "ComboBox" );

    public final static InputTypeName CONTENT_SELECTOR = InputTypeName.from( "ContentSelector" );

    public final static InputTypeName CONTENT_TYPE_FILTER = InputTypeName.from( "ContentTypeFilter" );

    public final static InputTypeName DATE = InputTypeName.from( "Date" );

    public final static InputTypeName DATE_TIME = InputTypeName.from( "DateTime" );

    public final static InputTypeName DOUBLE = InputTypeName.from( "Double" );

    public final static InputTypeName FILE_UPLOADER = InputTypeName.from( "FileUploader" );

    public final static InputTypeName GEO_POINT = InputTypeName.from( "GeoPoint" );

    public final static InputTypeName HTML_AREA = InputTypeName.from( "HtmlArea" );

    public final static InputTypeName IMAGE_SELECTOR = InputTypeName.from( "ImageSelector" );

    public final static InputTypeName IMAGE_UPLOADER = InputTypeName.from( "ImageUploader" );

    public final static InputTypeName LONG = InputTypeName.from( "Long" );

    public final static InputTypeName PAGE_CONTROLLER = InputTypeName.from( "PageController" );

    public final static InputTypeName RADIO_BUTTON = InputTypeName.from( "RadioButton" );

    public final static InputTypeName SITE_CONFIGURATOR = InputTypeName.from( "SiteConfigurator" );

    public final static InputTypeName TAG = InputTypeName.from( "Tag" );

    public final static InputTypeName TEXT_AREA = InputTypeName.from( "TextArea" );

    public final static InputTypeName TEXT_LINE = InputTypeName.from( "TextLine" );

    public final static InputTypeName TIME = InputTypeName.from( "Time" );

    private final String name;

    private InputTypeName( final String name )
    {
        this.name = name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof InputTypeName ) )
        {
            return false;
        }

        final InputTypeName other = (InputTypeName) o;
        return Objects.equals( this.name, other.name );
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    public static InputTypeName from( final String name )
    {
        return new InputTypeName( name );
    }
}
