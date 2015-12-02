package com.enonic.xp.region;

import org.junit.Test;

import static org.junit.Assert.*;

public class LayoutDescriptorsTest
{
    @Test
    public void empty()
    {
        assertTrue( LayoutDescriptors.empty().isEmpty() );
    }
}
