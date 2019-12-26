package com.enonic.xp.media;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FormItemName;
import com.enonic.xp.schema.xdata.XDataName;

@PublicApi
public final class MediaInfo
{
    private final String mediaType;

    private final ImmutableMultimap<String, String> metadata;

    private final String textContent;

    public static final String CAMERA_INFO = "cameraInfo";

    public static final String GPS_INFO = "gpsInfo";

    public static final String IMAGE_INFO = "imageInfo";

    public static final XDataName IMAGE_INFO_METADATA_NAME = XDataName.from( ApplicationKey.MEDIA_MOD, IMAGE_INFO );

    public static final XDataName CAMERA_INFO_METADATA_NAME = XDataName.from( ApplicationKey.MEDIA_MOD, CAMERA_INFO );

    public static final XDataName GPS_INFO_METADATA_NAME = XDataName.from( ApplicationKey.BASE, GPS_INFO );

    public static final String GPS_INFO_GEO_POINT = "geoPoint";

    public static final String IMAGE_INFO_PIXEL_SIZE = "pixelSize";

    public static final String IMAGE_INFO_IMAGE_HEIGHT = "imageHeight";

    public static final String IMAGE_INFO_IMAGE_WIDTH = "imageWidth";

    public static final String MEDIA_INFO_BYTE_SIZE = "byteSize";

    private MediaInfo( final Builder builder )
    {
        this.mediaType = builder.mediaType;
        this.metadata = builder.metadata.build();
        this.textContent = builder.textContent;
        Preconditions.checkNotNull( this.metadata, "xData cannot be null" );
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public String getTextContent()
    {
        return textContent;
    }

    public ImmutableMultimap<String, String> getMetadata()
    {
        return metadata;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String mediaType;

        private final ImmutableMultimap.Builder<String, String> metadata = ImmutableMultimap.builder();

        private String textContent;

        public Builder mediaType( final String value )
        {
            this.mediaType = value;
            return this;
        }

        public Builder addMetadata( final String name, final String value )
        {
            this.metadata.put( FormItemName.safeName( name ), value );
            return this;
        }


        public Builder setTextContent( final String textContent )
        {
            this.textContent = textContent;
            return this;
        }

        public MediaInfo build()
        {
            return new MediaInfo( this );
        }
    }
}
