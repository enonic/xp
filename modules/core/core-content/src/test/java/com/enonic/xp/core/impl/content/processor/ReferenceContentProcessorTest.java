package com.enonic.xp.core.impl.content.processor;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
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
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ReferenceContentProcessorTest
{
    private final ReferenceContentProcessor processor = new ReferenceContentProcessor();

    private Parser<ContentIds> parser;

    private ContentTypeService contentTypeService;

    private MixinService mixinService;


    @Before
    public void setUp()
        throws Exception
    {
        this.parser = new ContentIdsHtmlParser();
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.mixinService = Mockito.mock( MixinService.class );

        processor.setContentTypeService( this.contentTypeService );
        processor.setParser( this.parser );
        processor.setMixinService( this.mixinService );
    }

    @Test
    public void testProcessCreate_empty()
    {
        final PropertyTree data = new PropertyTree();

        final CreateContentParams params = createContentParams( data );

        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, null );

        Form form = Form.create().build();
        Mockito.when( this.mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).
            thenReturn( form );

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            form( form ).
            build();

        Mockito.when( this.contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

        final ProcessCreateResult result = this.processor.processCreate( processCreateParams );

        assertEquals( result.getCreateContentParams().getData(), data );
    }

    @Test
    public void testProcessCreate_one_ref()

    {
        final PropertyTree data = new PropertyTree();
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        Form form = Form.create().addFormItem( input ).build();
        Mockito.when( this.mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).
            thenReturn( form );

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
        Mockito.when( this.mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).
            thenReturn( form );

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

        final ProcessCreateResult result = this.processor.processCreate( processCreateParams );

        data.getSet( "testSet" ).addReference( "_" + input.getName() + "_references", Reference.from( "aaa" ) );
        assertEquals( result.getCreateContentParams().getData(), data );
    }

    @Test
    public void testProcessCreate_one_download_ref()

    {
        final PropertyTree data = new PropertyTree();
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        Form form = Form.create().addFormItem( input ).build();
        Mockito.when( this.mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).
            thenReturn( form );

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
        Mockito.when( this.mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).
            thenReturn( form );

        final String value = "<img src=\"image://a3cc4a32-a278-4fc6-bc9b-4e8b337003dd\" \n" +
            "alt=\"Renault4_R01.jpg\" style=\"text-align: right; width: 100%;\"/>";

        final ProcessCreateResult result = processCreate_one_ref( data, form, input.getName(), "aContent", value );

        data.addReference( "_inputName_references", Reference.from( "a3cc4a32-a278-4fc6-bc9b-4e8b337003dd" ) );

        assertEquals( result.getCreateContentParams().getData(), data );
    }

    @Test
    public void testProcessCreate_one_image_ref_with_image_parameters()

    {
        final PropertyTree data = new PropertyTree();
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        Form form = Form.create().addFormItem( input ).build();
        Mockito.when( this.mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).
            thenReturn( form );

        final String value = "<img src=\"image://a3cc4a32-a278-4fc6-bc9b-4e8b337003dd?scale=21:9&amp;size=640\" \n" +
            "alt=\"Renault4_R01.jpg\" style=\"text-align: right; width: 100%;\"/>";

        final ProcessCreateResult result = processCreate_one_ref( data, form, input.getName(), "aContent", value );

        data.addReference( "_inputName_references", Reference.from( "a3cc4a32-a278-4fc6-bc9b-4e8b337003dd" ) );

        assertEquals( result.getCreateContentParams().getData(), data );
    }

    @Test
    public void all_links()
        throws Exception
    {

        final PropertyTree data = new PropertyTree();
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        Form form = Form.create().addFormItem( input ).build();
        Mockito.when( this.mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).
            thenReturn( form );

        final String withAll =
            "<p>Here is link to content:&nbsp;<a href=\"content://c30febe4-d1e0-46b9-915e-60e361021829\" target=\"_blank\" title=\"fisk\">Content link sir</a></p>\\n<p>Another link to download here&nbsp;<a href=\"media://download/3d8dc241-1a52-48f6-9a27-cde26226bd63\">Fisk </a></p>\\n<p>Link to image also &lt;b&gt;</p>\\n<figure class=\"justify\"><img src=\"image://0d7dcd8b-bde8-4091-a222-51e85f17f20f\" alt=\"An_Afghan_elder_and_his_cat_sit_outside_his_store.jpg\" style=\"text-align: justify; width: 100%;\" /><figcaption style=\"text-align: left;\">ewesfsef</figcaption></figure>";

        final ProcessCreateResult result = processCreate_one_ref( data, form, input.getName(), "aContent", withAll );

        final Iterable<Reference> references = result.getCreateContentParams().getData().getReferences( "_inputName_references" );
        final ArrayList<Reference> referenceList = Lists.newArrayList( references );
        assertEquals( 3, referenceList.size() );
        assertTrue( referenceList.contains( Reference.from( "c30febe4-d1e0-46b9-915e-60e361021829" ) ) );
        assertTrue( referenceList.contains( Reference.from( "3d8dc241-1a52-48f6-9a27-cde26226bd63" ) ) );
        assertTrue( referenceList.contains( Reference.from( "0d7dcd8b-bde8-4091-a222-51e85f17f20f" ) ) );
    }

    @Test
    public void testProcessUpdate_one_ref_add()
    {
        final PropertyTree data = new PropertyTree();
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "inputName" ).build();
        Form form = Form.create().addFormItem( input ).build();
        Mockito.when( this.mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).
            thenReturn( form );

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

        final ProcessUpdateResult updateResult = this.processor.processUpdate( processUpdateParams );

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

    private void mockMixinWithHtmlArea()
    {
        final Input htmlInput1 = Input.create().name( "myHtmlArea" ).label( "HtmlArea" ).inputType( InputTypeName.HTML_AREA ).build();

        final FormItemSet myOuterSet =
            FormItemSet.create().name( "myOuterSet" ).label( "Label" ).multiple( true ).addFormItem( htmlInput1 ).build();

        final Form mixinForm = Form.create().
            addFormItem( myOuterSet ).
            build();
        Mockito.when( this.mixinService.getByName( Mockito.isA( MixinName.class ) ) ).
            thenReturn( Mixin.create().
                form( mixinForm ).
                displayName( "My mixin" ).
                build() );
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

        return this.processor.processCreate( processCreateParams );
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
