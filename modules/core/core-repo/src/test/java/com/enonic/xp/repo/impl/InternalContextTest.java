package com.enonic.xp.repo.impl;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextBuilder;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InternalContextTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( InternalContext.class ).usingGetClass().withNonnullFields( "repositoryId", "branch" ).verify();
    }

    @Test
    void fromContext_withPrimarySearchPreferenceAttribute()
    {
        final InternalContext context = InternalContext.from( ContextBuilder.create()
                                                                  .repositoryId( "repo" )
                                                                  .branch( "draft" )
                                                                  .attribute( "searchPreference", "PRIMARY" )
                                                                  .build() );

        assertEquals( SearchPreference.PRIMARY, context.getSearchPreference() );
    }

    @Test
    void fromContext_withInvalidSearchPreferenceAttribute()
    {
        final InternalContext context = InternalContext.from( ContextBuilder.create()
                                                                  .repositoryId( "repo" )
                                                                  .branch( "draft" )
                                                                  .attribute( "searchPreference", "INVALID" )
                                                                  .build() );

        assertNull( context.getSearchPreference() );
    }
}
