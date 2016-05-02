package com.enonic.xp.core.impl.content;

import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;

import static com.enonic.xp.content.ContentPropertyNames.HTMLAREA_TEXT;

public class IndexConfigVisitor
    extends InputVisitor
{

    private final PatternIndexConfigDocument.Builder configDocumentBuilder;

    private int htmlAreasVisited = 0;

    public IndexConfigVisitor( final PatternIndexConfigDocument.Builder configDocumentBuilder )
    {
        this.configDocumentBuilder = configDocumentBuilder;
    }

    @Override
    public void visit( final Input input )
    {
        if ( InputTypeName.HTML_AREA.equals( input.getInputType() ) )
        {
            htmlAreasVisited++;

            final IndexConfig htmlIndexConfig = IndexConfig.create().
                enabled( true ).
                fulltext( true ).
                nGram( true ).
                decideByType( false ).
                includeInAllText( true ).
                addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
                build();

            configDocumentBuilder.add( HTMLAREA_TEXT + ( htmlAreasVisited == 1 ? "" : htmlAreasVisited ), htmlIndexConfig );
        }
    }
}
