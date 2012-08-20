package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import static org.junit.Assert.*;

public class OccurrencesTest
{
    @Test
    public void required()
    {
        assertEquals( false, new Occurrences( 0, 0 ).impliesRequired() );
        assertEquals( true, new Occurrences( 1, 0 ).impliesRequired() );
        assertEquals( true, new Occurrences( 2, 0 ).impliesRequired() );
    }

    @Test
    public void multiple()
    {
        assertEquals( true, new Occurrences( 0, 0 ).isMultiple() );
        assertEquals( false, new Occurrences( 0, 1 ).isMultiple() );
        assertEquals( true, new Occurrences( 0, 2 ).isMultiple() );
    }

    @Test
    public void unlimited()
    {
        assertEquals( true, new Occurrences( 0, 0 ).isUnlimited() );
        assertEquals( false, new Occurrences( 0, 1 ).isUnlimited() );
        assertEquals( false, new Occurrences( 0, 2 ).isUnlimited() );
    }
}
