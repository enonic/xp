package com.enonic.xp.layer;

import com.enonic.xp.content.ContentConstants;

public class ContentLayer
{
    public static final String BRANCH_PREFIX_DRAFT = ContentConstants.BRANCH_VALUE_DRAFT + "-";

    public static final String BRANCH_PREFIX_MASTER = ContentConstants.BRANCH_VALUE_MASTER + "-";

    public static final ContentLayer DEFAULT_CONTENT_LAYER = ContentLayer.from( "default", null );

    private final String name;

    private final String parentName;

    private ContentLayer( final Builder builder )
    {
        name = builder.name;
        parentName = builder.parentName;
    }

    public String getName()
    {
        return name;
    }

    public String getParentName()
    {
        return parentName;
    }

    public static ContentLayer from( final String name, final String parentName )
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
        private String name;

        private String parentName;

        private Builder()
        {
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder parentName( final String parentName )
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
