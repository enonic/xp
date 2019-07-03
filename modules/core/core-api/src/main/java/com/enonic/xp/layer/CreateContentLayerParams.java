package com.enonic.xp.layer;

import com.google.common.base.Preconditions;

public class CreateContentLayerParams
{
    private ContentLayerName name;

    private ContentLayerName parentName;

    private String displayName;

    private CreateContentLayerParams( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        Preconditions.checkNotNull( builder.displayName, "displayName cannot be null" );
        name = builder.name;
        parentName = builder.parentName == null ? ContentLayerName.DEFAULT_LAYER_NAME : builder.parentName;
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

        public CreateContentLayerParams build()
        {
            return new CreateContentLayerParams( this );
        }
    }
}

