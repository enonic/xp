package com.enonic.xp.project;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectNameTest
{
    @Test
    void maxLength()
    {
        final ProjectName projectName = ProjectName.from( "a".repeat( 63 ) );
        assertThat( projectName.getRepoId() ).hasToString( ProjectConstants.PROJECT_REPO_ID_PREFIX + "a".repeat( 63 ) );
        assertThatThrownBy( () -> ProjectName.from( "a".repeat( 64 ) ) ).isInstanceOf( IllegalArgumentException.class );
    }
}
