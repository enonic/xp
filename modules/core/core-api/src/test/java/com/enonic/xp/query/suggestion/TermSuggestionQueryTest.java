package com.enonic.xp.query.suggestion;

import org.junit.Test;

import com.enonic.xp.query.suggester.TermSuggestionQuery;

import static org.junit.Assert.*;

public class TermSuggestionQueryTest
{
    @Test
    public void testBuilder()
    {
        final TermSuggestionQuery query = TermSuggestionQuery.create( "queryName" ).
            field( "category" ).
            build();

        assertEquals( "queryName", query.getName() );
        assertEquals( "category", query.getField() );
    }

}
