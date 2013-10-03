package com.enonic.wem.api.schema.content.editor;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.enonic.wem.api.Icon;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.form.Form;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static org.junit.Assert.*;

public class SetContentTypeEditorTest
{
    private static final DateTime TIME1 = new DateTime( 2013, 1, 1, 12, 0, DateTimeZone.UTC );

    private static final DateTime TIME2 = new DateTime( 2013, 1, 1, 12, 5, DateTimeZone.UTC );

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
            name( "test" ).
            displayName( "test" ).
            contentDisplayNameScript( "myScript()" ).
            superType( QualifiedContentTypeName.unstructured() ).
            setAbstract( true ).
            setFinal( false ).
            createdTime( TIME1 ).
            modifiedTime( TIME2 ).
            addFormItem( newInput().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( formItemSet ).
            icon( Icon.from( "imgdata".getBytes(), "image/png" ) ).
            build();

        final SetContentTypeEditor.Builder editorBuilder = SetContentTypeEditor.newSetContentTypeEditor();
        editorBuilder.displayName( "test" );
        editorBuilder.setAbstract( true );
        editorBuilder.setFinal( false );
        editorBuilder.superType( QualifiedContentTypeName.unstructured() );
        editorBuilder.icon( Icon.from( "imgdata".getBytes(), "image/png" ) );
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
            name( "test" ).
            contentDisplayNameScript( "myScript()" ).
            superType( QualifiedContentTypeName.unstructured() ).
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
        editorBuilder.superType( QualifiedContentTypeName.structured() );
        editorBuilder.icon( Icon.from( "imgdata".getBytes(), "image/png" ) );
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
        assertEquals( QualifiedContentTypeName.structured(), result.getSuperType() );
        assertEquals( Icon.from( "imgdata".getBytes(), "image/png" ), result.getIcon() );
        assertEquals( form.toString(), result.form().toString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetContentTypeEditor_noFieldsSet()
        throws Exception
    {
        SetContentTypeEditor editor = SetContentTypeEditor.newSetContentTypeEditor().build();
    }

}
