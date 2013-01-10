package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.form.Mixin;

public interface MixinEditor
{
    /**
     * @param mixin to be edited
     * @return updated Mixin, null if it has not been updated.
     */
    public Mixin edit( Mixin mixin )
        throws Exception;
}
