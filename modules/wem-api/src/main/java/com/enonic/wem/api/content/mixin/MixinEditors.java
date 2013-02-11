package com.enonic.wem.api.content.mixin;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.type.form.FormItem;

public abstract class MixinEditors
{
    public static MixinEditor composite( final MixinEditor... editors )
    {
        return new CompositeMixinEditor( editors );
    }

    public static MixinEditor setMixin( final String displayName, final FormItem formItem, final Icon icon )
    {
        return new SetMixinEditor( displayName, formItem, icon );
    }
}
