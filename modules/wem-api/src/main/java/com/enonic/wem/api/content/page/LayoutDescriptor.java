package com.enonic.wem.api.content.page;

import com.enonic.wem.api.schema.content.form.Form;

public class LayoutDescriptor
    extends Descriptor
{
    ControllerSetup controller;

    /**
     * Only for display in LayoutTemplate.
     */
    Form templateConfig;

    /**
     * Only for display in Layout.
     */
    Form config;

    /**
     * Only for display in Live Edit.
     */
    Form liveEditConfig;
}
