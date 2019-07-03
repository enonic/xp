package com.enonic.xp.layer;

import com.enonic.xp.content.ContentConstants;

public class ContentLayer
{
    public static final String BRANCH_PREFIX_DRAFT = ContentConstants.BRANCH_VALUE_DRAFT + "-";

    public static final String BRANCH_PREFIX_MASTER = ContentConstants.BRANCH_VALUE_MASTER + "-";

    public static final ContentLayer DEFAULT_CONTENT_LAYER = ContentLayer.from( null, null );

    private final ContentLayerName name;

    private final ContentLayerName parentName;

    private ContentLayer( final Builder builder )
    {
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

    public static ContentLayer from( final ContentLayerName name, final ContentLayerName parentName )
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

        public ContentLayer build()
        {
            return new ContentLayer( this );
        }
    }
}
