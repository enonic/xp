package com.enonic.wem.core.schema.mixin;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemVisitor;
import com.enonic.wem.api.form.MixinReference;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;

class MixinUsageResolver
{
    private final Mixin mixin;

    MixinUsageResolver( final Mixin mixin )
    {
        this.mixin = mixin;
    }

    ContentTypes resolveUsingContentTypes( final ContentTypes contentTypes )
    {

        final ContentTypes.Builder builder = new ContentTypes.Builder();

        for ( ContentType contentType : contentTypes )
        {

            if ( !resolveUsingMixinReferences( contentType.form() ).isEmpty() )
            {
                builder.add( contentType );
            }
        }

        return builder.build();
    }

    List<MixinReference> resolveUsingMixinReferences( final Form form )
    {
        final List<MixinReference> found = new ArrayList<>();
        new FormItemVisitor()
        {
            @Override
            public void visit( final FormItem formItem )
            {
                MixinReference mixinReference = formItem.toMixinReference();
                if ( mixinReference.getMixinName().equals( mixin.getName() ) )
                {
                    found.add( mixinReference );
                }
            }
        }.restrictFormItemType( MixinReference.class ).
            traverse( form );
        return found;
    }
}
