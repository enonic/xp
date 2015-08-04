package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.core.impl.index.processor.HtmlStripper;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexValueProcessorRegistry;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;
import static com.enonic.xp.content.ContentPropertyNames.HTMLAREA_TEXT;
import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.SITE;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;

class ContentIndexConfigFactory
{
    public static IndexConfigDocument create( final CreateContentTranslatorParams params )
    {
        return doCreateIndexConfig();
    }

    public static IndexConfigDocument create( final Content content )
    {
        return doCreateIndexConfig();
    }

    private static IndexConfigDocument doCreateIndexConfig()
    {
        final PatternIndexConfigDocument.Builder configDocumentBuilder = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
            add( PAGE, IndexConfig.MINIMAL ).
            add( PropertyPath.from( PAGE, "regions" ), IndexConfig.NONE ).
            add( SITE, IndexConfig.NONE ).
            add( DATA, IndexConfig.BY_TYPE ).
            add( TYPE, IndexConfig.MINIMAL ).
            add( ATTACHMENT, IndexConfig.MINIMAL ).
            add( PropertyPath.from( EXTRA_DATA ), IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE );

        IndexConfig htmlIndexConfig = IndexConfig.create().
            enabled( true ).
            fulltext( true ).
            nGram( true ).
            decideByType( false ).
            includeInAllText( true ).
            addIndexValueProcessor( IndexValueProcessorRegistry.getIndexValueProcessor( HtmlStripper.NAME ) ).
            build();
        configDocumentBuilder.add( HTMLAREA_TEXT, htmlIndexConfig );

        return configDocumentBuilder.build();
    }

}
