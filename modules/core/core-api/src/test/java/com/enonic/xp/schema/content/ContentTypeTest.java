package com.enonic.xp.schema.content;


import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTypeTest
{
    private static final Form MEDIA_DEFAULT = Form.create().
        addFormItem( Input.create().name( ContentPropertyNames.MEDIA ).
            label( "Media" ).
            inputType( InputTypeName.MEDIA_UPLOADER ).build() ).
        build();

    @Test
    public void layout()
    {
        ContentType contentType = ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test" ).build();
        FieldSet layout = FieldSet.create().
            label( "Personalia" ).
            name( "personalia" ).
            addFormItem( Input.create().name( "eyeColour" ).label( "Eye color" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();

        contentType.getForm().getFormItems().add( layout );

        assertEquals( "eyeColour", contentType.getForm().getInput( "eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout_inside_formItemSet()
    {
        ContentType contentType = ContentType.create().name( "myapplication:test" ).superType( ContentTypeName.structured() ).build();

        FieldSet layout = FieldSet.create().
            label( "Personalia" ).
            name( "personalia" ).
            addFormItem( Input.create().name( "eyeColour" ).label( "Eye color" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();

        FormItemSet myFormItemSet = FormItemSet.create().name( "mySet" ).addFormItem( layout ).build();
        contentType.getForm().getFormItems().add( myFormItemSet );

        assertEquals( "mySet.eyeColour", contentType.getForm().getInput( "mySet.eyeColour" ).getPath().toString() );
    }

    @Test
    public void address()
    {
        FormItemSet formItemSet = FormItemSet.create().name( "address" ).build();
        formItemSet.add( Input.create().name( "label" ).label( "Label" ).inputType( InputTypeName.TEXT_LINE ).build() );
        formItemSet.add( Input.create().name( "street" ).label( "Street" ).inputType( InputTypeName.TEXT_LINE ).build() );
        formItemSet.add( Input.create().name( "postalNo" ).label( "Postal No" ).inputType( InputTypeName.TEXT_LINE ).build() );
        formItemSet.add( Input.create().name( "country" ).label( "Country" ).inputType( InputTypeName.TEXT_LINE ).build() );

        ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:test" ).
            addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( formItemSet ).
            build();

        assertEquals( "title", contentType.getForm().getInput( "title" ).getPath().toString() );
        assertEquals( "address.label", contentType.getForm().getInput( "address.label" ).getPath().toString() );
        assertEquals( "address.street", contentType.getForm().getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", contentType.getForm().getInput( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", contentType.getForm().getInput( "address.country" ).getPath().toString() );
    }

    @Test
    public void formItemSet_in_formItemSet()
    {
        FormItemSet formItemSet = FormItemSet.create().
            name( "top-set" ).
            addFormItem( Input.create().
                name( "myInput" ).
                label( "Input" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( FormItemSet.create().
                name( "inner-set" ).
                addFormItem( Input.create().
                    name( "myInnerInput" ).
                    label( "Inner input" ).
                    inputType( InputTypeName.TEXT_LINE ).
                    build() ).
                build() ).
            build();

        ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:test" ).
            addFormItem( formItemSet ).
            build();

        assertEquals( "top-set", contentType.getForm().getFormItemSet( "top-set" ).getPath().toString() );
        assertEquals( "top-set.myInput", contentType.getForm().getInput( "top-set.myInput" ).getPath().toString() );
        assertEquals( "top-set.inner-set", contentType.getForm().getFormItemSet( "top-set.inner-set" ).getPath().toString() );
        assertEquals( "top-set.inner-set.myInnerInput",
                      contentType.getForm().getInput( "top-set.inner-set.myInnerInput" ).getPath().toString() );
    }

    @Test
    public void contentTypeBuilder()
    {
        ContentType.Builder builder =
            ContentType.
                create().
                name( ContentTypeName.media() ).
                form( MEDIA_DEFAULT ).
                setAbstract().
                setFinal().
                allowChildContent( true ).
                setBuiltIn().
                displayNameExpression( "displayNameExpression" ).
                displayName( "displayName" ).
                displayNameLabel( "displayNameLabel" ).
                displayNameLabelI18nKey( "displayNameLabelI18nKey" ).
                description( "description" ).
                modifiedTime( Instant.now() ).
                createdTime( Instant.now() ).
                creator( PrincipalKey.ofAnonymous() ).
                modifier( PrincipalKey.ofAnonymous() );
        ContentType contentType1 = builder.build();
        ContentType contentType2 = ContentType.create( contentType1 ).build();
        assertEquals( contentType1.getName(), contentType2.getName() );
        assertEquals( contentType1.getForm(), contentType2.getForm() );
        assertEquals( contentType1.isAbstract(), contentType2.isAbstract() );
        assertEquals( contentType1.isFinal(), contentType2.isFinal() );
        assertEquals( contentType1.allowChildContent(), contentType2.allowChildContent() );
        assertEquals( contentType1.isBuiltIn(), contentType2.isBuiltIn() );
        assertEquals( contentType1.getDisplayNameExpression(), contentType2.getDisplayNameExpression() );
        assertEquals( contentType1.getDisplayName(), contentType2.getDisplayName() );
        assertEquals( contentType1.getDisplayNameLabel(), contentType2.getDisplayNameLabel() );
        assertEquals( contentType1.getDisplayNameLabelI18nKey(), contentType2.getDisplayNameLabelI18nKey() );
        assertEquals( contentType1.getDescription(), contentType2.getDescription() );
        assertEquals( contentType1.getModifiedTime(), contentType2.getModifiedTime() );
        assertEquals( contentType1.getCreatedTime(), contentType2.getCreatedTime() );
        assertEquals( contentType1.getCreator(), contentType2.getModifier() );
        assertEquals( contentType1.toString(), contentType2.toString() );
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
    }
}
