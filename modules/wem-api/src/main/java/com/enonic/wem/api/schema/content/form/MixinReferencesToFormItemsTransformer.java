package com.enonic.wem.api.schema.content.form;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinFetcher;

public class MixinReferencesToFormItemsTransformer
{
    private final MixinFetcher mixinFetcher;

    public MixinReferencesToFormItemsTransformer( final MixinFetcher mixinFetcher )
    {
        Preconditions.checkNotNull( mixinFetcher, "mixinFetcher cannot be null" );
        this.mixinFetcher = mixinFetcher;
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
                final Mixin mixin = mixinFetcher.getMixin( mixinReference.getQualifiedMixinName() );
                // TODO: what to do when mixin not found?
                if ( mixin != null )
                {
                    Preconditions.checkArgument( mixinReference.getMixinClass() == mixin.getFormItem().getClass(),
                                                 "Mixin expected to be of type %s: " + mixin.getFormItem().getClass().getSimpleName(),
                                                 mixinReference.getMixinClass().getSimpleName() );

                    final FormItem formItemCreatedFromMixin = FormItem.from( mixin, mixinReference );
                    if ( formItemCreatedFromMixin instanceof FormItemSet )
                    {
                        final FormItemSet formItemSet = (FormItemSet) formItemCreatedFromMixin;
                        final FormItemSet transformedFormItemSet = transformFormItemSet( formItemSet );
                        formItems.add( transformedFormItemSet );
                    }
                    else
                    {
                        formItems.add( formItemCreatedFromMixin );
                    }
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
