package com.enonic.xp.core.impl.content;

import org.junit.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class ContentIndexConfigFactoryTest
{
    @Test
    public void media_indexing()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "myString", "myStringValue" );
        final PropertySet metadata = data.addSet( ContentPropertyNames.METADATA );
        metadata.addString( "media", "imageMedia" );
        metadata.addDouble( "double", 13d );
        metadata.addString( "no-index", "no-index-value" );
        final PropertySet subSet = metadata.addSet( "subSet" );
        subSet.addString( "subSetValue", "promp" );

        final CreateContentTranslatorParams createContentTranslatorParams = CreateContentTranslatorParams.create().
            type( ContentTypeName.imageMedia() ).
            displayName( "myContent" ).
            name( "my-content" ).
            parent( ContentPath.ROOT ).
            contentData( data ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ContentConstants.DEFAULT_CHILD_ORDER ).
            build();

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create( createContentTranslatorParams );

        assertEquals( IndexConfig.MINIMAL, indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.EXTRA_DATA ) ) );

        assertEquals( IndexConfig.MINIMAL,
                      indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.EXTRA_DATA, "media" ) ) );

        assertEquals( IndexConfig.MINIMAL,
                      indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.EXTRA_DATA, "subSet" ) ) );

        assertEquals( IndexConfig.MINIMAL, indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.EXTRA_DATA, "subSet", "subSetValue" ) ) );
    }

    @Test
    public void htmlAreaIndexing()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "htmlarea_text", "value1" );
        data.addString( "htmlarea_text2", "value2" );
        data.addString( "htmlarea_text3", "value3" );

        final IndexConfig htmlIndexConfig = IndexConfig.create().
            enabled( true ).
            fulltext( true ).
            nGram( true ).
            decideByType( false ).
            includeInAllText( true ).
            addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
            build();

        final CreateContentTranslatorParams createContentTranslatorParams = CreateContentTranslatorParams.create().
            type( ContentTypeName.imageMedia() ).
            displayName( "myContent" ).
            name( "my-content" ).
            parent( ContentPath.ROOT ).
            contentData( data ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ContentConstants.DEFAULT_CHILD_ORDER ).
            build();

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create( createContentTranslatorParams );

        assertEquals( htmlIndexConfig, indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.HTMLAREA_TEXT ) ) );

        assertEquals( htmlIndexConfig,
                      indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.HTMLAREA_TEXT + "1" ) ) );

        assertEquals( htmlIndexConfig,
                      indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.HTMLAREA_TEXT + "2" ) ) );


    }
}