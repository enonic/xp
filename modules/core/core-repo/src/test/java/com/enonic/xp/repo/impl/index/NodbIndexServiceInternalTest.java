package com.enonic.xp.repo.impl.index;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodbIndexServiceInternalTest
{
    @Test
    void refusesActivationUnlessBackendIsNodb()
    {
        final NodbIndexServiceInternal service = new NodbIndexServiceInternal();

        assertThrows( IllegalStateException.class, () -> service.activate( Map.of( "backend", "elasticsearch" ) ) );
        assertThrows( IllegalStateException.class, () -> service.activate( Map.of() ) );
        assertDoesNotThrow( () -> service.activate( Map.of( "backend", "nodb" ) ) );
    }

    @Test
    void answersFromWhatIsTrueOfTheNodbStack()
    {
        final NodbIndexServiceInternal service = new NodbIndexServiceInternal();
        service.activate( Map.of( "backend", "nodb" ) );

        assertTrue( service.isMaster() );
        assertTrue( service.waitForYellowStatus() );
        assertDoesNotThrow( () -> service.closeIndices( "a" ) );
        assertDoesNotThrow( () -> service.openIndices( "a" ) );
        assertThrows( UnsupportedOperationException.class, () -> service.deleteIndices( "a" ) );
    }
}
