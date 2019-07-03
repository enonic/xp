package com.enonic.xp.layer;

import com.google.common.base.Preconditions;

public class ContentLayer
{
    private final ContentLayerName name;

    private final ContentLayerName parentName;

    private final String displayName;

    private ContentLayer( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "Name cannot be null" );
        Preconditions.checkNotNull( builder.displayName, "Display name cannot be null" );
        name = builder.name;
        parentName = builder.parentName;
        displayName = builder.displayName;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentLayerName name;

        private ContentLayerName parentName;

        private String displayName;

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

        public ContentLayer build()
        {
            return new ContentLayer( this );
        }
    }
}
