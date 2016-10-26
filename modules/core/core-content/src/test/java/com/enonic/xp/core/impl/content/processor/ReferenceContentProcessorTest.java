package com.enonic.xp.core.impl.content.processor;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ReferenceContentProcessorTest
{
    private final ReferenceContentProcessor referenceContentProcessor = new ReferenceContentProcessor();

    private Parser<ContentIds> parser;

    private ContentTypeService contentTypeService;


    @Before
    public void setUp()
        throws Exception
    {
        this.parser = new ContentIdsHtmlParser();
        this.contentTypeService = Mockito.mock( ContentTypeService.class );

        referenceContentProcessor.setContentTypeService( this.contentTypeService );
        referenceContentProcessor.setParser( this.parser );
    }

    @Test
    public void testProcessCreate_empty()

    {
        final PropertyTree data = new PropertyTree();

        final CreateContentParams params = createContentParams( data );

        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, null );

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            form( Form.create().build() ).
            build();

        Mockito.when( this.contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

        final ProcessCreateResult result = this.referenceContentProcessor.processCreate( processCreateParams );

        assertEquals( result.getCreateContentParams().getData(), data );
    }

    @Test
    public void testProcessCreate_one_ref()

    {
        final PropertyTree data = new PropertyTree();
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        Form form = Form.create().addFormItem( input ).build();

        final ProcessCreateResult result = processCreate_one_ref( data, form, input.getName(), "aContent", "content://" );

        data.addReference( "_inputName_references", Reference.from( "aContent" ) );

        assertEquals( result.getCreateContentParams().getData(), data );
    }

    @Test
    public void testProcessCreate_ref_in_set()

    {
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        final FormItemSet formItemSet = FormItemSet.create().name( "testSet" ).addFormItem( input ).build();

        Form form = Form.create().addFormItem( formItemSet ).build();

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            form( form ).
            build();

        Mockito.when( this.contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

        final PropertyTree data = new PropertyTree();

        final PropertySet set = new PropertySet();
        set.addString( input.getName(), "<p><a href=\"content://aaa\">aaa</a></p>" );

        data.addSet( "testSet", set );

        final CreateContentParams params = createContentParams( data.copy() );
        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, null );

        final ProcessCreateResult result = this.referenceContentProcessor.processCreate( processCreateParams );

        data.getSet( "testSet" ).addReference( "_" + input.getName() + "_references", Reference.from( "aaa" ) );
        assertEquals( result.getCreateContentParams().getData(), data );
    }

    @Test
    public void testProcessCreate_one_download_ref()

    {
        final PropertyTree data = new PropertyTree();
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        Form form = Form.create().addFormItem( input ).build();

        final ProcessCreateResult result = processCreate_one_ref( data, form, input.getName(), "aContent", "media://download/" );

        data.addReference( "_inputName_references", Reference.from( "aContent" ) );

        assertEquals( result.getCreateContentParams().getData(), data );
    }

    @Test
    public void testProcessCreate_one_image_ref()

    {
        final PropertyTree data = new PropertyTree();
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        Form form = Form.create().addFormItem( input ).build();

        final ProcessCreateResult result = processCreate_one_ref( data, form, input.getName(), "aContent", "image://" );

        data.addReference( "_inputName_references", Reference.from( "aContent" ) );

        assertEquals( result.getCreateContentParams().getData(), data );
    }

    @Test
    public void testProcessUpdate_one_ref_add()
    {

        final PropertyTree data = new PropertyTree();
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        Form form = Form.create().addFormItem( input ).build();

        final ProcessCreateResult createResult = processCreate_one_ref( data, form, input.getName(), "aContent", "content://" );

        final PropertyTree resultData = createResult.getCreateContentParams().getData();
        final Value resValue = resultData.getValue( input.getPath().toString() );

        resultData.setString( input.getPath().toString(), resValue + "<p><a href=\"content://bContent\">bbb</a></p>" );

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            form( form ).
            build();

        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().
            contentType( contentType ).
            build();

        final ProcessUpdateResult updateResult = this.referenceContentProcessor.processUpdate( processUpdateParams );

        final EditableContent editableContent = new EditableContent( Content.create().
            name( createResult.getCreateContentParams().getName() ).
            type( createResult.getCreateContentParams().getType() ).
            parentPath( createResult.getCreateContentParams().getParent() ).
            data( resultData ).
            build() );

        updateResult.editor.edit( editableContent );

        final Set<Reference> refs = Sets.newHashSet( editableContent.data.getReferences( "_" + input.getName() + "_references" ) );

        assertTrue( refs.size() == 2 );
        assertTrue( refs.contains( Reference.from( "bContent" ) ) );
        assertTrue( refs.contains( Reference.from( "aContent" ) ) );
    }

    private ProcessCreateResult processCreate_one_ref( final PropertyTree data, final Form form, final String refInputName,
                                                       final String contentId, final String format )
    {

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            form( form ).
            build();

        Mockito.when( this.contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

        data.addString( refInputName, "<p><a href=\"" + format + contentId + "\">aaa</a></p>" );

        final CreateContentParams params = createContentParams( data.copy() );
        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, null );

        final ProcessCreateResult result = this.referenceContentProcessor.processCreate( processCreateParams );

        return result;
    }


    private CreateContentParams createContentParams( final PropertyTree data )
    {
        return CreateContentParams.create().
            parent( ContentPath.ROOT ).
            name( "myContent" ).
            contentData( data ).
            type( ContentTypeName.from( "test:testType" ) ).
            build();
    }

    private final Input.Builder getDefaultInputBuilder( final InputTypeName inputTypeName, final String inputName )
    {

        final InputTypeProperty defaultProperty = InputTypeProperty.create( "default", null ).build();

        final InputTypeDefault inputTypeDefault = InputTypeDefault.create().property( defaultProperty ).build();

        return Input.create().
            name( inputName ).
            label( "label" ).
            inputType( inputTypeName ).
            defaultValue( inputTypeDefault );

    }

}
