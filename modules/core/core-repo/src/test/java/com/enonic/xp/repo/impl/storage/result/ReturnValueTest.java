package com.enonic.xp.repo.impl.storage.result;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.repo.impl.ReturnValue;

import static org.junit.Assert.*;

public class ReturnValueTest
{
    @Test
    public void addList()
        throws Exception
    {
        List<String> values = Lists.newArrayList();
        values.add( "a" );
        values.add( "b" );
        values.add( "c" );

        final ReturnValue returnValue = ReturnValue.create( values );

        assertEquals( 3, returnValue.getValues().size() );
        assertEquals( "a", returnValue.getSingleValue() );
    }
}