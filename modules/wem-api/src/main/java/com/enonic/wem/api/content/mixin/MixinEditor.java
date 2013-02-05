package com.enonic.wem.api.content.mixin;

public interface MixinEditor
{
    /**
     * @param mixin to be edited
     * @return updated Mixin, null if it has not been updated.
     */
    public Mixin edit( Mixin mixin )
        throws Exception;
}
