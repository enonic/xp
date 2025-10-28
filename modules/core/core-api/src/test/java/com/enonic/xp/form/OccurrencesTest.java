package com.enonic.xp.form;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OccurrencesTest
{
    @Test
    void required()
    {
        assertEquals( false, Occurrences.create( 0, 0 ).impliesRequired() );
        assertEquals( true, Occurrences.create( 1, 0 ).impliesRequired() );
        assertEquals( true, Occurrences.create( 2, 0 ).impliesRequired() );
    }

    @Test
    void multiple()
    {
        assertEquals( true, Occurrences.create( 0, 0 ).isMultiple() );
        assertEquals( false, Occurrences.create( 0, 1 ).isMultiple() );
        assertEquals( true, Occurrences.create( 0, 2 ).isMultiple() );
    }

    @Test
    void unlimited()
    {
        assertEquals( true, Occurrences.create( 0, 0 ).isUnlimited() );
        assertEquals( false, Occurrences.create( 0, 1 ).isUnlimited() );
        assertEquals( false, Occurrences.create( 0, 2 ).isUnlimited() );
    }
}
