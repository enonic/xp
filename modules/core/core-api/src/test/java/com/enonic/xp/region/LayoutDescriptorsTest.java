package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LayoutDescriptorsTest
{
    @Test
    public void empty()
    {
        assertTrue( LayoutDescriptors.empty().isEmpty() );
    }
}
