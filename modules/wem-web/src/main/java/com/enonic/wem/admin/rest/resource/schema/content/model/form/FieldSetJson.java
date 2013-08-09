package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import com.enonic.wem.api.schema.content.form.FieldSet;

public class FieldSetJson
    extends FormItemJson
{
    private final FieldSet fieldSet;

    private FormItemJsonArray items;

    public FieldSetJson( final FieldSet fieldSet )
    {
        this.fieldSet = fieldSet;
        this.items = new FormItemJsonArray( fieldSet.getFormItems() );
    }

    public String getType()
    {
        return FieldSet.class.getSimpleName();
    }

    public FormItemJsonArray getItems()
    {
        return items;
    }

    public String getName()
    {
        return ( fieldSet instanceof FieldSet ) ? ( (FieldSet) fieldSet ).getName() : null;
    }

    public String getLabel()
    {
        return ( fieldSet instanceof FieldSet ) ? ( (FieldSet) fieldSet ).getLabel() : null;
    }
}
