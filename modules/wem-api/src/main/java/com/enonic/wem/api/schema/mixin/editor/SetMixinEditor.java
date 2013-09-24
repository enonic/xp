package com.enonic.wem.api.schema.mixin.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.api.schema.content.form.FormItems;
import com.enonic.wem.api.schema.mixin.Mixin;

public final class SetMixinEditor
    implements MixinEditor
{
    private final FormItems formItems;

    private final String displayName;

    private final Icon icon;

    private SetMixinEditor( final Builder builder )
    {
        this.formItems = builder.formItems;
        this.displayName = builder.displayName;
        this.icon = builder.icon;
    }

    public static Builder newSetMixinEditor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private FormItems formItems = new FormItems( null );

        private String displayName;

        private Icon icon;

        public Builder formItems( final FormItems value )
        {
            this.formItems = value;
            return this;
        }

        public Builder addFormItem( final FormItem value )
        {
            this.formItems.add( value );
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder icon( final Icon value )
        {
            this.icon = value;
            return this;
        }

        public SetMixinEditor build()
        {
            return new SetMixinEditor( this );
        }

    }

    @Override
    public Mixin edit( final Mixin mixin )
        throws Exception
    {
        final Mixin.Builder builder = Mixin.newMixin( mixin );

        if ( this.displayName != null )
        {
            builder.displayName( this.displayName );
        }

        if ( this.formItems != null )
        {
            builder.formItems( this.formItems );
        }

        if ( this.icon != null )
        {
            builder.icon( this.icon );
        }

        return builder.build();
    }
}
