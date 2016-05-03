package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class ContentIndexConfigFactoryTest
{

    protected ContentTypeService contentTypeService;

    @Before
    public void setUp()
        throws Exception
    {
        contentTypeService = Mockito.mock( ContentTypeService.class );
    }

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

        final Form form = Form.create().build();
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test" ).form( form ).build();
        Mockito.when( contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

        final IndexConfigDocument indexConfigDocument =
            ContentIndexConfigFactory.create( createContentTranslatorParams, contentTypeService );

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

        final Input htmlInput1 = Input.create().name( "html_area1" ).label( "HtmlArea" ).inputType( InputTypeName.HTML_AREA ).build();
        final Input htmlInput2 = Input.create().name( "html_area2" ).label( "HtmlArea" ).inputType( InputTypeName.HTML_AREA ).build();
        final Input htmlInput3 = Input.create().name( "html_area3" ).label( "HtmlArea" ).inputType( InputTypeName.HTML_AREA ).build();
        final Input htmlInput4 = Input.create().name( "html_area4" ).label( "HtmlArea" ).inputType( InputTypeName.HTML_AREA ).build();

        final FormItemSet myOuterSet =
            FormItemSet.create().name( "myOuterSet" ).label( "Label" ).multiple( true ).addFormItem( htmlInput1 ).build();
        final FormItemSet myInnerSet =
            FormItemSet.create().name( "myInnerSet" ).label( "Label" ).multiple( true ).addFormItem( htmlInput2 ).build();
        final FormItemSet myInnermostSet =
            FormItemSet.create().name( "myInnermostSet" ).label( "Label" ).multiple( true ).addFormItem( htmlInput3 ).build();
        myInnerSet.add( myInnermostSet );
        myOuterSet.add( myInnerSet );

        final Form form = Form.create().
            addFormItem( htmlInput4 ).
            addFormItem( myOuterSet ).build();

        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test" ).form( form ).build();
        Mockito.when( contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

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

        final IndexConfigDocument indexConfigDocument =
            ContentIndexConfigFactory.create( createContentTranslatorParams, contentTypeService );

        assertEquals( htmlIndexConfig, indexConfigDocument.getConfigForPath( PropertyPath.from( htmlInput1.getPath().toString() ) ) );

        assertEquals( htmlIndexConfig, indexConfigDocument.getConfigForPath( PropertyPath.from( htmlInput2.getPath().toString() ) ) );

        assertEquals( htmlIndexConfig, indexConfigDocument.getConfigForPath( PropertyPath.from( htmlInput3.getPath().toString() ) ) );

        assertEquals( htmlIndexConfig, indexConfigDocument.getConfigForPath( PropertyPath.from( htmlInput4.getPath().toString() ) ) );

    }

    @Test
    public void page_indexing()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet page = data.addSet( ContentPropertyNames.PAGE );
        final PropertySet region = page.addSet( "region" );
        final PropertySet component = region.addSet( "component" );
        final PropertySet textcomponent = component.addSet( "textcomponent" );
        textcomponent.setString( "text", "<h1>This is a text component</h1>" );

        final CreateContentTranslatorParams createContentTranslatorParams = CreateContentTranslatorParams.create().
            type( ContentTypeName.imageMedia() ).
            displayName( "myContent" ).
            name( "my-content" ).
            parent( ContentPath.ROOT ).
            contentData( data ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ContentConstants.DEFAULT_CHILD_ORDER ).
            build();

        final IndexConfigDocument indexConfigDocument =
            ContentIndexConfigFactory.create( createContentTranslatorParams, contentTypeService );

        assertFalse( indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.PAGE ) ).isEnabled() );
        assertFalse( indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.PAGE, "region", "component", "partcomponent", "template" ) ).isEnabled() );
        assertTrue( indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.PAGE, "region", "component", "textcomponent", "text" ) ).isEnabled() );
    }
}