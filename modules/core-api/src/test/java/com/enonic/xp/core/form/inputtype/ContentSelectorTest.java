package com.enonic.xp.core.form.inputtype;

import org.junit.Test;

import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.util.Reference;

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
