package com.enonic.xp.xml.serializer;

import org.junit.Test;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Layout;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.xml.mapper.XmlMixinMapper;
import com.enonic.xp.xml.model.XmlMixin;

import static com.enonic.xp.form.FormItemSet.newFormItemSet;
import static com.enonic.xp.form.Input.newInput;
import static com.enonic.xp.schema.mixin.Mixin.newMixin;
import static junit.framework.Assert.assertEquals;

public class XmlMixinSerializerTest
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

        final Mixin.Builder builder = newMixin().name( "mymodule:mixin" );
        builder.displayName( "display name" ).description( "description" );

        builder.addFormItem( set );

        final Mixin mixin = builder.build();

        final XmlMixin xml = new XmlMixinMapper( CURRENT_MODULE ).toXml( mixin );
        final String result = XmlSerializers.mixin().serialize( xml );

        assertXml( "mixin.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "mixin.xml" );
        final Mixin.Builder builder = newMixin();

        final XmlMixin xmlObject = XmlSerializers.mixin().parse( xml );
        new XmlMixinMapper( CURRENT_MODULE ).fromXml( xmlObject, builder );

        builder.name( "mymodule:mymixin" );
        final Mixin mixin = builder.build();
        assertEquals( "mymodule:mymixin", mixin.getName().toString() );
        assertEquals( "display name", mixin.getDisplayName() );
        assertEquals( "description", mixin.getDescription() );

        assertEquals( 1, mixin.getFormItems().size() );
    }

}
