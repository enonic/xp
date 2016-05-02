package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;
import static com.enonic.xp.content.ContentPropertyNames.HTMLAREA_TEXT;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.SITE;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;

class ContentIndexConfigFactory
{
    public static IndexConfigDocument create( final CreateContentTranslatorParams params )
    {
        return doCreateIndexConfig( getNumberOfHtmlAreas( params.getData() ) );
    }

    public static IndexConfigDocument create( final Content content )
    {
        return doCreateIndexConfig( getNumberOfHtmlAreas( content.getData() ) );
    }

    private static IndexConfigDocument doCreateIndexConfig( int htmlAreas )
    {
        final PatternIndexConfigDocument.Builder configDocumentBuilder = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
            add( CREATOR, IndexConfig.MINIMAL ).
            add( MODIFIER, IndexConfig.MINIMAL ).
            add( CREATED_TIME, IndexConfig.MINIMAL ).
            add( MODIFIED_TIME, IndexConfig.MINIMAL ).
            add( OWNER, IndexConfig.MINIMAL ).
            add( PAGE, IndexConfig.MINIMAL ).
            add( PropertyPath.from( PAGE, "regions" ), IndexConfig.NONE ).
            add( SITE, IndexConfig.NONE ).
            add( DATA, IndexConfig.BY_TYPE ).
            add( TYPE, IndexConfig.MINIMAL ).
            add( ATTACHMENT, IndexConfig.MINIMAL ).
            add( PropertyPath.from( EXTRA_DATA ), IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE );

        final IndexConfig htmlIndexConfig = IndexConfig.create().
            enabled( true ).
            fulltext( true ).
            nGram( true ).
            decideByType( false ).
            includeInAllText( true ).
            addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
            build();

        for ( int i = 1; i <= htmlAreas; i++ )
        {
            configDocumentBuilder.add( HTMLAREA_TEXT + ( i == 1 ? "" : i ), htmlIndexConfig );
        }

        return configDocumentBuilder.build();
    }

    private static int getNumberOfHtmlAreas( final PropertyTree propertyTree )
    {
        int counter = 0;
        for ( final String propName : propertyTree.getRoot().getPropertyNames() )
        {
            if ( propName.startsWith( "htmlarea_text" ) )
            {
                counter++;
            }
        }
        return counter;
    }
}
