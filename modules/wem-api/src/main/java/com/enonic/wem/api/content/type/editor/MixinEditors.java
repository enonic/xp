package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.form.Mixin;

public abstract class MixinEditors
{
    public static MixinEditor composite( final MixinEditor... editors )
    {
        return new CompositeMixinEditor( editors );
    }

    public static MixinEditor setMixin( final Mixin mixin )
    {
        return new SetMixinEditor( mixin );
    }
}
