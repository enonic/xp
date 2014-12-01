package com.enonic.wem.api.convert;

public class ByteConverterTest
    extends NumberConverterTest<Byte>
{
    public ByteConverterTest()
    {
        super( Byte.class, (byte) 11 );
    }
}
