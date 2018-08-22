package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.xdata.XDatas;

import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;

public class XDataConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final XDatas xDatas;

    public XDataConfigProcessor( final XDatas xDatas )
    {
        this.xDatas = xDatas;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( PropertyPath.from( EXTRA_DATA, "*" ), IndexConfig.BY_TYPE );

        if ( this.xDatas != null )
        {
            this.xDatas.forEach( xData -> {
                final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor(
                    String.join( ".", EXTRA_DATA, xData.getName().getApplicationPrefix(), xData.getName().getLocalName() ), builder );
                indexConfigVisitor.traverse( xData.getForm() );
            } );
        }

        return builder;
    }
}
