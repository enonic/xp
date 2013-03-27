package com.enonic.wem.api.content.schema.mixin.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.schema.content.form.FormItem;
import com.enonic.wem.api.content.schema.mixin.Mixin;

public final class SetMixinEditor
    implements MixinEditor
{
    private final FormItem formItem;

    private final String displayName;

    private final Icon icon;

    private SetMixinEditor( final Builder builder )
    {
        this.formItem = builder.formItem;
        this.displayName = builder.displayName;
        this.icon = builder.icon;
    }

    public static Builder newSetMixinEditor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private FormItem formItem;

        private String displayName;

        private Icon icon;

        public Builder formItem( final FormItem value )
        {
            this.formItem = value;
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

        if ( this.formItem != null )
        {
            builder.formItem( this.formItem );
        }

        if ( this.icon != null )
        {
            builder.icon( this.icon );
        }

        return builder.build();
    }
}
