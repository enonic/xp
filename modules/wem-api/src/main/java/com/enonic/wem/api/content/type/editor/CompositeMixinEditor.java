package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.form.Mixin;

final class CompositeMixinEditor
    implements MixinEditor
{
    protected final MixinEditor[] editors;

    public CompositeMixinEditor( final MixinEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public Mixin edit( final Mixin mixin )
        throws Exception
    {
        boolean modified = false;
        Mixin mixinEditet = mixin;
        for ( final MixinEditor editor : this.editors )
        {
            final Mixin updatedContent = editor.edit( mixinEditet );
            if ( updatedContent != null )
            {
                mixinEditet = updatedContent;
                modified = true;
            }
        }
        return modified ? mixinEditet : null;
    }
}
