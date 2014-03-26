package com.enonic.wem.api.form;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixin;

public class MixinReferencesToFormItemsTransformer
{
    private final MixinService mixinService;

    public MixinReferencesToFormItemsTransformer( final MixinService mixinService )
    {
        this.mixinService = mixinService;
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
                final Mixin mixin = mixinService.getByName( new GetMixinParams( mixinReference.getMixinName() ) );
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
                final FormItemSet.Builder formItemSetBuilder = FormItemSet.newFormItemSet((FormItemSet) formItem);
                formItemSetBuilder.clearFormItems();
                formItemSetBuilder.addFormItems( transform( (FormItemSet) formItem ) );
                formItems.add( formItemSetBuilder.build() );
            }
            else
            {
                formItems.add( formItem.copy() );
            }
        }
        return formItems;
    }


}
