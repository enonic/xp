package com.enonic.xp.form;


import org.junit.Test;

import static org.junit.Assert.*;

public class OccurrencesTest
{
    @Test
    public void required()
    {
        assertEquals( false, Occurrences.create( 0, 0 ).impliesRequired() );
        assertEquals( true, Occurrences.create( 1, 0 ).impliesRequired() );
        assertEquals( true, Occurrences.create( 2, 0 ).impliesRequired() );
    }

    @Test
    public void multiple()
    {
        assertEquals( true, Occurrences.create( 0, 0 ).isMultiple() );
        assertEquals( false, Occurrences.create( 0, 1 ).isMultiple() );
        assertEquals( true, Occurrences.create( 0, 2 ).isMultiple() );
    }

    @Test
    public void unlimited()
    {
        assertEquals( true, Occurrences.create( 0, 0 ).isUnlimited() );
        assertEquals( false, Occurrences.create( 0, 1 ).isUnlimited() );
        assertEquals( false, Occurrences.create( 0, 2 ).isUnlimited() );
    }
}
