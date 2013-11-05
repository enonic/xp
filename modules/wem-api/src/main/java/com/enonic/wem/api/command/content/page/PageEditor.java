package com.enonic.wem.api.command.content.page;

import com.enonic.wem.api.content.page.Page;

public interface PageEditor
{
    /**
     * @param page to be edited
     * @return updated page, null if it has not been modified.
     */
    public Page edit( Page page )
        throws Exception;
}
