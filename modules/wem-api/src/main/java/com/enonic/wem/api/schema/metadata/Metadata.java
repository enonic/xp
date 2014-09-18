package com.enonic.wem.api.schema.metadata;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.BaseSchema;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKind;

public final class Metadata extends BaseSchema<MetadataName>
    implements Schema
{
    private final String displayName;
    private final Form form;

    private Metadata( final Builder builder )
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

    public static class Builder
        extends BaseSchema.Builder<Builder, MetadataName>
    {
        private String displayName;
        private Form form;

        public Builder()
        {
            super( SchemaKind.METADATA );
        }

        public Builder( final Metadata metadata )
        {
            super( metadata );
            this.displayName = metadata.displayName;
            this.form = metadata.form;
        }

        public Builder name( final MetadataName value )
        {
            super.name( value );
            return this;
        }

        public Builder name( final String value )
        {
            super.name( MetadataName.from( value ) );
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

        public Metadata build()
        {
            return new Metadata( this );
        }
    }


}
