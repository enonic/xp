package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinService;

public class InlineMixinsToFormItemsTransformer
{
    private final MixinService mixinService;

    public InlineMixinsToFormItemsTransformer( final MixinService mixinService )
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
            if ( formItem instanceof InlineMixin )
            {
                final InlineMixin inline = (InlineMixin) formItem;
                final Mixin mixin = mixinService.getByName( inline.getMixinName() );
                if ( mixin != null )
                {
                    for ( FormItem mixinFormItem : mixin.getFormItems() )
                    {
                        formItems.add( mixinFormItem.copy() );
                    }
                }
                else
                {
                    throw new MixinNotFound( inline.getMixinName() );
                }
            }
            else if ( formItem instanceof FormItemSet )
            {
                final FormItemSet.Builder formItemSetBuilder = FormItemSet.newFormItemSet( (FormItemSet) formItem );
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
