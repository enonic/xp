package com.enonic.wem.api.schema.metadata;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.BaseSchema;
import com.enonic.wem.api.schema.Named;

public final class MetadataSchema
    extends BaseSchema<MetadataSchemaName>
    implements Named<MetadataSchemaName>
{
    private final String displayName;

    private final Form form;

    private MetadataSchema( final Builder builder )
    {
        super( builder );
        this.displayName = builder.displayName;
        this.form = builder.form;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Form getForm()
    {
        return form;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder newMetadataSchema()
    {
        return new Builder();
    }

    public static Builder newMetadataSchema( final MetadataSchema metadataSchema )
    {
        return new Builder( metadataSchema );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder, MetadataSchemaName>
    {
        private String displayName;

        private Form form;

        public Builder()
        {
            super();
        }

        public Builder( final MetadataSchema metadataSchema )
        {
            super( metadataSchema );
            this.displayName = metadataSchema.displayName;
            this.form = metadataSchema.form;
        }

        public Builder name( final MetadataSchemaName value )
        {
            super.name( value );
            return this;
        }

        public Builder name( final String value )
        {
            super.name( MetadataSchemaName.from( value ) );
            return this;
        }

        public Builder displayName( String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder form( Form value )
        {
            this.form = value;
            return this;
        }

        public MetadataSchema build()
        {
            return new MetadataSchema( this );
        }
    }


}
