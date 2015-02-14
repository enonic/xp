package com.enonic.xp.form;

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
        else if ( formItem instanceof InlineMixin )
        {
            return new InlineMixinJson( (InlineMixin) formItem );
        }
        throw new IllegalArgumentException( "Unsupported FormItem: " + formItem.getClass().getSimpleName() );
    }
}
