package com.enonic.xp.index;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternBasedIndexConfigDocumentOldShitTest
{
    @Test
    void test_best_matching_pattern_used()
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().add( IndexPath.from( "data" ), IndexConfig.BY_TYPE )
            .add( IndexPath.from( "data.secret.show" ), IndexConfig.FULLTEXT )
            .add( IndexPath.from( "data.secret" ), IndexConfig.NONE )
            .add( IndexPath.from( "displayName" ), IndexConfig.FULLTEXT )
            .add( IndexPath.from( "creator" ), IndexConfig.MINIMAL )
            .
            defaultConfig( IndexConfig.MINIMAL ).
            build();

        assertEquals( IndexConfig.NONE, config.getConfigForPath( IndexPath.from( "data.secret.dummy" ) ) );
        assertEquals( IndexConfig.FULLTEXT, config.getConfigForPath( IndexPath.from( "data.secret.show" ) ) );
        assertEquals( IndexConfig.BY_TYPE, config.getConfigForPath( IndexPath.from( "data.stuff" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( IndexPath.from( "creator" ) ) );

    }
}
