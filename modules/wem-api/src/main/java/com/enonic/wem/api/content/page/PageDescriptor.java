package com.enonic.wem.api.content.page;


import com.enonic.wem.api.schema.content.form.Form;

public class PageDescriptor
    extends Descriptor
{
    ControllerSetup controller;

    /**
     * Only for display in PageTemplate.
     */
    Form pageTemplateConfig;

    /**
     * Only for display in Page.
     */
    Form config;

    /**
     * Only for display in LiveEdit.
     */
    Form liveEditConfig;

    public ControllerSetup getControllerSetup()
    {
        return controller;
    }
}
