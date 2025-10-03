package com.enonic.xp.site;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.Content;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

@PublicApi
public final class Site
    extends Content
{
    private Site( final Builder builder )
    {
        super( builder );
    }

    public String getDescription()
    {
        return this.getData().getString( "description" );
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

        return super.equals( o );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Site source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends Content.Builder<Builder>
    {
        public Builder( final Site source )
        {
            super( source );
        }

        public Builder()
        {
            super();
            type = ContentTypeName.site();
        }

        public Builder description( final String description )
        {

            if ( data == null )
            {
                data = new PropertyTree();
            }
            data.setString( "description", description );
            return this;
        }

        @Override
        public Site build()
        {
            return new Site( this );
        }

    }
}
