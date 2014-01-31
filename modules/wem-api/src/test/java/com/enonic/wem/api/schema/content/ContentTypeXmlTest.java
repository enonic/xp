package com.enonic.wem.api.schema.content;

import org.junit.Test;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;

public class ContentTypeXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testTo()
        throws Exception
    {
        final FormItemSet set = newFormItemSet().name( "mySet" ).build();
        final Layout layout = FieldSet.newFieldSet().label( "My field set" ).name( "myFieldSet" ).addFormItem(
            newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        set.add( layout );

        final ContentType.Builder contentTypeBuilder = newContentType().name( "all_schemas" );
        contentTypeBuilder.addFormItem( set );
        contentTypeBuilder.displayName( "All the Base Types" );
        contentTypeBuilder.contentDisplayNameScript( "$('firstName') + ' ' + $('lastName')" );
        contentTypeBuilder.superType( ContentTypeName.from( "content" ) );
        contentTypeBuilder.setAbstract( false );
        contentTypeBuilder.setFinal( true );

        final ContentType contentType = contentTypeBuilder.build();

        final ContentTypeXml siteTemplateXml = new ContentTypeXml();
        siteTemplateXml.from( contentType );
        final String result = XmlSerializers.contentType().serialize( siteTemplateXml );

        assertXml( "contentType.xml", result );
    }

    @Test
    public void testFrom()
        throws Exception
    {
        final String xml = readFromFile( "contentType.xml" );
        final ContentType.Builder builder = ContentType.newContentType();

        XmlSerializers.contentType().parse( xml ).to( builder );

        final ContentType contentType = builder.build();
        assertEquals(null, contentType.getName());
        assertEquals("All the Base Types", contentType.getDisplayName());
        assertEquals("$('firstName') + ' ' + $('lastName')", contentType.getContentDisplayNameScript());
        assertEquals("content", contentType.getSuperType().toString());
        assertEquals(false, contentType.isAbstract());
        assertEquals(true, contentType.isFinal());

    }

}
