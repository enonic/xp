package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import com.enonic.wem.admin.rest.resource.model.Item;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype.InputJson;
import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.Layout;
import com.enonic.wem.api.schema.content.form.MixinReference;

public class FormItemJson
    extends Item
{
    private final FormItem formItem;

    public FormItemJson( final FormItem formItem )
    {
        this.formItem = formItem;
    }

    public AbstractFormItem get()
    {
        if ( formItem instanceof FormItemSet )
        {
            return new FormItemSetJson( (FormItemSet) formItem );
        }
        else if ( formItem instanceof Layout )
        {
            return new LayoutJson( (Layout) formItem );
        }
        else if ( formItem instanceof Input )
        {
            return new InputJson( (Input) formItem );
        }
        else if ( formItem instanceof MixinReference )
        {
            return new MixinReferenceJson( (MixinReference) formItem );
        }
        throw new UnsupportedOperationException( "Unsupported FormItem: " + formItem.getClass().getSimpleName() );
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
