package com.enonic.wem.api.content.space.editor;

import com.enonic.wem.api.content.space.Space;

public interface SpaceEditor
{
    /**
     * @param space to be edited
     * @return updated space, null if it has not been updated.
     */
    public Space edit( Space space )
        throws Exception;
}
