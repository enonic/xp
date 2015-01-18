package com.enonic.wem.api.form.inputtype;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.util.Reference;

import static junit.framework.Assert.assertEquals;


public class ImageSelectorTest
{
    @Test
    public void newValue()
    {
        Value value = new ImageSelector().newValue( "ABC" );
        assertEquals( Reference.from( "ABC" ), value.asReference() );
    }
}
