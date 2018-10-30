package com.enonic.xp.portal.impl.processor;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.EditableSite;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.processor.ProcessUpdateParams;
import com.enonic.xp.content.processor.ProcessUpdateResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static org.junit.Assert.*;

public class HtmlAreaContentProcessorTest
{
    private ProcessUpdateResult result;

    @Before
    public void setUp()
        throws Exception
    {

        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().
            contentType( ContentType.create().
                name( ContentTypeName.from( "myContentType" ) ).
                superType( ContentTypeName.folder() ).
                build() ).
            build();

        result = new HtmlAreaContentProcessor().processUpdate( processUpdateParams );
    }

    @Test
    public void empty_data()
        throws IOException
    {

        final EditableContent editableContent = new EditableContent( Media.create().
            name( "myContentName" ).
            type( ContentTypeName.imageMedia() ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            build() );

        result.getEditor().edit( editableContent );

        assertEquals( 0, editableContent.processedReferences.build().getSize() );
    }

    @Test
    public void content_data()
        throws IOException
    {

        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id\"/>" ) );

        final EditableContent editableContent = new EditableContent( Media.create().
            name( "myContentName" ).
            type( ContentTypeName.imageMedia() ).
            parentPath( ContentPath.ROOT ).
            data( data ).
            build() );

        result.getEditor().edit( editableContent );

        assertEquals( 1, editableContent.processedReferences.build().getSize() );
        assertTrue( editableContent.processedReferences.build().contains( ContentId.from( "image-id" ) ) );
    }

    @Test
    public void site_config_data()
        throws IOException
    {

        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id\"/>" ) );

        final EditableSite editableSite = new EditableSite( Site.create().
            name( "myContentName" ).
            type( ContentTypeName.site() ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            siteConfigs( SiteConfigs.create().
                add( SiteConfig.
                    create().
                    config( data ).
                    application( ApplicationKey.SYSTEM ).
                    build() ).
                build() ).
            build() );

        result.getEditor().edit( editableSite );

        assertEquals( 1, editableSite.processedReferences.build().getSize() );
        assertTrue( editableSite.processedReferences.build().contains( ContentId.from( "image-id" ) ) );
    }

    @Test
    public void extra_data()
        throws IOException
    {
        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id\"/>" ) );

        final EditableSite editableSite = new EditableSite( Site.create().
            name( "myContentName" ).
            type( ContentTypeName.site() ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            extraDatas( ExtraDatas.create().
                add( new ExtraData( XDataName.from( "xDataName" ), data ) ).
                build() ).
            build() );

        result.getEditor().edit( editableSite );

        assertEquals( 1, editableSite.processedReferences.build().getSize() );
        assertTrue( editableSite.processedReferences.build().contains( ContentId.from( "image-id" ) ) );
    }

    @Test
    public void supports()
        throws IOException
    {
        assertTrue( new HtmlAreaContentProcessor().supports( ContentType.create().
            name( ContentTypeName.from( "myContentType" ) ).
            superType( ContentTypeName.folder() ).
            build() ) );
    }
}
