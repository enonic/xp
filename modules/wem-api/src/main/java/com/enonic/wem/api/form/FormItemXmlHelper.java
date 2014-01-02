package com.enonic.wem.api.form;

import java.util.List;

import com.enonic.wem.api.support.serializer.XmlSerializingException;

final class FormItemXmlHelper
{
    static void fromFormItem( List<FormItemXml> formItemXmlList, final Iterable<FormItem> formItems )
    {
        for ( FormItem formItem : formItems )
        {
            formItemXmlList.add( fromFormItem( formItem ) );
        }
    }

    static FormItemXml fromFormItem( final FormItem formItem )
    {
        if ( formItem instanceof Input )
        {
            final InputXml inputXml = new InputXml();
            inputXml.from( formItem.toInput() );
            return inputXml;
        }
        else if ( formItem instanceof FormItemSet )
        {
            final FormItemSetXml formItemSetXml = new FormItemSetXml();
            formItemSetXml.from( formItem.toFormItemSet() );
            return formItemSetXml;
        }
        else if ( formItem instanceof FieldSet )
        {
            final FieldSetXml fieldSetXml = new FieldSetXml();
            fieldSetXml.from( (FieldSet) formItem.toLayout() );
            return fieldSetXml;
        }
        else
        {
            throw new XmlSerializingException( "Serialization for FormItem '" + formItem.getClass().getName() + "' not implemented" );
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
            throw new XmlSerializingException( "Serialization for FormItemXml '" + formItemXml.getClass().getName() + "' not implemented" );
        }
    }
}
