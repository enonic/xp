package com.enonic.wem.api.schema.mixin;

import org.junit.Test;

import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.xml.BaseXmlSerializerTest;
import com.enonic.wem.api.xml.XmlSerializers;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static junit.framework.Assert.assertEquals;

public class MixinXmlTest
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

        final Mixin.Builder builder = newMixin().name( "mixin" );
        builder.displayName( "display name" ).description( "description" );

        builder.addFormItem( set );

        final Mixin mixin = builder.build();

        final MixinXml mixinXml = new MixinXml();
        mixinXml.from( mixin );
        final String result = XmlSerializers.mixin().serialize( mixinXml );

        assertXml( "mixin.xml", result );
    }

    @Test
    public void testFrom()
        throws Exception
    {
        final String xml = readFromFile( "mixin.xml" );
        final Mixin.Builder builder = newMixin();

        XmlSerializers.mixin().parse( xml ).to( builder );

        final Mixin mixin = builder.build();
        assertEquals( null, mixin.getName() );
        assertEquals( "display name", mixin.getDisplayName() );
        assertEquals( "description", mixin.getDescription() );

        assertEquals( 1, mixin.getFormItems().size() );

    }

}
