package com.enonic.wem.api.content.mixin;

final class CompositeMixinEditor
    implements MixinEditor
{
    private final MixinEditor[] editors;

    CompositeMixinEditor( final MixinEditor... editors )
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
