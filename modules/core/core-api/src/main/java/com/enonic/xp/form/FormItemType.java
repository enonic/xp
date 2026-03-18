package com.enonic.xp.form;


public enum FormItemType
{
    INPUT, FORM_ITEM_SET, FIELD_SET, FORM_FRAGMENT, FORM_OPTION_SET, FORM_OPTION_SET_OPTION;

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
            return FIELD_SET;
        }
        else if ( FormFragment.class.getSimpleName().equals( value ) )
        {
            return FORM_FRAGMENT;
        }
        else if ( FormOptionSetOption.class.getSimpleName().equals( value ) )
        {
            return FORM_OPTION_SET_OPTION;
        }
        else if ( FormOptionSet.class.getSimpleName().equals( value ) )
        {
            return FORM_OPTION_SET;
        }
        else
        {
            throw new IllegalArgumentException( "Unknown FormItemType: " + value );
        }
    }
}
