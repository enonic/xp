package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public final class PartComponent
    extends PageComponent<PartTemplateKey>
    implements RegionPlaceableComponent
{
    public PartComponent( final Builder builder )
    {
        super( builder );
    }

    public static Builder newPartComponent()
    {
        return new Builder();
    }

    public static class Builder
        extends PageComponent.Builder<PartTemplateKey>
    {
        private Builder()
        {
        }

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = new ComponentName( value );
            return this;
        }

        public Builder template( PartTemplateKey value )
        {
            this.template = value;
            return this;
        }

        public Builder template( String value )
        {
            this.template = PartTemplateKey.from( value );
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public PartComponent build()
        {
            return new PartComponent( this );
        }
    }
}
