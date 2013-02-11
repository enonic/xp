package com.enonic.wem.api.content.mixin;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.type.form.FormItem;

final class SetMixinEditor
    implements MixinEditor
{
    private final FormItem formItem;

    private final String displayName;

    private final Icon icon;

    SetMixinEditor( final String displayName, final FormItem formItem, final Icon icon )
    {
        this.formItem = formItem;
        this.displayName = displayName;
        this.icon = icon;
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
            builder.icon( Icon.copyOf( this.icon ) );
        }

        return builder.build();
    }
}
