package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.util.Reference;
import com.enonic.xp.form.inputtype.ImageSelector;

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
