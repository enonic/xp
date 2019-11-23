package com.enonic.xp.index;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyPath;

import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternIndexConfigDocumentTest
{
    @Test
    public void pattern_best_match()
        throws Exception
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            add( "page", IndexConfig.MINIMAL ).
            add( "page" + ELEMENT_DIVIDER + "region" + ELEMENT_DIVIDER + "another", IndexConfig.BY_TYPE ).
            add( "page" + ELEMENT_DIVIDER + "region" + ELEMENT_DIVIDER + "component" + ELEMENT_DIVIDER + "textcomponent" + ELEMENT_DIVIDER +
                     "text", IndexConfig.FULLTEXT ).
            build();

        assertEquals( IndexConfig.FULLTEXT,
                      config.getConfigForPath( PropertyPath.from( "page", "region", "component", "textcomponent", "text" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( PropertyPath.from( "page" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( PropertyPath.from( "page", "region" ) ) );
        assertEquals( IndexConfig.BY_TYPE, config.getConfigForPath( PropertyPath.from( "page", "region", "another", "fisk", "ost" ) ) );
    }

    @Test
    public void no_match_use_default()
        throws Exception
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            add( "page", IndexConfig.NONE ).
            add( "page" + ELEMENT_DIVIDER + "region", IndexConfig.FULLTEXT ).
            add( "page" + ELEMENT_DIVIDER + "region" + ELEMENT_DIVIDER + "**", IndexConfig.FULLTEXT ).
            defaultConfig( IndexConfig.MINIMAL ).
            build();

        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( PropertyPath.from( "site", "data", "ost" ) ) );
        assertEquals( IndexConfig.NONE, config.getConfigForPath( PropertyPath.from( "page" ) ) );
        assertEquals( IndexConfig.FULLTEXT, config.getConfigForPath( PropertyPath.from( "page", "region" ) ) );
    }

    @Test
    public void pattern_wildcard_match()
        throws Exception
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            add( "page", IndexConfig.MINIMAL ).
            add( "page" + ELEMENT_DIVIDER + "regions" + ELEMENT_DIVIDER + "**" + ELEMENT_DIVIDER + "text", IndexConfig.FULLTEXT ).
            add( "page" + ELEMENT_DIVIDER + "regions" + ELEMENT_DIVIDER + "*" + ELEMENT_DIVIDER + "center" + ELEMENT_DIVIDER + "*" +
                     ELEMENT_DIVIDER + "text", IndexConfig.NONE ).
            build();

        assertEquals( IndexConfig.FULLTEXT, config.getConfigForPath(
            PropertyPath.from( "page", "regions", "main", "components", "regions", "fisk", "components", "text" ) ) );
        assertEquals( IndexConfig.NONE,
                      config.getConfigForPath( PropertyPath.from( "page", "regions", "main", "center", "components", "text" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( PropertyPath.from( "page" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( PropertyPath.from( "page", "region" ) ) );
    }

}
