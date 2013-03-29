package com.enonic.wem.api.content.schema.content.form.inputtype;

import org.junit.Test;

import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.content.data.Value;

import static junit.framework.Assert.assertEquals;


public class EmbeddedImageTest
{
    @Test
    public void newValue()
    {
        Value value = new EmbeddedImage().newValue( "ABC" );
        assertEquals( BinaryId.from( "ABC" ), value.asBinaryId() );
    }
}
