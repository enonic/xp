package com.enonic.xp.content;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentVersionTest
{
    @Test
    public void testBuilder()
    {
        final Instant now1 = Instant.now();
        final Instant now2 = Instant.now();

        final ContentVersionPublishInfo publishInfo = ContentVersionPublishInfo.create().
            message( "My version 1" ).
            publisher( PrincipalKey.ofAnonymous() ).
            timestamp( Instant.ofEpochSecond( 1562056003L ) ).
            contentPublishInfo( ContentPublishInfo.create().
                first( Instant.now() ).
                from( Instant.now() ).
                to( Instant.now().plus( 3, ChronoUnit.DAYS ) ).
                build() ).
            build();

        final WorkflowInfo workflowInfo = WorkflowInfo.create().
            state( WorkflowState.READY ).
            build();

        final ContentVersion version = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now1 ).
            timestamp( now2 ).
            modifier( PrincipalKey.ofAnonymous() ).
            displayName( "contentVersion" ).
            comment( "comment" ).
            publishInfo( publishInfo ).
            workflowInfo( workflowInfo ).
            build();

        assertEquals( ContentVersionId.from( "a" ), version.getId() );
        assertEquals( now1, version.getModified() );
        assertEquals( now2, version.getTimestamp() );
        assertEquals( "comment", version.getComment() );
        assertEquals( PrincipalKey.ofAnonymous(), version.getModifier() );
        assertEquals( "contentVersion", version.getDisplayName() );
        assertEquals( publishInfo, version.getPublishInfo() );
        assertEquals( workflowInfo, version.getWorkflowInfo() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ContentVersion.class ).verify();
    }
}
