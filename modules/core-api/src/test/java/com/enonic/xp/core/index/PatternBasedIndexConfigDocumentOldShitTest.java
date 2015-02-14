package com.enonic.xp.core.index;

import org.junit.Test;

import com.enonic.xp.core.data.PropertyPath;

import static org.junit.Assert.*;

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

        assertEquals( IndexConfig.NONE, config.getConfigForPath( PropertyPath.from( "data.secret.dummy" ) ) );
        assertEquals( IndexConfig.FULLTEXT, config.getConfigForPath( PropertyPath.from( "data.secret.show" ) ) );
        assertEquals( IndexConfig.BY_TYPE, config.getConfigForPath( PropertyPath.from( "data.stuff" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( PropertyPath.from( "creator" ) ) );

    }
}