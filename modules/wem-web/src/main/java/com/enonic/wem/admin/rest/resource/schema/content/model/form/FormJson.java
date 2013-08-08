package com.enonic.wem.admin.rest.resource.schema.content.model.form;


import com.enonic.wem.api.schema.content.form.FormItem;

public class FormJson
    extends FormItemJsonArray
{
    public FormJson( final Iterable<FormItem> formItems )
    {
        super( formItems );
    }
}
