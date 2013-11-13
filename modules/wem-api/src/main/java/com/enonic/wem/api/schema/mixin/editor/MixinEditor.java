package com.enonic.wem.api.schema.mixin.editor;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.support.Editor;

public interface MixinEditor
    extends Editor<Mixin>
{
    /**
     * @param mixin to be edited
     * @return updated Mixin, null if it has not been updated.
     */
    public Mixin edit( Mixin mixin );
}
