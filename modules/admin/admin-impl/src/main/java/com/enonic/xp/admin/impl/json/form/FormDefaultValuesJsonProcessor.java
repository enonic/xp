package com.enonic.xp.admin.impl.json.form;

import java.util.Iterator;

import com.enonic.xp.data.Value;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypes;

import static com.enonic.xp.form.FormItemType.FORM_ITEM_SET;
import static com.enonic.xp.form.FormItemType.FORM_OPTION_SET;
import static com.enonic.xp.form.FormItemType.INPUT;
import static com.enonic.xp.form.FormItemType.LAYOUT;

final class FormDefaultValuesJsonProcessor
{

    static void setDefaultValues( final Form form, final FormJson formJson )
    {
        processFormItems( form.getFormItems(), formJson.getFormItems() );
    }

    private static void processFormItems( final Iterable<FormItem> formItems, final Iterable<FormItemJson> formItemsJson )
    {
        final Iterator<FormItem> formItemsIt = formItems.iterator();
        final Iterator<FormItemJson> formItemsJsonIt = formItemsJson.iterator();
        while ( formItemsIt.hasNext() && formItemsJsonIt.hasNext() )
        {
            final FormItem formItem = formItemsIt.next();
            final FormItemJson formItemJson = formItemsJsonIt.next();

            if ( formItem.getType() == INPUT )
            {
                final Input input = formItem.toInput();
                final InputJson inputJson = (InputJson) formItemJson;
                if ( input.getDefaultValue() != null )
                {
                    try
                    {
                        final Value defaultValue = InputTypes.BUILTIN.resolve( input.getInputType() ).
                            createDefaultValue( input );
                        if ( defaultValue != null )
                        {
                            inputJson.setDefaultValue( defaultValue );
                        }
                    }
                    catch ( IllegalArgumentException ex )
                    {
                        // DO NOTHING
                    }
                }
            }
            else if ( formItem.getType() == FORM_ITEM_SET )
            {
                processFormItems( formItem.toFormItemSet().getFormItems(), ( (FormItemSetJson) formItemJson ).getItems() );
            }
            else if ( formItem.getType() == LAYOUT && formItem.toLayout() instanceof FieldSet )
            {
                processFormItems( (FieldSet) formItem, ( (FieldSetJson) formItemJson ).getItems() );
            }
            else if ( formItem.getType() == FORM_OPTION_SET )
            {
                final Iterator<FormOptionSetOption> formOptionSetOptionIt = formItem.toFormOptionSet().iterator();
                final Iterator<FormOptionSetOptionJson> formOptionSetOptionJsonIt =
                    ( (FormOptionSetJson) formItemJson ).getOptions().iterator();

                while ( formOptionSetOptionIt.hasNext() && formOptionSetOptionJsonIt.hasNext() )
                {
                    final FormOptionSetOption formOptionSetOption = formOptionSetOptionIt.next();
                    final FormOptionSetOptionJson formOptionSetOptionJson = formOptionSetOptionJsonIt.next();

                    processFormItems( formOptionSetOption.getFormItems(), formOptionSetOptionJson.getItems() );
                }
            }
        }
    }
}
