package com.enonic.wem.admin.json.form;

import com.enonic.wem.api.form.FieldSet;

@SuppressWarnings("UnusedDeclaration")
public class FieldSetJson
    extends LayoutJson
{
    private final FieldSet fieldSet;

    private FormItemJsonArray items;

    public FieldSetJson( final FieldSet fieldSet )
    {
        super( fieldSet );
        this.fieldSet = fieldSet;
        this.items = new FormItemJsonArray( fieldSet.getFormItems() );
    }

    public String getLabel()
    {
        return fieldSet.getLabel();
    }

    public FormItemJsonArray getItems()
    {
        return items;
    }

}
