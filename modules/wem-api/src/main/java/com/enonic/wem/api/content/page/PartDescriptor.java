package com.enonic.wem.api.content.page;


import com.enonic.wem.api.schema.content.form.Form;

public class PartDescriptor
    implements ComponentDescriptor
{
    private String displayName;

    private ControllerSetup controller;

    /**
     * Only for display in PartTemplate.
     */
    private Form templateConfig;

    /**
     * Only for display in Part.
     */
    private Form config;

    /**
     * Only for display in Live Edit.
     */
    private Form liveEditConfig;

    public String getDisplayName()
    {
        return displayName;
    }
}
