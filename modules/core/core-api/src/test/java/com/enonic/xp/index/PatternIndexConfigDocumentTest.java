package com.enonic.xp.index;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternIndexConfigDocumentTest
{
    @Test
    public void pattern_best_match()
        throws Exception
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            add( "page", IndexConfig.MINIMAL ).
            add( "page.region.another", IndexConfig.BY_TYPE ).
            add( "page.region.component.textcomponent.text", IndexConfig.FULLTEXT ).
            build();

        assertEquals( IndexConfig.FULLTEXT,
                      config.getConfigForPath( IndexPath.from( String.join( ".","page", "region", "component", "textcomponent", "text" ) ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( IndexPath.from( "page" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( IndexPath.from( "page.region" ) ) );
        assertEquals( IndexConfig.BY_TYPE, config.getConfigForPath( IndexPath.from( "page.region.another.fisk.ost" ) ) );
    }

    @Test
    public void no_match_use_default()
        throws Exception
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            add( "page", IndexConfig.NONE ).
            add( "page.region", IndexConfig.FULLTEXT ).
            add( "page.region.**", IndexConfig.FULLTEXT ).
            defaultConfig( IndexConfig.MINIMAL ).
            build();

        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( IndexPath.from( "site.data.ost" ) ) );
        assertEquals( IndexConfig.NONE, config.getConfigForPath( IndexPath.from( "page" ) ) );
        assertEquals( IndexConfig.FULLTEXT, config.getConfigForPath( IndexPath.from( "page.region" ) ) );
    }

    @Test
    public void pattern_wildcard_match()
        throws Exception
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            add( "page", IndexConfig.MINIMAL ).
            add( "page.regions.**.text", IndexConfig.FULLTEXT ).
            add( "page.regions.*.center.*.text", IndexConfig.NONE ).
            build();

        assertEquals( IndexConfig.FULLTEXT, config.getConfigForPath(
            IndexPath.from( "page.regions.main.components.regions.fisk.components.text" ) ) );
        assertEquals( IndexConfig.NONE,
                      config.getConfigForPath( IndexPath.from( "page.regions.main.center.components.text" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( IndexPath.from( "page" ) ) );
        assertEquals( IndexConfig.MINIMAL, config.getConfigForPath( IndexPath.from( "page.region" ) ) );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( PatternIndexConfigDocument.class ).withRedefinedSuperclass().withIgnoredFields( "pathIndexConfigMap" ).verify();
    }
}
