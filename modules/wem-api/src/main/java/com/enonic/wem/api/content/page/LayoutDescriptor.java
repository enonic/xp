package com.enonic.wem.api.content.page;

import com.enonic.wem.api.form.Form;

public class LayoutDescriptor
    implements ComponentDescriptor
{
    private String displayName;

    private ControllerSetup controller;

    /**
     * Only for display in LayoutTemplate.
     */
    private Form templateConfig;

    /**
     * Only for display in Layout.
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
