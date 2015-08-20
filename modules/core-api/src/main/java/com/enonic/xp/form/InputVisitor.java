package com.enonic.xp.form;

public abstract class InputVisitor
{
    public final void traverse( final Iterable<FormItem> items )
    {
        for ( final FormItem item : items )
        {
            doTraverse( item );
        }
    }

    private void doTraverse( final FormItem item )
    {
        if ( item instanceof Input )
        {
            visit( (Input) item );
        }
        else if ( item instanceof FormItemSet )
        {
            doTraverse( (FormItemSet) item );
        }
        else if ( item instanceof FieldSet )
        {
            doTraverse( (FieldSet) item );
        }
    }

    private void doTraverse( final FormItemSet set )
    {
        traverse( set );
    }

    private void doTraverse( final FieldSet set )
    {
        traverse( set );
    }

    public abstract void visit( final Input input );
}
