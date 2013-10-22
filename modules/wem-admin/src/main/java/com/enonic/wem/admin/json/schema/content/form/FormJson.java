package com.enonic.wem.admin.json.schema.content.form;


import com.enonic.wem.api.schema.content.form.FormItem;

@SuppressWarnings("UnusedDeclaration")
public class FormJson
    extends FormItemJsonArray
{
    public FormJson( final Iterable<FormItem> formItems )
    {
        super( formItems );
    }
}
