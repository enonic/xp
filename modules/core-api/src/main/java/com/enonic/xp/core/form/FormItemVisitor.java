package com.enonic.xp.core.form;


import com.google.common.base.Preconditions;

public abstract class FormItemVisitor
{
    private Class type;

    public FormItemVisitor restrictFormItemType( Class type )
    {
        Preconditions.checkArgument( FormItem.class.isAssignableFrom( type ) );
        this.type = type;
        return this;
    }

    public void traverse( final Iterable<FormItem> formItems )
    {
        for ( FormItem formItem : formItems )
        {
            if ( type == null || type.equals( formItem.getClass() ) )
            {
                visit( formItem );
            }

            if ( formItem instanceof FormItemSet )
            {
                final FormItemSet formItemSet = formItem.toFormItemSet();
                traverse( formItemSet.getFormItems() );
            }
            else if ( formItem instanceof FieldSet )
            {
                final FieldSet fieldSet = (FieldSet) formItem;
                traverse( fieldSet.getFormItems() );
            }
        }
    }

    public abstract void visit( FormItem formItem );
}
