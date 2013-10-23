package com.enonic.wem.admin.json.form;

import com.enonic.wem.admin.json.form.inputtype.InputJson;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.MixinReference;

public class FormItemJsonFactory
{
    public static FormItemJson create( final FormItem formItem )
    {
        if ( formItem instanceof FormItemSet )
        {
            return new FormItemSetJson( (FormItemSet) formItem );
        }
        else if ( formItem instanceof Layout )
        {
            return LayoutJsonFactory.create( (Layout) formItem );
        }
        else if ( formItem instanceof Input )
        {
            return new InputJson( (Input) formItem );
        }
        else if ( formItem instanceof MixinReference )
        {
            return new MixinReferenceJson( (MixinReference) formItem );
        }
        throw new IllegalArgumentException( "Unsupported FormItem: " + formItem.getClass().getSimpleName() );
    }
}
