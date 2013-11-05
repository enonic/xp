package com.enonic.wem.api.content.page;


import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;

public interface ComponentDescriptor
{
    String getDisplayName();

    Form getConfig();

    ModuleResourceKey getControllerResource();
}
