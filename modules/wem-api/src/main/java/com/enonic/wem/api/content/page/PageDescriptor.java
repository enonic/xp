package com.enonic.wem.api.content.page;


import com.enonic.wem.api.Path;
import com.enonic.wem.api.schema.content.form.Form;

public class PageDescriptor
{
    String displayName;

    Path controller;

    ControllerParams controllerParams;

    Form liveEdit;

    Form config;

    Form page;
}
