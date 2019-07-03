package com.enonic.xp.layer;

import com.google.common.base.Preconditions;

public class CreateContentLayerParams
{
    private ContentLayerName name;

    private ContentLayerName parentName;

    private CreateContentLayerParams( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name is required for a Layer" );
        name = builder.name;
        parentName = builder.parentName;
    }

    public ContentLayerName getName()
    {
        return name;
    }

    public ContentLayerName getParentName()
    {
        return parentName;
    }

    public static CreateContentLayerParams from( final ContentLayerName name, final ContentLayerName parentName )
    {
        return create().
            name( name ).
            parentName( parentName ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentLayerName name;

        private ContentLayerName parentName;

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

        public CreateContentLayerParams build()
        {
            return new CreateContentLayerParams( this );
        }
    }
}

