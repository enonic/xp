package com.enonic.xp.index;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyPath;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternBasedIndexConfigDocumentOldShitTest
{
    @Test
    public void test_best_matching_pattern_used()
        throws Exception
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            add( PropertyPath.from( "data" ), IndexConfig.BY_TYPE ).
            add( PropertyPath.from( "data.secret.show" ), IndexConfig.FULLTEXT ).
            add( PropertyPath.from( "data.secret" ), IndexConfig.NONE ).
            add( PropertyPath.from( "displayName" ), IndexConfig.FULLTEXT ).
            add( PropertyPath.from( "creator" ), IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.MINIMAL ).
            build();

        assertEquals( IndexConfig.NONE, config.getConfigForPath( IndexPath.from( "data.secret.dummy" ) ) );
        assertEquals( IndexConfig.FULLTEXT, config.getConfigForPath( IndexPath.from( "data.secret.show" ) ) );
        assertEquals( IndexConfig.BY_TYPE, config.getConfigForPath( IndexPath.from( "data.stuff" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( IndexPath.from( "creator" ) ) );

    }
}
