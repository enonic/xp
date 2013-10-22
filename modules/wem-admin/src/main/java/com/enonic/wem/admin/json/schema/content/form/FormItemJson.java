package com.enonic.wem.admin.json.schema.content.form;

import com.enonic.wem.api.schema.content.form.FormItem;

@SuppressWarnings("UnusedDeclaration")
public abstract class FormItemJson
{
    private FormItem formItem;

    protected FormItemJson( FormItem formItem )
    {
        this.formItem = formItem;
    }

    public String getName()
    {
        return formItem.getName();
    }

    public String getFormItemType()
    {
        return this.formItem.getClass().getSimpleName();
    }
}
