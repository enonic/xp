package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.form.SubType;

public interface SubTypeEditor
{
    /**
     * @param subType to be edited
     * @return updated sub type, null if it has not been updated.
     */
    public SubType edit( SubType subType )
        throws Exception;
}
