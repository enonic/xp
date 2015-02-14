package com.enonic.xp.form;

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
