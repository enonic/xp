package com.enonic.xp.form;


import org.junit.Test;

import static org.junit.Assert.*;

public class OccurrencesTest
{
    @Test
    public void required()
    {
        assertEquals( false, Occurrences.create().minimum( 0 ).maximum( 0 ).build().impliesRequired() );
        assertEquals( true, Occurrences.create().minimum( 1 ).maximum( 0 ).build().impliesRequired() );
        assertEquals( true, Occurrences.create().minimum( 2 ).maximum( 0 ).build().impliesRequired() );
    }

    @Test
    public void multiple()
    {
        assertEquals( true, Occurrences.create().minimum( 0 ).maximum( 0 ).build().isMultiple() );
        assertEquals( false, Occurrences.create().minimum( 0 ).maximum( 1 ).build().isMultiple() );
        assertEquals( true, Occurrences.create().minimum( 0 ).maximum( 2 ).build().isMultiple() );
    }

    @Test
    public void unlimited()
    {
        assertEquals( true, Occurrences.create().minimum( 0 ).maximum( 0 ).build().isUnlimited() );
        assertEquals( false, Occurrences.create().minimum( 0 ).maximum( 1 ).build().isUnlimited() );
        assertEquals( false, Occurrences.create().minimum( 0 ).maximum( 2 ).build().isUnlimited() );
    }
}
