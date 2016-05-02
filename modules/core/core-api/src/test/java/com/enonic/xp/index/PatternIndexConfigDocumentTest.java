package com.enonic.xp.index;

import org.junit.Test;

import com.enonic.xp.data.PropertyPath;

import static org.junit.Assert.*;

public class PatternIndexConfigDocumentTest
{
    @Test
    public void pattern_best_match()
        throws Exception
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            add( "page", IndexConfig.MINIMAL ).
            add( "page.region.component.textcomponent.text", IndexConfig.FULLTEXT ).
            build();

        assertTrue( config.getConfigForPath( PropertyPath.from( "page", "region", "component", "textcomponent", "text" ) ).isFulltext() );
        assertFalse( config.getConfigForPath( PropertyPath.from( "page" ) ).isFulltext() );
        assertFalse( config.getConfigForPath( PropertyPath.from( "page", "region" ) ).isFulltext() );
    }
}