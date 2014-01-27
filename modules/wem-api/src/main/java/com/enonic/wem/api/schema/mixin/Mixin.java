package com.enonic.wem.api.schema.mixin;

import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.schema.BaseSchema;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.SchemaKey;

public class Mixin
    extends BaseSchema<MixinName>
    implements Schema
{
    private final FormItems formItems;

    private final SchemaIcon schemaIcon;

    private Mixin( final Builder builder )
    {
        super( builder );
        this.formItems = builder.formItems;
        this.schemaIcon = builder.schemaIcon;
    }

    @Override
    public SchemaKey getSchemaKey()
    {
        return getName() != null ? SchemaKey.from( getName() ) : null;
    }

    @Override
    public boolean hasChildren()
    {
        return false;
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    public static Builder newMixin()
    {
        return new Builder();
    }

    public SchemaIcon getSchemaIcon()
    {
        return schemaIcon;
    }

    public static Builder newMixin( final Mixin mixin )
    {
        return new Builder( mixin );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder, MixinName>
    {
        private FormItems formItems = new FormItems();

        private SchemaIcon schemaIcon;

        public Builder()
        {
            super();
        }

        public Builder( final Mixin mixin )
        {
            super( mixin );
            this.formItems = mixin.formItems;
            this.schemaIcon = mixin.getSchemaIcon();
        }

        public Builder name( final MixinName value )
        {
            super.name( value );
            return this;
        }

        public Builder name( final String value )
        {
            super.name( MixinName.from( value ) );
            return this;
        }

        public Builder formItems( FormItems value )
        {
            this.formItems = value;
            return this;
        }

        public Builder addFormItem( FormItem value )
        {
            this.formItems.add( value );
            return this;
        }

        public Builder schemaIcon( SchemaIcon schemaIcon )
        {
            this.schemaIcon = schemaIcon;
            return this;
        }

        public Mixin build()
        {
            return new Mixin( this );
        }
    }
}
