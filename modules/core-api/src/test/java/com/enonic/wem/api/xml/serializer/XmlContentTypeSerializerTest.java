package com.enonic.wem.api.xml.serializer;

import org.junit.Test;

import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.xml.mapper.XmlContentTypeMapper;
import com.enonic.wem.api.xml.model.XmlContentType;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;

public class XmlContentTypeSerializerTest
    extends BaseXmlSerializerTest
{
    @Test
    public void test_to_xml()
        throws Exception
    {
        final FormItemSet set = newFormItemSet().name( "mySet" ).build();
        final Layout layout = FieldSet.newFieldSet().label( "My field set" ).name( "myFieldSet" ).addFormItem(
            newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        set.add( layout );

        final ContentType.Builder contentTypeBuilder =
            newContentType().name( "mymodule:all_schemas" ).addFormItem( set ).displayName( "All the Base Types" ).description(
                "description" ).contentDisplayNameScript( "$('firstName') + ' ' + $('lastName')" ).superType(
                ContentTypeName.from( "mymodule:content" ) ).setAbstract( false ).setFinal( true );

        final ContentType contentType = contentTypeBuilder.build();

        final XmlContentType xml = XmlContentTypeMapper.toXml( contentType );
        final String result = XmlSerializers.contentType().serialize( xml );

        assertXml( "content-type.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "content-type.xml" );
        final ContentType.Builder builder = newContentType();
        builder.name( "mymodule:content-type" );

        final XmlContentType xmlObject = XmlSerializers.contentType().parse( xml );
        XmlContentTypeMapper.fromXml( xmlObject, builder );

        final ContentType contentType = builder.build();
        assertEquals( "mymodule:content-type", contentType.getName().toString() );
        assertEquals( "All the Base Types", contentType.getDisplayName() );
        assertEquals( "description", contentType.getDescription() );
        assertEquals( "$('firstName') + ' ' + $('lastName')", contentType.getContentDisplayNameScript() );
        assertEquals( "mymodule:content", contentType.getSuperType().toString() );
        assertEquals( false, contentType.isAbstract() );
        assertEquals( true, contentType.isFinal() );

    }


}
