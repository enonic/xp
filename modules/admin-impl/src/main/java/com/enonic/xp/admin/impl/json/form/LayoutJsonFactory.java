package com.enonic.xp.admin.impl.json.form;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Layout;

@Beta
@SuppressWarnings("UnusedDeclaration")
public class LayoutJsonFactory
{
    public static FormItemJson create( final Layout layout )
    {
        if ( layout instanceof FieldSet )
        {
            return new FieldSetJson( (FieldSet) layout );
        }

        throw new IllegalArgumentException( "Unsupported Layout: " + layout.getClass().getSimpleName() );
    }
}
