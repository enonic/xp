package com.enonic.wem.api.space.editor;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.support.Editor;

public interface SpaceEditor
    extends Editor<Space>
{
    /**
     * @param space to be edited
     * @return updated space, null if it has not been updated.
     */
    public Space edit( Space space );
}
