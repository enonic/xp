package com.enonic.wem.api.form;


import org.junit.Test;

import static com.enonic.wem.api.form.Occurrences.newOccurrences;
import static org.junit.Assert.*;

public class OccurrencesTest
{
    @Test
    public void required()
    {
        assertEquals( false, newOccurrences().minimum( 0 ).maximum( 0 ).build().impliesRequired() );
        assertEquals( true, newOccurrences().minimum( 1 ).maximum( 0 ).build().impliesRequired() );
        assertEquals( true, newOccurrences().minimum( 2 ).maximum( 0 ).build().impliesRequired() );
    }

    @Test
    public void multiple()
    {
        assertEquals( true, newOccurrences().minimum( 0 ).maximum( 0 ).build().isMultiple() );
        assertEquals( false, newOccurrences().minimum( 0 ).maximum( 1 ).build().isMultiple() );
        assertEquals( true, newOccurrences().minimum( 0 ).maximum( 2 ).build().isMultiple() );
    }

    @Test
    public void unlimited()
    {
        assertEquals( true, newOccurrences().minimum( 0 ).maximum( 0 ).build().isUnlimited() );
        assertEquals( false, newOccurrences().minimum( 0 ).maximum( 1 ).build().isUnlimited() );
        assertEquals( false, newOccurrences().minimum( 0 ).maximum( 2 ).build().isUnlimited() );
    }
}
