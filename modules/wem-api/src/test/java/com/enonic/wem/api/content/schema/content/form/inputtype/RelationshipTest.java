package com.enonic.wem.api.content.schema.content.form.inputtype;

import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.Value;

import static junit.framework.Assert.assertEquals;


public class RelationshipTest
{
    @Test
    public void newValue()
    {
        Value value = new Relationship().newValue( "ABC" );
        assertEquals( ContentId.from( "ABC" ), value.asContentId() );
    }
}
