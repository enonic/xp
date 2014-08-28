package com.enonic.wem.api.form;

import org.junit.Test;

import com.enonic.wem.api.xml.BaseXmlSerializerTest;
import com.enonic.wem.api.xml.XmlSerializers;

import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static junit.framework.Assert.assertEquals;

public class MixinReferenceXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        final MixinReference mixinReference = newMixinReference().
            name( "mixin" ).
            mixin( "mymodule-1.0.0:reference" ).
            build();

        final MixinReferenceXml mixinReferenceXml = new MixinReferenceXml();

        mixinReferenceXml.from( mixinReference );

        final String result = XmlSerializers.mixinReference().serialize( mixinReferenceXml );

        assertXml( "mixinReference.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "mixinReference.xml" );
        final MixinReference.Builder builder = MixinReference.newMixinReference();

        XmlSerializers.mixinReference().parse( xml ).to( builder );

        final MixinReference mixinReference = builder.build();

        assertEquals( "mixin", mixinReference.getName() );
        assertEquals( "mymodule-1.0.0:reference", mixinReference.getMixinName().toString() );
    }
}
