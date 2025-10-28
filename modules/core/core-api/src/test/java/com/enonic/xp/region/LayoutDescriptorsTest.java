package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LayoutDescriptorsTest
{
    @Test
    void empty()
    {
        assertTrue( LayoutDescriptors.empty().isEmpty() );
    }
}
