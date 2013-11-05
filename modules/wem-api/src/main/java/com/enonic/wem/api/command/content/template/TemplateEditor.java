package com.enonic.wem.api.command.content.template;

import com.enonic.wem.api.content.page.Template;

public interface TemplateEditor
{
    /**
     * @param template to be edited
     * @return updated template, null if it has not been modified.
     */
    public Template edit( Template template )
        throws Exception;
}
