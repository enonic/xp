package com.enonic.xp.repo.impl.dump;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.Model;
import com.enonic.xp.util.Version;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DumpServiceImplModelVersionTest
{
    @Test
    void accepts_current_model_version()
    {
        assertDoesNotThrow( () -> DumpServiceImpl.verifyModelVersion( Model.MODEL_VERSION ) );
    }

    @Test
    void rejects_older_model_version()
    {
        assertThatThrownBy( () -> DumpServiceImpl.verifyModelVersion( new Version( 8, 0, 0 ) ) )
            .isInstanceOf( RepoLoadException.class )
            .hasMessageContaining( "model version [8.0.0] is not supported" );
    }

    @Test
    void rejects_missing_model_version()
    {
        assertThatThrownBy( () -> DumpServiceImpl.verifyModelVersion( null ) ).isInstanceOf( RepoLoadException.class );
    }
}
