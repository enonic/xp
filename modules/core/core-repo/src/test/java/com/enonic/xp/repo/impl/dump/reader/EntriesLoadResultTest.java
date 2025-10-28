package com.enonic.xp.repo.impl.dump.reader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntriesLoadResultTest
{

    @Test
    void build()
    {
        final EntriesLoadResult.Builder builder = EntriesLoadResult.create();

        final EntryLoadResult entryResult = EntryLoadResult.create().
            successful().
            successful().
            successful().
            error( EntryLoadError.error( "an error" ) ).
            build();

        builder.add( entryResult );
        builder.add( entryResult );

        final EntriesLoadResult result = builder.build();

        assertEquals( 6, result.getSuccessful() );
        assertEquals( 2, result.getErrors().size() );
    }
}
