package com.enonic.xp.form;

import java.util.HashMap;
import java.util.Map;

final class FormValidator
{
    static void validate( final Form form )
    {
        Map<FormItemPath, FormItem> items = new HashMap<>();
        validateFormItems( form, items );
    }

    static private void validateFormItems( final Iterable<FormItem> formItems, Map<FormItemPath, FormItem> items )
    {
        for ( FormItem item : formItems )
        {
            switch ( item.getType() )
            {
                case INPUT:
                case MIXIN_REFERENCE:
                case FORM_OPTION_SET_OPTION:
                    if ( items.containsKey( item.getPath() ) )
                    {
                        throw new IllegalArgumentException( "FormItem already added: " + item.getPath() );
                    }
                    items.put( item.getPath(), item );
                    break;

                case FORM_OPTION_SET:
                    if ( items.containsKey( item.getPath() ) )
                    {
                        throw new IllegalArgumentException( "FormItem already added: " + item.getPath() );
                    }
                    items.put( item.getPath(), item );
                    for ( final FormOptionSetOption formOptionSetOption : item.toFormOptionSet() )
                    {
                        validateFormItems( formOptionSetOption.getFormItems(), items );
                    }
                    break;

                case FORM_ITEM_SET:
                    if ( items.containsKey( item.getPath() ) )
                    {
                        throw new IllegalArgumentException( "FormItem already added: " + item.getPath() );
                    }
                    items.put( item.getPath(), item );
                    validateFormItems( (FormItemSet) item, items );
                    break;

                case LAYOUT:
                    validateFormItems( (FieldSet) item, items );
                    break;
            }
        }
    }
}
