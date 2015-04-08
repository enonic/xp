package com.enonic.xp.form;


import com.google.common.annotations.Beta;

@Beta
public enum FormItemType
{
    INPUT,
    FORM_ITEM_SET,
    LAYOUT,
    MIXIN_REFERENCE;

    public static FormItemType parse( final String value )
    {
        if ( Input.class.getSimpleName().equals( value ) )
        {
            return INPUT;
        }
        else if ( FormItemSet.class.getSimpleName().equals( value ) )
        {
            return FORM_ITEM_SET;
        }
        else if ( FieldSet.class.getSimpleName().equals( value ) )
        {
            return LAYOUT;
        }
        else if ( InlineMixin.class.getSimpleName().equals( value ) )
        {
            return MIXIN_REFERENCE;
        }
        else
        {
            throw new IllegalArgumentException( "Unknown FormItemType: " + value );
        }
    }
}
