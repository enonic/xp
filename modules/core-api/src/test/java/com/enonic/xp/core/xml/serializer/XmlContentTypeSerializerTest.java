package com.enonic.xp.core.xml.serializer;

import org.junit.Test;

import com.enonic.xp.core.form.FieldSet;
import com.enonic.xp.core.form.FormItemSet;
import com.enonic.xp.core.form.Layout;
import com.enonic.xp.core.form.inputtype.InputTypes;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.schema.content.ContentType;
import com.enonic.xp.core.schema.content.ContentTypeName;
import com.enonic.xp.core.schema.mixin.MixinNames;
import com.enonic.xp.core.xml.mapper.XmlContentTypeMapper;
import com.enonic.xp.core.xml.model.XmlContentType;

import static com.enonic.xp.core.form.FormItemSet.newFormItemSet;
import static com.enonic.xp.core.form.Input.newInput;
import static com.enonic.xp.core.schema.content.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;

public class XmlContentTypeSerializerTest
    extends BaseXmlSerializerTest
{
    private final static ModuleKey CURRENT_MODULE = ModuleKey.from( "mymodule" );

    @Test
    public void test_to_xml()
        throws Exception
    {
        final FormItemSet set = newFormItemSet().name( "mySet" ).build();
        final Layout layout = FieldSet.newFieldSet().label( "My field set" ).name( "myFieldSet" ).addFormItem(
            newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        set.add( layout );

        final ContentType.Builder contentTypeBuilder =
            newContentType().name( "mymodule:all_schemas" ).addFormItem( set ).displayName( "All the Base Types" ).
                metadata( MixinNames.from( "mymodule:metadata" ) ).description( "description" ).contentDisplayNameScript(
                "$('firstName') + ' ' + $('lastName')" ).superType( ContentTypeName.from( "mymodule:content" ) ).setAbstract(
                false ).setFinal( true );

        final ContentType contentType = contentTypeBuilder.build();

        final XmlContentType xml = new XmlContentTypeMapper( CURRENT_MODULE ).toXml( contentType );
        final String result = XmlSerializers.contentType().serialize( xml );

        assertXml( "content-type-to.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "content-type-from.xml" );
        final ContentType.Builder builder = newContentType();
        builder.name( "mymodule:content-type" );

        final XmlContentType xmlObject = XmlSerializers.contentType().parse( xml );
        new XmlContentTypeMapper( CURRENT_MODULE ).fromXml( xmlObject, builder );

        final ContentType contentType = builder.build();
        assertEquals( "mymodule:content-type", contentType.getName().toString() );
        assertEquals( "All the Base Types", contentType.getDisplayName() );
        assertEquals( "description", contentType.getDescription() );
        assertEquals( "$('firstName') + ' ' + $('lastName')", contentType.getContentDisplayNameScript() );
        assertEquals( "mymodule:content", contentType.getSuperType().toString() );
        assertEquals( "[mymodule:metadata]", contentType.getMetadata().toString() );
        assertEquals( false, contentType.isAbstract() );
        assertEquals( true, contentType.isFinal() );

    }
}
