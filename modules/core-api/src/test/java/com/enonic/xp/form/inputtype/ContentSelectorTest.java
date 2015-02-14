package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.util.Reference;
import com.enonic.xp.form.inputtype.ContentSelector;

import static junit.framework.Assert.assertEquals;


public class ContentSelectorTest
{
    @Test
    public void newValue()
    {
        Value value = new ContentSelector().newValue( "ABC" );
        assertEquals( Reference.from( "ABC" ), value.asReference() );
    }
}
