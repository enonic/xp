package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class RegionPathTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( RegionPath.class ).withNonnullFields( "regionName" ).verify();
    }
}
