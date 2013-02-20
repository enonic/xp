package com.enonic.wem.api.content.schema.mixin.editor;

import com.enonic.wem.api.content.schema.mixin.Mixin;

public interface MixinEditor
{
    /**
     * @param mixin to be edited
     * @return updated Mixin, null if it has not been updated.
     */
    public Mixin edit( Mixin mixin )
        throws Exception;
}
