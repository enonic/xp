package com.enonic.xp.schema.content;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class ContentTypeTest
{

    @Test
    public void layout()
    {
        ContentType contentType = ContentType.create().superType( ContentTypeName.structured() ).name( "mymodule:test" ).build();
        FieldSet layout = FieldSet.create().
            label( "Personalia" ).
            name( "personalia" ).
            addFormItem( Input.create().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        contentType.form().addFormItem( layout );

        assertEquals( "eyeColour", contentType.form().getInput( "eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout_inside_formItemSet()
    {
        ContentType contentType = ContentType.create().name( "mymodule:test" ).superType( ContentTypeName.structured() ).build();

        FieldSet layout = FieldSet.create().
            label( "Personalia" ).
            name( "personalia" ).
            addFormItem( Input.create().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        FormItemSet myFormItemSet = FormItemSet.create().name( "mySet" ).addFormItem( layout ).build();
        contentType.form().addFormItem( myFormItemSet );

        assertEquals( "mySet.eyeColour", contentType.form().getInput( "mySet.eyeColour" ).getPath().toString() );
    }

    @Test
    public void address()
    {
        FormItemSet formItemSet = FormItemSet.create().name( "address" ).build();
        formItemSet.add( Input.create().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( Input.create().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( Input.create().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( Input.create().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() );

        ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "mymodule:test" ).
            addFormItem( Input.create().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( formItemSet ).
            build();

        assertEquals( "title", contentType.form().getInput( "title" ).getPath().toString() );
        assertEquals( "address.label", contentType.form().getInput( "address.label" ).getPath().toString() );
        assertEquals( "address.street", contentType.form().getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", contentType.form().getInput( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", contentType.form().getInput( "address.country" ).getPath().toString() );
    }

    @Test
    public void formItemSet_in_formItemSet()
    {
        FormItemSet formItemSet = FormItemSet.create().
            name( "top-set" ).
            addFormItem( Input.create().
                name( "myInput" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( FormItemSet.create().
                name( "inner-set" ).
                addFormItem( Input.create().
                    name( "myInnerInput" ).
                    inputType( InputTypes.TEXT_LINE ).
                    build() ).
                build() ).
            build();

        ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "mymodule:test" ).
            addFormItem( formItemSet ).
            build();

        assertEquals( "top-set", contentType.form().getFormItemSet( "top-set" ).getPath().toString() );
        assertEquals( "top-set.myInput", contentType.form().getInput( "top-set.myInput" ).getPath().toString() );
        assertEquals( "top-set.inner-set", contentType.form().getFormItemSet( "top-set.inner-set" ).getPath().toString() );
        assertEquals( "top-set.inner-set.myInnerInput",
                      contentType.form().getInput( "top-set.inner-set.myInnerInput" ).getPath().toString() );
    }

    @Test
    public void contentTypeBuilder()
    {
        ContentType.Builder builder = ContentType.create().name( ContentTypeName.media() ).form(
            ContentTypeForms.MEDIA_DEFAULT ).setAbstract().setFinal().allowChildContent( true ).setBuiltIn().contentDisplayNameScript(
            "contentDisplayNameScript" ).metadata( null ).displayName( "displayName" ).description( "description" ).modifiedTime(
            Instant.now() ).createdTime( Instant.now() ).creator( PrincipalKey.ofAnonymous() ).modifier( PrincipalKey.ofAnonymous() );
        ContentType contentType1 = builder.build();
        ContentType contentType2 = ContentType.create( contentType1 ).build();
        assertEquals( contentType1.getName(), contentType2.getName() );
        assertEquals( contentType1.form(), contentType2.form() );
        assertEquals( contentType1.isAbstract(), contentType2.isAbstract() );
        assertEquals( contentType1.isFinal(), contentType2.isFinal() );
        assertEquals( contentType1.allowChildContent(), contentType2.allowChildContent() );
        assertEquals( contentType1.isBuiltIn(), contentType2.isBuiltIn() );
        assertEquals( contentType1.getContentDisplayNameScript(), contentType2.getContentDisplayNameScript() );
        assertEquals( contentType1.getDisplayName(), contentType2.getDisplayName() );
        assertEquals( contentType1.getDescription(), contentType2.getDescription() );
        assertEquals( contentType1.getModifiedTime(), contentType2.getModifiedTime() );
        assertEquals( contentType1.getCreatedTime(), contentType2.getCreatedTime() );
        assertEquals( contentType1.getCreator(), contentType2.getModifier() );
        assertEquals( contentType1.toString(), contentType2.toString() );
    }

    @Test
    public void contentTypeFilter()
    {
        ContentTypeFilter.Builder builder = ContentTypeFilter.create().allowContentType( ContentTypeName.media() ).allowContentType(
                ContentTypeName.from( "mymodule:my_type" ) ).allowContentTypes(
                ContentTypeNames.from( ContentTypeName.archiveMedia() ) ).defaultDeny().denyContentType(
                ContentTypeName.audioMedia() ).denyContentTypes(
                ContentTypeNames.from( ContentTypeName.documentMedia() ) ).allowContentType( "mymodule:my_type1" ).denyContentType(
                "mymodule:my_type2" );
        ContentTypeFilter ctFilter = builder.build();
        ContentTypeFilter ctFilter1 = builder.build();
        assertTrue( ctFilter.isContentTypeAllowed( ContentTypeName.from( "mymodule:my_type" ) ) );
        assertTrue( ctFilter.isContentTypeAllowed( ContentTypeName.from( "mymodule:my_type1" ) ) );
        assertTrue( ctFilter.isContentTypeAllowed( ContentTypeName.archiveMedia() ) );
        assertFalse( ctFilter.isContentTypeAllowed( ContentTypeName.from( "mymodule:my_type2" ) ) );
        assertFalse( ctFilter.isContentTypeAllowed( ContentTypeName.audioMedia() ) );
        assertFalse( ctFilter.isContentTypeAllowed( ContentTypeName.documentMedia() ) );
        assertTrue( ctFilter.equals( ( ctFilter1 ) ) );
        assertFalse( ctFilter.equals( ( builder ) ) );
    }

    @Test
    public void getAllContentTypesParams()
    {
        GetAllContentTypesParams params = new GetAllContentTypesParams();
        params.inlineMixinsToFormItems( true );
        assertTrue( params.isInlineMixinsToFormItems() );
    }

    @Test
    public void getChildContentTypesParams()
    {
        GetChildContentTypesParams params1 = new GetChildContentTypesParams();
        params1.parentName( ContentTypeName.archiveMedia() );
        params1.validate();
        GetChildContentTypesParams params2 = new GetChildContentTypesParams();
        params2.parentName( ContentTypeName.archiveMedia() );
        assertEquals( params1.getParentName(), params2.getParentName() );
        assertEquals( params1, params2 );
        assertTrue( params1.hashCode() == params2.hashCode() );
        assertTrue( params1.equals( params1 ) );
        assertFalse( params1.equals( null ) );
    }

    @Test
    public void getContentTypeParams()
    {
        GetContentTypeParams params1 = GetContentTypeParams.from( ContentTypeName.archiveMedia() );
        GetContentTypeParams params2 = GetContentTypeParams.from( ContentTypeName.archiveMedia() );
        GetContentTypeParams params3 = GetContentTypeParams.from( ContentTypeName.archiveMedia() );
        params1.inlineMixinsToFormItems( true );
        params1.validate();
        assertFalse( params2.isInlineMixinsToFormItems() );
        assertFalse( params2.equals( params1 ) );
        assertFalse( params2.hashCode() == params1.hashCode() );
        assertTrue( params2.getContentTypeName().equals( params1.getContentTypeName() ) );
        assertEquals( params1, params1 );
        params1.contentTypeName( "test" );
        assertNotEquals( params1, params2 );
        assertEquals( params2, params3 );
    }

    @Test
    public void getContentTypesParams()
    {
        List<ContentTypeName> list = new ArrayList<ContentTypeName>()
        {{
                add( ContentTypeName.audioMedia() );
            }};
        GetContentTypesParams params1 = new GetContentTypesParams();
        params1.contentTypeNames( ContentTypeNames.create().add( ContentTypeName.archiveMedia() ).addAll( list ).build() );
        GetContentTypesParams params2 = new GetContentTypesParams();
        params2.contentTypeNames( ContentTypeNames.empty() );
        params2.inlineMixinsToFormItems( true );
        params1.validate();
        assertTrue( params1.getContentTypeNames().getSize() == 2 );
        assertFalse( params1.hashCode() == params2.hashCode() );
        assertEquals( params1, params1 );
        assertNotEquals( params1.isInlineMixinsToFormItems(), params2.isInlineMixinsToFormItems() );
        assertNotEquals( params1, null );
    }


}