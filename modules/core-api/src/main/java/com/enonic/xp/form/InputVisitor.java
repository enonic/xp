package com.enonic.xp.form;

public abstract class InputVisitor
{
    public void traverse( final Iterable<FormItem> formItems )
    {
        for ( FormItem formItem : formItems )
        {
            if ( formItem instanceof Input )
            {
                visit( (Input) formItem );
            }
            else if ( formItem instanceof FormItemSet || formItem instanceof FieldSet )
            {
                traverse( (Iterable<FormItem>) formItem );
            }
        }
    }

    public abstract void visit( final Input input );
}
