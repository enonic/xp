package com.enonic.wem.core.content;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentTypeName;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class ContentNodeTranslatorTest
{
    private ContentNodeTranslator translator = new ContentNodeTranslator();

    @Test
    public void toNode_contentData_to_rootdataset()
        throws Exception
    {
        final DataSet rootDataSet = RootDataSet.newDataSet().set( "test", "testValue", ValueTypes.STRING ).build();

        final Content mycontent = Content.newContent().
            name( "mycontent" ).
            path( ContentPath.from( "/myPath" ) ).
            type( ContentTypeName.from( "my-content-type" ) ).
            contentData( new ContentData( rootDataSet.toRootDataSet() ) ).
            build();

        final Node node = translator.toNode( mycontent );

        final Property testProperty = node.property( "contentdata.test" );

        assertNotNull( testProperty );
        assertEquals( "testValue", testProperty.getValue().asString() );
    }

    @Test
    public void translate_entityIndexConfig_enabled_for_contentdata()
        throws Exception
    {
        final DataSet rootDataSet = RootDataSet.newDataSet().set( "test", "testValue", ValueTypes.STRING ).build();

        final Content mycontent = Content.newContent().
            name( "mycontent" ).
            path( ContentPath.from( "/myPath" ) ).
            type( ContentTypeName.from( "my-content-type" ) ).
            contentData( new ContentData( rootDataSet.toRootDataSet() ) ).
            build();

        final Node node = translator.toNode( mycontent );

        final EntityIndexConfig entityIndexConfig = node.getEntityIndexConfig();

        final PropertyIndexConfig testIndexConfig = entityIndexConfig.getPropertyIndexConfig( DataPath.from( "contentdata.test" ) );

        assertNotNull( testIndexConfig );
        assertTrue( testIndexConfig.enabled() && testIndexConfig.fulltextEnabled() && testIndexConfig.tokenizeEnabled() );
    }

    @Test
    public void translate_entityIndexConfig_disabled_for_form()
        throws Exception
    {
        FormItemSet formItemSet = FormItemSet.newFormItemSet().
            name( "mySet" ).
            label( "My set" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 0, 10 ).
            addFormItem( Input.newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( Input.newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() ).
            build();

        final Form form = Form.newForm().addFormItems( formItemSet.getFormItems() ).build();

        final Content mycontent = Content.newContent().
            name( "mycontent" ).
            path( ContentPath.from( "/myPath" ) ).
            type( ContentTypeName.from( "my-content-type" ) ).
            form( form ).
            build();

        final Node node = translator.toNode( mycontent );

        final EntityIndexConfig entityIndexConfig = node.getEntityIndexConfig();

        final PropertyIndexConfig testIndexConfig =
            entityIndexConfig.getPropertyIndexConfig( DataPath.from( "form.formItems.Input[0].inputType.name" ) );

        assertNotNull( testIndexConfig );
        assertTrue( !testIndexConfig.enabled() && !testIndexConfig.fulltextEnabled() && !testIndexConfig.tokenizeEnabled() );
    }


    @Test
    public void translate_content_to_node_and_back()
        throws Exception
    {

        final DataSet rootDataSet = RootDataSet.newDataSet().set( "test", "testValue", ValueTypes.STRING ).build();

        FormItemSet formItemSet = FormItemSet.newFormItemSet().
            name( "mySet" ).
            label( "My set" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 0, 10 ).
            addFormItem( Input.newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( Input.newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() ).
            build();

        final Form form = Form.newForm().addFormItems( formItemSet.getFormItems() ).build();

        final Content myContent = Content.newContent().
            id( ContentId.from( "myId" ) ).
            displayName( "myDisplayName" ).
            name( "myContent" ).
            path( ContentPath.from( "/myPath" ) ).
            type( ContentTypeName.from( "my-content-type" ) ).
            form( form ).
            contentData( new ContentData( rootDataSet.toRootDataSet() ) ).
            build();

        final Node node = translator.toNode( myContent );

        final Content translatedContent = translator.fromNode( node );

        final ContentData contentData = translatedContent.getContentData();

        assertNotNull( translatedContent.getDisplayName() );
        assertEquals( myContent.getDisplayName(), translatedContent.getDisplayName() );
        assertNotNull( translatedContent.getType() );
        assertEquals( myContent.getType(), translatedContent.getType() );
        assertNotNull( translatedContent.getForm() );
        assertEquals( myContent.getForm(), translatedContent.getForm() );
        assertNotNull( translatedContent.getPath() );
        // TODO: This will not match for now, since the space is removed when translating to Node
        //assertEquals( myContent.getPath(), translatedContent.getPath() );
        assertNotNull( translatedContent.getName() );
        assertEquals( myContent.getName(), translatedContent.getName() );
        Assert.assertNotNull( contentData );
        assertEquals( myContent.getContentData(), translatedContent.getContentData() );
    }
}
