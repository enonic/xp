package com.enonic.wem.api.form;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.mixin.Mixin;

public class MixinReferencesToFormItemsTransformer
{
    private final Client client;

    public MixinReferencesToFormItemsTransformer( final Client client )
    {
        Preconditions.checkNotNull( client, "client cannot be null" );
        this.client = client;
    }

    public Form transformForm( final Form form )
    {
        final Form.Builder transformedForm = Form.newForm();
        final List<FormItem> transformedFormItems = transform( form );

        for ( final FormItem formItem : transformedFormItems )
        {
            transformedForm.addFormItem( formItem );
        }
        return transformedForm.build();
    }

    public FormItemSet transformFormItemSet( final FormItemSet formItemSet )
    {
        final FormItemSet.Builder transformed = FormItemSet.newFormItemSet( formItemSet );
        transformed.clearFormItems();
        final List<FormItem> transformedFormItems = transform( formItemSet );

        for ( final FormItem formItem : transformedFormItems )
        {
            transformed.addFormItem( formItem );
        }
        return transformed.build();
    }

    private List<FormItem> transform( final Iterable<FormItem> iterable )
    {
        final List<FormItem> formItems = new ArrayList<>();
        for ( final FormItem formItem : iterable )
        {
            if ( formItem instanceof MixinReference )
            {
                final MixinReference mixinReference = (MixinReference) formItem;
                final Mixin mixin = client.execute( Commands.mixin().get().byQualifiedName( mixinReference.getQualifiedMixinName() ) );
                if ( mixin != null )
                {
                    FormItems mixinFormItems = mixin.getFormItems();
                    for ( FormItem mixinFormItem : mixinFormItems )
                    {
                        FormItem createdFormItem = FormItem.from( mixinFormItem, mixinReference );
                        if ( createdFormItem instanceof FormItemSet )
                        {
                            final FormItemSet formItemSet = (FormItemSet) createdFormItem;
                            final FormItemSet transformedFormItemSet = transformFormItemSet( formItemSet );
                            formItems.add( transformedFormItemSet );
                        }
                        else
                        {
                            formItems.add( createdFormItem );
                        }
                    }
                }
                else
                {
                    throw new MixinNotFound( mixinReference.getQualifiedMixinName() );
                }
            }
            else
            {
                formItem.setParent( null );
                formItems.add( formItem );
            }
        }
        return formItems;
    }


}
