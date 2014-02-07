package com.enonic.wem.api.content.site;


public interface SiteTemplateEditor
{

    /**
     * @param siteTemplate site template to be edited
     * @return updated site template, null if it has not been modified.
     */
    public SiteTemplate edit( SiteTemplate siteTemplate );

}
