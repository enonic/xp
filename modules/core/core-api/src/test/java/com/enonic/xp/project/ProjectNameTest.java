package com.enonic.xp.project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectNameTest
{
    @Test
    void maxLength()
    {
        final ProjectName projectName = ProjectName.from( "a".repeat( 63 ) );
        assertEquals( ProjectConstants.PROJECT_REPO_ID_PREFIX + "a".repeat( 63 ), projectName.getRepoId().toString() );
        assertThrows( IllegalArgumentException.class, () -> ProjectName.from( "a".repeat( 64 ) ) );
    }
}
