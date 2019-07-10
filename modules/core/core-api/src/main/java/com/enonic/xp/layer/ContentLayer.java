package com.enonic.xp.layer;

import java.util.Locale;

import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.Attachment;

public class ContentLayer
{
    private final ContentLayerName name;

    private final ContentLayerName parentName;

    private final String displayName;

    private final String description;

    private final Locale language;

    private final Attachment icon;

    private ContentLayer( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "Name cannot be null" );
        Preconditions.checkNotNull( builder.displayName, "Display name cannot be null" );
        name = builder.name;
        parentName = builder.parentName;
        displayName = builder.displayName;
        description = builder.description;
        language = builder.language;
        icon = builder.icon;
    }

    public ContentLayerName getName()
    {
        return name;
    }

    public ContentLayerName getParentName()
    {
        return parentName;
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

    public Attachment getIcon()
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

        private ContentLayerName parentName;

        private String displayName;

        private String description;

        private Locale language;

        private Attachment icon;

        private Builder()
        {
        }

        public Builder name( final ContentLayerName name )
        {
            this.name = name;
            return this;
        }

        public Builder parentName( final ContentLayerName parentName )
        {
            this.parentName = parentName;
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

        public Builder icon( final Attachment icon )
        {
            this.icon = icon;
            return this;
        }

        public ContentLayer build()
        {
            return new ContentLayer( this );
        }
    }
}
