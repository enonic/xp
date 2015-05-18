package com.enonic.xp.form.inputtype;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;

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
