package com.enonic.xp.repo.impl.storage.result;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.ReturnValue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnValueTest
{
    @Test
    public void addList()
        throws Exception
    {
        List<String> values = new ArrayList<>();
        values.add( "a" );
        values.add( "b" );
        values.add( "c" );

        final ReturnValue returnValue = ReturnValue.create( values );

        assertEquals( 3, returnValue.getValues().size() );
        assertEquals( "a", returnValue.getSingleValue() );
    }
}
