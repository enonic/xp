package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.mixin.Mixins;

import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;

public class XDataConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final Mixins mixins;

    public XDataConfigProcessor( final Mixins mixins )
    {
        this.mixins = mixins;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( PropertyPath.from( EXTRA_DATA, "*" ), IndexConfig.BY_TYPE );

        if ( this.mixins != null )
        {
            this.mixins.forEach( mixin -> {
                final IndexConfigVisitor indexConfigVisitor =
                    new IndexConfigVisitor( String.join( ".", EXTRA_DATA, mixin.getName().getApplicationPrefix(), mixin.getName().getLocalName() ), builder );
                indexConfigVisitor.traverse( mixin.getForm() );
            } );
        }

        return builder;
    }
}
