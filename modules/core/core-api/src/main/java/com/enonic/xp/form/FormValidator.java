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

    static private void validateFormItems( final Iterable<FormItem> formItems, final Map<FormItemPath, FormItem> items )
    {
        for ( final FormItem item : formItems )
        {
            if ( item.getType() == FormItemType.LAYOUT )
            {
                validateFormItems( (FieldSet) item, items );
            }
            else
            {
                final FormItemPath path = ( (NamedFormItem) item ).getPath();
                if ( items.containsKey( path ) )
                {
                    throw new IllegalArgumentException( "FormItem already added: " + path );
                }
                items.put( path, item );

                switch ( item.getType() )
                {
                    case FORM_OPTION_SET:
                        for ( final FormOptionSetOption formOptionSetOption : item.toFormOptionSet() )
                        {
                            validateFormItems( formOptionSetOption.getFormItems(), items );
                        }
                        break;

                    case FORM_ITEM_SET:
                        validateFormItems( (FormItemSet) item, items );
                        break;
                }
            }
        }
    }
}
