package com.enonic.wem.admin.json.content;

import java.time.Instant;

import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentVersion;
import com.enonic.wem.api.content.ContentVersionId;
import com.enonic.wem.api.content.GetActiveContentVersionsResult;
import com.enonic.wem.api.entity.Workspace;

import static org.junit.Assert.*;

public class GetActiveContentVersionsResultJsonTest
{
    @Test
    public void test_for_jvs_to_watch()
        throws Exception
    {

        final Instant now = Instant.now();

        final ContentVersion version = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now ).
            displayName( "myDisplayName" ).
            modifier( UserKey.superUser() ).
            build();

        final Workspace stage = Workspace.from( "stage" );
        final Workspace prod = Workspace.from( "prod" );

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( stage, version ).
            add( prod, version ).
            build();

        assertNotNull( result.getContentVersions().get( stage ) );
        assertNotNull( result.getContentVersions().get( prod ) );

        final GetActiveContentVersionsResultJson getActiveContentVersionsResultJson = new GetActiveContentVersionsResultJson( result );


    }
}