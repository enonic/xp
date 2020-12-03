package com.enonic.xp.inputtype;

import java.util.Locale;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class InputTypeName
{
    public static final InputTypeName CHECK_BOX = InputTypeName.from( "CheckBox" );

    public static final InputTypeName COMBO_BOX = InputTypeName.from( "ComboBox" );

    public static final InputTypeName CONTENT_SELECTOR = InputTypeName.from( "ContentSelector" );

    public static final InputTypeName CUSTOM_SELECTOR = InputTypeName.from( "CustomSelector" );

    public static final InputTypeName CONTENT_TYPE_FILTER = InputTypeName.from( "ContentTypeFilter" );

    public static final InputTypeName DATE = InputTypeName.from( "Date" );

    public static final InputTypeName DATE_TIME = InputTypeName.from( "DateTime" );

    public static final InputTypeName DOUBLE = InputTypeName.from( "Double" );

    public static final InputTypeName MEDIA_UPLOADER = InputTypeName.from( "MediaUploader" );

    public static final InputTypeName ATTACHMENT_UPLOADER = InputTypeName.from( "AttachmentUploader" );

    public static final InputTypeName GEO_POINT = InputTypeName.from( "GeoPoint" );

    public static final InputTypeName HTML_AREA = InputTypeName.from( "HtmlArea" );

    public static final InputTypeName IMAGE_SELECTOR = InputTypeName.from( "ImageSelector" );

    public static final InputTypeName MEDIA_SELECTOR = InputTypeName.from( "MediaSelector" );

    public static final InputTypeName IMAGE_UPLOADER = InputTypeName.from( "ImageUploader" );

    public static final InputTypeName LONG = InputTypeName.from( "Long" );

    public static final InputTypeName RADIO_BUTTON = InputTypeName.from( "RadioButton" );

    public static final InputTypeName SITE_CONFIGURATOR = InputTypeName.from( "SiteConfigurator" );

    public static final InputTypeName TAG = InputTypeName.from( "Tag" );

    public static final InputTypeName TEXT_AREA = InputTypeName.from( "TextArea" );

    public static final InputTypeName TEXT_LINE = InputTypeName.from( "TextLine" );

    public static final InputTypeName TIME = InputTypeName.from( "Time" );

    private final String name;

    private final String lowercaseName;

    private InputTypeName( final String name )
    {
        Objects.requireNonNull( name, "InputTypeName can't be null" );
        this.name = name;
        this.lowercaseName = name.toLowerCase( Locale.ROOT );
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
        final InputTypeName that = (InputTypeName) o;
        return lowercaseName.equals( that.lowercaseName );
    }

    @Override
    public int hashCode()
    {
        return lowercaseName.hashCode();
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
