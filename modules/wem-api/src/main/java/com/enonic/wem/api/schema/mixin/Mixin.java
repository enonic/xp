package com.enonic.wem.api.schema.mixin;


import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.item.Node;
import com.enonic.wem.api.item.NodePath;
import com.enonic.wem.api.item.NodeTranslatable;
import com.enonic.wem.api.schema.BaseSchema;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKey;

public class Mixin
    extends BaseSchema<QualifiedMixinName>
    implements Schema, NodeTranslatable
{
    private final FormItems formItems;

    private Mixin( final Builder builder )
    {
        super( builder );
        this.formItems = builder.formItems;
    }

    @Override
    public SchemaKey getSchemaKey()
    {
        return SchemaKey.from( getQualifiedName() );
    }

    @Override
    public QualifiedMixinName getQualifiedName()
    {
        return QualifiedMixinName.from( getName() );
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    @Override
    public Node toNode( final NodePath parent )
    {
        final Node.Builder builder = Node.newNode();

        builder.name( this.getName() );
        builder.createdTime( this.getCreatedTime() );
        builder.modifiedTime( this.getModifiedTime() );
        builder.creator( this.getCreator() );
        builder.modifier( this.getModifier() );
        builder.property( "displayName", this.getDisplayName() );
        // TODO: formItems
        builder.icon( this.getIcon() );

        return builder.build();
    }

    public static Builder newMixin()
    {
        return new Builder();
    }

    public static Builder newMixin( final Mixin mixin )
    {
        return new Builder( mixin );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder>
    {
        private FormItems formItems = new FormItems( null );

        public Builder()
        {
            super();
        }

        public Builder( final Mixin mixin )
        {
            super( mixin );
            this.formItems = mixin.formItems;
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

        public Mixin build()
        {
            return new Mixin( this );
        }
    }
}
