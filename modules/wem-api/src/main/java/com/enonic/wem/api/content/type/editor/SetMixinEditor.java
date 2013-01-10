package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.form.FormItemSetMixin;
import com.enonic.wem.api.content.type.form.InputMixin;
import com.enonic.wem.api.content.type.form.Mixin;

final class SetMixinEditor
    implements MixinEditor
{
    protected final Mixin source;

    public SetMixinEditor( final Mixin source )
    {
        this.source = source;
    }

    @Override
    public Mixin edit( final Mixin mixin )
        throws Exception
    {
        if ( mixin instanceof InputMixin )
        {
            final InputMixin inputMixin = (InputMixin) mixin;
            return InputMixin.newInputMixin( inputMixin ).build();
        }
        else if ( mixin instanceof FormItemSetMixin )
        {
            FormItemSetMixin formItemSetMixin = (FormItemSetMixin) mixin;
            return FormItemSetMixin.newFormItemSetMixin( formItemSetMixin ).build();
        }
        else
        {
            throw new IllegalArgumentException( "Type of Mixin not supported: " + mixin.getClass().getSimpleName() );
        }
    }
}
