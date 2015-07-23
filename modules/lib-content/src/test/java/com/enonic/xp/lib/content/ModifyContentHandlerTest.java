package com.enonic.xp.lib.content;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class ModifyContentHandlerTest
    extends ScriptTestSupport
{
    private ContentService contentService;

    @Before
    public void setup()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        addService( ContentService.class, this.contentService );

        mockResource( "mymodule:/test/ModifyContentHandlerTest.js" );
        mockResource( "mymodule:/site/lib/xp/content.js" );
    }

    @Test
    public void modifyById()
        throws Exception
    {
        Mockito.when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0] ) );

        final Content content = TestDataFixtures.newSmallContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.translateToPropertyTree( Mockito.isA( JsonNode.class ), Mockito.eq( content.getType() ) ) ).
            thenReturn( createPropertyTree() );

        mockXData();

        runTestFunction( "/test/ModifyContentHandlerTest.js", "modifyById" );
    }

    @Test
    public void modifyByPath()
        throws Exception
    {
        final Content content = TestDataFixtures.newSmallContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.translateToPropertyTree( Mockito.isA( JsonNode.class ), Mockito.eq( content.getType() ) ) ).
            thenReturn( createPropertyTree() );

        Mockito.when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0] ) );

        mockXData();

        runTestFunction( "/test/ModifyContentHandlerTest.js", "modifyByPath" );
    }

    private void mockXData()
    {
        final PropertyTree mySchema = new PropertyTree();
        mySchema.addDouble( "a", 1.0 );

        Mockito.when( this.contentService.translateToPropertyTree( Mockito.isA( JsonNode.class ),
                                                                   Mockito.eq( MixinName.from( "com.enonic.mymodule:myschema" ) ) ) ).
            thenReturn( mySchema );

        final PropertyTree other = new PropertyTree();
        other.addString( "name", "test" );

        Mockito.when( this.contentService.translateToPropertyTree( Mockito.isA( JsonNode.class ),
                                                                   Mockito.eq( MixinName.from( "com.enonic.mymodule:other" ) ) ) ).
            thenReturn( other );
    }

    private PropertyTree createPropertyTree()
    {
        final PropertyTree data = new PropertyTree();
        data.addDouble( "a", 2.0 );
        data.addString( "b", "2" );
        final PropertySet c1 = data.addSet( "c" );
        final PropertySet c2 = data.addSet( "c" );

        c1.addBoolean( "d", true );
        c2.addBoolean( "d", true );
        c2.addStrings( "e", "3", "4", "5" );
        c2.addLong( "f", 2l );

        data.addString( "z", "99" );
        return data;
    }

    private Content invokeUpdate( final UpdateContentParams params )
    {
        Assert.assertEquals( ContentId.from( "123456" ), params.getContentId() );

        final ContentEditor editor = params.getEditor();
        Assert.assertNotNull( editor );

        final Content content = TestDataFixtures.newSmallContent();
        final EditableContent editable = new EditableContent( content );

        editor.edit( editable );
        return editable.build();
    }

    @Test
    public void modify_notFound()
        throws Exception
    {
        runTestFunction( "/test/ModifyContentHandlerTest.js", "modify_notFound" );
    }
}
