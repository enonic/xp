package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class InputTypeName
{
    public final static InputTypeName CHECK_BOX = InputTypeName.from( "CheckBox" );

    public final static InputTypeName COMBO_BOX = InputTypeName.from( "ComboBox" );

    public final static InputTypeName CONTENT_SELECTOR = InputTypeName.from( "ContentSelector" );

    public final static InputTypeName CUSTOM_SELECTOR = InputTypeName.from( "CustomSelector" );

    public final static InputTypeName CONTENT_TYPE_FILTER = InputTypeName.from( "ContentTypeFilter" );

    public final static InputTypeName DATE = InputTypeName.from( "Date" );

    public final static InputTypeName DATE_TIME = InputTypeName.from( "DateTime" );

    public final static InputTypeName DOUBLE = InputTypeName.from( "Double" );

    public final static InputTypeName MEDIA_UPLOADER = InputTypeName.from( "MediaUploader" );

    public final static InputTypeName ATTACHMENT_UPLOADER = InputTypeName.from( "AttachmentUploader" );

    public final static InputTypeName GEO_POINT = InputTypeName.from( "GeoPoint" );

    public final static InputTypeName HTML_AREA = InputTypeName.from( "HtmlArea" );

    public final static InputTypeName IMAGE_SELECTOR = InputTypeName.from( "ImageSelector" );

    public final static InputTypeName MEDIA_SELECTOR = InputTypeName.from( "MediaSelector" );

    public final static InputTypeName IMAGE_UPLOADER = InputTypeName.from( "ImageUploader" );

    public final static InputTypeName LONG = InputTypeName.from( "Long" );

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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final InputTypeName that = (InputTypeName) o;

        return name != null ? name.toLowerCase().equals( that.name != null ? that.name.toLowerCase() : null ) : that.name == null;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
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
