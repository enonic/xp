package com.enonic.wem.api.command.content.page;

import com.enonic.wem.api.content.page.Template;

public interface TemplateEditor<T extends Template>
{
    /**
     * @param template to be edited
     * @return updated template, null if it has not been modified.
     */
    public T edit( T template )
        throws Exception;
}
