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

    private List<FormItem> transform( final Iterable<FormItem> iterable )
    {
        final List<FormItem> formItems = new ArrayList<>();
        for ( final FormItem formItem : iterable )
        {
            if ( formItem instanceof MixinReference )
            {
                final MixinReference mixinReference = (MixinReference) formItem;
                final Mixin mixin = client.execute( Commands.mixin().get().byName( mixinReference.getMixinName() ) );
                if ( mixin != null )
                {
                    for ( FormItem mixinFormItem : mixin.getFormItems() )
                    {
                        formItems.add( mixinFormItem.copy() );
                    }
                }
                else
                {
                    throw new MixinNotFound( mixinReference.getMixinName() );
                }
            }
            else if ( formItem instanceof FormItemSet )
            {
                final FormItemSet.Builder forItemSetBuilder = FormItemSet.newFormItemSet().name( formItem.getName() );
                forItemSetBuilder.addFormItems( transform( (FormItemSet) formItem ) );
                formItems.add( forItemSetBuilder.build() );
            }
            else
            {
                formItems.add( formItem.copy() );
            }
        }
        return formItems;
    }


}
