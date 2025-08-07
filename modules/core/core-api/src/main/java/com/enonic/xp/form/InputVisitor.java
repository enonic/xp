package com.enonic.xp.form;

public abstract class InputVisitor
{
    public final void traverse( final Iterable<? extends FormItem> items )
    {
        for ( final FormItem item : items )
        {
            if ( item instanceof Input )
            {
                visit( (Input) item );
            }
            else if ( item instanceof FormItemSet formItemSet )
            {
                traverse( formItemSet );
            }
            else if ( item instanceof FieldSet fieldSet)
            {
                traverse( fieldSet );
            }
            else if ( item instanceof FormOptionSet formOptionSet )
            {
                for ( FormOptionSetOption option : formOptionSet )
                {
                    traverse( option );
                }
            }
        }
    }

    public abstract void visit( Input input );
}
