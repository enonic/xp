package com.enonic.xp.content;

import java.time.Instant;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetActiveContentVersionsResultTest
{
    @Test
    public void same_version()
        throws Exception
    {
        final Instant now = Instant.now();

        final ContentVersion version = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now ).
            timestamp( now ).
            build();

        final Branch draft = Branch.from( "draft" );
        final Branch master = Branch.from( "master" );

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( ActiveContentVersionEntry.from( draft, version ) ).
            add( ActiveContentVersionEntry.from( master, version ) ).
            build();

        assertEquals( 2, result.getActiveContentVersions().size() );
    }

    @Test
    public void skip_null()
        throws Exception
    {
        final Instant now = Instant.now();

        final ContentVersion version = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now ).
            build();

        final Branch draft = Branch.from( "draft" );
        final Branch master = Branch.from( "master" );

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( ActiveContentVersionEntry.from( draft, version ) ).
            add( ActiveContentVersionEntry.from( master, null ) ).
            build();

        assertEquals( 1, result.getActiveContentVersions().size() );
    }

    @Test
    public void test_ordering()
        throws Exception
    {
        final Instant oldest = Instant.parse( "2014-09-25T10:00:00.00Z" );
        final Instant middle = Instant.parse( "2014-09-25T11:00:00.00Z" );
        final Instant newest = Instant.parse( "2014-09-25T12:00:00.00Z" );

        final Branch archive = Branch.from( "archive" );
        final Branch draft = Branch.from( "draft" );
        final Branch master = Branch.from( "master" );

        final ContentVersion oldVersion = ContentVersion.create().
            id( ContentVersionId.from( "b" ) ).
            modified( middle ).
            timestamp( middle ).
            build();

        final ContentVersion oldestVersion = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( oldest ).
            timestamp( oldest ).
            build();

        final ContentVersion newVersion = ContentVersion.create().
            id( ContentVersionId.from( "c" ) ).
            modified( newest ).
            timestamp( newest ).
            build();

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( ActiveContentVersionEntry.from( master, oldVersion ) ).
            add( ActiveContentVersionEntry.from( draft, newVersion ) ).
            add( ActiveContentVersionEntry.from( archive, oldestVersion ) ).
            build();

        final Iterator<ActiveContentVersionEntry> iterator = result.getActiveContentVersions().iterator();

        assertEquals( draft, iterator.next().getBranch() );
        assertEquals( master, iterator.next().getBranch() );
        assertEquals( archive, iterator.next().getBranch() );
    }
}
