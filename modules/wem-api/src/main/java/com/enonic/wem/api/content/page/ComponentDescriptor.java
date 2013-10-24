package com.enonic.wem.api.content.page;


import com.enonic.wem.api.form.Form;

public interface ComponentDescriptor
{
    String getDisplayName();

    Form getConfig();
}
