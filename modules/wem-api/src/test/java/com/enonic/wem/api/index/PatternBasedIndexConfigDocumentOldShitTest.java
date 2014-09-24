package com.enonic.wem.api.index;

import org.junit.Test;

import com.enonic.wem.api.data.DataPath;

import static org.junit.Assert.*;

public class PatternBasedIndexConfigDocumentOldShitTest
{
    @Test
    public void test_best_matching_pattern_used()
        throws Exception
    {
        final PatternBasedIndexConfigDocument config = PatternBasedIndexConfigDocument.create().
            add( DataPath.from( "data" ), IndexConfig.BY_TYPE ).
            add( DataPath.from( "data.secret.show" ), IndexConfig.FULLTEXT ).
            add( DataPath.from( "data.secret" ), IndexConfig.NONE ).
            add( DataPath.from( "displayName" ), IndexConfig.FULLTEXT ).
            add( DataPath.from( "creator" ), IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.MINIMAL ).
            build();

        assertEquals( IndexConfig.NONE, config.getConfigForData( DataPath.from( "data.secret.dummy" ) ) );
        assertEquals( IndexConfig.FULLTEXT, config.getConfigForData( DataPath.from( "data.secret.show" ) ) );
        assertEquals( IndexConfig.BY_TYPE, config.getConfigForData( DataPath.from( "data.stuff" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForData( DataPath.from( "creator" ) ) );

    }
}