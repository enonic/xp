package com.enonic.wem.xml.form;

import java.util.List;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;

final class FormItemsHelper
{

    static void fromFormItems( List<FormItemXml> formItems, final Iterable<FormItem> input )
    {
        for ( FormItem formItem : input )
        {
            if ( formItem instanceof Input )
            {
                final InputXml inputXml = new InputXml();
                inputXml.from( formItem.toInput() );
                formItems.add( inputXml );
            }
            else if ( formItem instanceof FormItemSet )
            {
                final FormItemSetXml formItemSetXml = new FormItemSetXml();
                formItemSetXml.from( formItem.toFormItemSet() );
                formItems.add( formItemSetXml );
            }
            else if ( formItem instanceof FieldSet )
            {
                final FieldSetXml fieldSetXml = new FieldSetXml();
                fieldSetXml.from( (FieldSet) formItem.toLayout() );
                formItems.add( fieldSetXml );
            }
            else
            {
                throw new SystemException( "Serialization for FormItem '" + formItem.getClass().getName() + "' not implemented" );
            }
        }
    }

    static FormItem toFormItem( final FormItemXml formItemXml )
    {
        if ( formItemXml instanceof FormItemSetXml )
        {
            final FormItemSet.Builder formItemSetBuilder = FormItemSet.newFormItemSet();
            final FormItemSetXml formItemSetXml = (FormItemSetXml) formItemXml;
            formItemSetXml.to( formItemSetBuilder );
            return formItemSetBuilder.build();
        }
        else if ( formItemXml instanceof InputXml )
        {
            final Input.Builder inputBuilder = Input.newInput();
            final InputXml inputXml = (InputXml) formItemXml;
            inputXml.to( inputBuilder );
            return inputBuilder.build();
        }
        else if ( formItemXml instanceof FieldSetXml )
        {
            final FieldSet.Builder fieldSetBuilder = FieldSet.newFieldSet();
            final FieldSetXml fieldSetXml = (FieldSetXml) formItemXml;
            fieldSetXml.to( fieldSetBuilder );
            return fieldSetBuilder.build();
        }
        else
        {
            throw new SystemException( "Serialization for FormItemXml '" + formItemXml.getClass().getName() + "' not implemented" );
        }
    }
}
