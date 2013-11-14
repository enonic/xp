package com.enonic.wem.api.content.page;

import com.enonic.wem.api.support.Editor;

public interface PageEditor
    extends Editor<Page>
{
    /**
     * @param page to be edited
     * @return updated page, null if it has not been modified.
     */
    public Page edit( Page page );
}
