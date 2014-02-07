package com.enonic.wem.api.content.page;

public interface PageTemplateEditor
{
    /**
     * @param pageTemplate page template to be edited
     * @return updated page template, null if it has not been modified.
     */
    PageTemplate edit( PageTemplate pageTemplate );

}
