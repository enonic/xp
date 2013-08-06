package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import com.enonic.wem.api.schema.content.form.FieldSet;
import com.enonic.wem.api.schema.content.form.Layout;

public class LayoutJson
    extends AbstractFormItem
{
    private final Layout layout;

    private FormItemListJson items;

    public LayoutJson( final Layout layout )
    {
        this.layout = layout;

        if ( layout instanceof FieldSet )
        {
            final FieldSet set = ( FieldSet ) layout;
            this.items = new FormItemListJson( set.getFormItems() );
        }
    }

    public String getType()
    {
        return FieldSet.class.getSimpleName();
    }

    public FormItemListJson getItems()
    {
        return items;
    }

    public String getName()
    {
        return ( layout instanceof FieldSet ) ? ( (FieldSet) layout).getName() : null;
    }

    public String getLabel()
    {
        return ( layout instanceof FieldSet ) ? ( (FieldSet) layout).getLabel() : null;
    }
}
