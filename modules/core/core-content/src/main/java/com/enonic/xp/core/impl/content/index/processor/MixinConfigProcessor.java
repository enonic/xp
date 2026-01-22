package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.mixin.MixinDescriptors;
import com.enonic.xp.schema.mixin.MixinName;

import static com.enonic.xp.content.ContentPropertyNames.MIXINS;

public class MixinConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final MixinDescriptors descriptors;

    public MixinConfigProcessor( final MixinDescriptors descriptors )
    {
        this.descriptors = descriptors;
    }

    @Override
    public PatternIndexConfigDocument processDocument( final PatternIndexConfigDocument config )
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create( config );

        builder.add( IndexPath.from( MIXINS, "*" ), IndexConfig.BY_TYPE );

        if ( this.descriptors != null )
        {
            this.descriptors.forEach( descriptor -> {
                final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor(
                    String.join( ".", MIXINS, getApplicationPrefix( descriptor.getName() ), descriptor.getName().getLocalName() ),
                    builder );
                indexConfigVisitor.traverse( descriptor.getForm() );
            } );
        }

        return builder.build();
    }

    private String getApplicationPrefix( final MixinName mixinName )
    {
        return mixinName.getApplicationKey().toString().replace( '.', '-' );
    }
}
