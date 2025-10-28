package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.xdata.MixinDescriptors;
import com.enonic.xp.schema.xdata.MixinName;

import static com.enonic.xp.content.ContentPropertyNames.MIXIN_DATA;

public class MixinConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final MixinDescriptors descriptors;

    public MixinConfigProcessor( final MixinDescriptors descriptors )
    {
        this.descriptors = descriptors;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( PropertyPath.from( MIXIN_DATA, "*" ), IndexConfig.BY_TYPE );

        if ( this.descriptors != null )
        {
            this.descriptors.forEach( descriptor -> {
                final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor(
                    String.join( ".", MIXIN_DATA, getApplicationPrefix( descriptor.getName() ), descriptor.getName().getLocalName() ),
                    builder );
                indexConfigVisitor.traverse( descriptor.getForm() );
            } );
        }

        return builder;
    }

    private String getApplicationPrefix( final MixinName mixinName )
    {
        return mixinName.getApplicationKey().toString().replace( '.', '-' );
    }
}
