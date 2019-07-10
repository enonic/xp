package com.enonic.xp.layer;

import java.util.Locale;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;

public class UpdateContentLayerParams
{
    private ContentLayerName name;

    private String displayName;

    private String description;

    private Locale language;

    private CreateAttachment icon;

    private UpdateContentLayerParams( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        Preconditions.checkNotNull( builder.displayName, "displayName cannot be null" );
        if ( builder.iconName != null || builder.iconMimeType != null || builder.iconByteSource != null )
        {
            Preconditions.checkNotNull( builder.iconName, "iconName cannot be null" );
            Preconditions.checkNotNull( builder.iconMimeType, "iconMimeType cannot be null" );
            Preconditions.checkNotNull( builder.iconMimeType, "iconMimeType cannot be null" );
        }
        name = builder.name;
        displayName = builder.displayName;
        description = builder.description;
        language = builder.language;

        if ( builder.iconName != null || builder.iconMimeType != null || builder.iconByteSource != null )
        {
            icon = CreateAttachment.create().
                name( builder.iconName ).
                label( "icon" ).
                mimeType( builder.iconMimeType ).
                byteSource( builder.iconByteSource ).
                build();
        }
    }

    public ContentLayerName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public Locale getLanguage()
    {
        return language;
    }

    public CreateAttachment getIcon()
    {
        return icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentLayerName name;

        private String displayName;

        private String description;

        private Locale language;

        private String iconName;

        private String iconMimeType;

        private ByteSource iconByteSource;

        private Builder()
        {
        }

        public Builder name( final ContentLayerName name )
        {
            this.name = name;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder language( final Locale language )
        {
            this.language = language;
            return this;
        }

        public Builder iconName( final String iconName )
        {
            this.iconName = iconName;
            return this;
        }

        public Builder iconMimeType( final String iconMimeType )
        {
            this.iconMimeType = iconMimeType;
            return this;
        }

        public Builder iconByteSource( final ByteSource iconByteSource )
        {
            this.iconByteSource = iconByteSource;
            return this;
        }

        public UpdateContentLayerParams build()
        {
            return new UpdateContentLayerParams( this );
        }
    }
}

