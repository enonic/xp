package com.enonic.xp.form;

import com.google.common.annotations.Beta;

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
