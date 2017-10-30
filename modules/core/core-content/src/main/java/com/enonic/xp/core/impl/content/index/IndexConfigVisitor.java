package com.enonic.xp.core.impl.content.index;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;

public class IndexConfigVisitor
    extends InputVisitor
{

    private final PatternIndexConfigDocument.Builder configDocumentBuilder;

    private final String parentElement;

    public IndexConfigVisitor( final String parentElement, final PatternIndexConfigDocument.Builder configDocumentBuilder )
    {
        this.parentElement = parentElement;
        this.configDocumentBuilder = configDocumentBuilder;
    }

    @Override
    public void visit( final Input input )
    {
        if ( InputTypeName.HTML_AREA.equals( input.getInputType() ) )
        {
            final IndexConfig htmlIndexConfig = IndexConfig.create().
                enabled( true ).
                fulltext( true ).
                nGram( true ).
                decideByType( false ).
                includeInAllText( true ).
                addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
                build();

            configDocumentBuilder.add( createPath( input ), htmlIndexConfig );
        }
    }

    private PropertyPath createPath( final Input input )
    {
        if ( this.parentElement == null )
        {
            return PropertyPath.from( input.getPath().toString() );
        }

        return PropertyPath.from( parentElement, input.getPath().getElementsAsArray() );
    }

}
