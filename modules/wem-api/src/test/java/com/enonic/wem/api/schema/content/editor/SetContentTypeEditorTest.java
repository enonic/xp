package com.enonic.wem.api.schema.content.editor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class SetContentTypeEditorTest
{
    private static final Instant TIME1 = LocalDateTime.of( 2013, 1, 1, 12, 0 ).toInstant( ZoneOffset.UTC );

    private static final Instant TIME2 = LocalDateTime.of( 2013, 1, 1, 12, 5 ).toInstant( ZoneOffset.UTC );

    @Test
    public void testSetContentTypeEditor_no_changes()
        throws Exception
    {
        final FormItemSet formItemSet = newFormItemSet().name( "address" ).build();
        formItemSet.add( newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() );

        final ContentType contentType = newContentType().
            name( "mymodule-1.0.0:test" ).
            displayName( "test" ).
            contentDisplayNameScript( "myScript()" ).
            superType( ContentTypeName.unstructured() ).
            setAbstract( true ).
            setFinal( false ).
            createdTime( TIME1 ).
            modifiedTime( TIME2 ).
            addFormItem( newInput().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( formItemSet ).
            icon( Icon.from( "ABC".getBytes(), "image/png", TIME1 ) ).
            build();

        final SetContentTypeEditor.Builder editorBuilder = SetContentTypeEditor.newSetContentTypeEditor();
        editorBuilder.displayName( "test" );
        editorBuilder.setAbstract( true );
        editorBuilder.setFinal( false );
        editorBuilder.superType( ContentTypeName.unstructured() );
        editorBuilder.icon( Icon.from( "ABC".getBytes(), "image/png", TIME1 ) );
        editorBuilder.contentDisplayNameScript( "myScript()" );

        SetContentTypeEditor editor = editorBuilder.build();
        ContentType result = editor.edit( contentType );
        assertNull( result );
    }

    @Test
    public void testSetContentTypeEditor_update()
        throws Exception
    {
        final FormItemSet formItemSet = newFormItemSet().name( "address" ).build();
        formItemSet.add( newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() );

        final ContentType contentType = newContentType().
            name( "mymodule-1.0.0:test" ).
            contentDisplayNameScript( "myScript()" ).
            superType( ContentTypeName.unstructured() ).
            setAbstract( true ).
            setFinal( true ).
            createdTime( TIME1 ).
            modifiedTime( TIME2 ).
            addFormItem( newInput().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final SetContentTypeEditor.Builder editorBuilder = SetContentTypeEditor.newSetContentTypeEditor();
        editorBuilder.displayName( "NEW NAME" );
        editorBuilder.setAbstract( false );
        editorBuilder.setFinal( false );
        editorBuilder.superType( ContentTypeName.structured() );
        editorBuilder.icon( Icon.from( "ABC".getBytes(), "image/png", TIME1 ) );
        editorBuilder.contentDisplayNameScript( "newScript()" );
        final Form form = Form.newForm().addFormItem( formItemSet ).build();
        editorBuilder.form( form );

        SetContentTypeEditor editor = editorBuilder.build();
        ContentType result = editor.edit( contentType );
        assertNotNull( result );
        assertEquals( "NEW NAME", result.getDisplayName() );
        assertEquals( false, result.isFinal() );
        assertEquals( false, result.isAbstract() );
        assertEquals( TIME1, result.getCreatedTime() );
        assertEquals( TIME2, result.getModifiedTime() );
        assertEquals( "newScript()", result.getContentDisplayNameScript() );
        assertEquals( ContentTypeName.structured(), result.getSuperType() );
        assertEquals( Icon.from( "ABC".getBytes(), "image/png", TIME1 ), result.getIcon() );
        assertEquals( form.toString(), result.form().toString() );
    }
}
