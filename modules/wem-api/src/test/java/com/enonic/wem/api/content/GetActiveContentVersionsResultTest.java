package com.enonic.wem.api.content;

import java.time.Instant;

import org.junit.Test;

import com.google.common.collect.UnmodifiableIterator;

import com.enonic.wem.api.entity.Workspace;

import static org.junit.Assert.*;

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
            build();

        final Workspace stage = Workspace.from( "stage" );
        final Workspace prod = Workspace.from( "prod" );

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( stage, version ).
            add( prod, version ).
            build();

        assertNotNull( result.getContentVersions().get( stage ) );
        assertNotNull( result.getContentVersions().get( prod ) );
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

        final Workspace stage = Workspace.from( "stage" );
        final Workspace prod = Workspace.from( "prod" );

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( stage, version ).
            add( prod, null ).
            build();

        assertEquals( 1, result.getContentVersions().size() );
        assertNotNull( result.getContentVersions().get( stage ) );
    }

    @Test
    public void test_ordering()
        throws Exception
    {
        final Instant oldest = Instant.parse( "2014-09-25T10:00:00.00Z" );
        final Instant middle = Instant.parse( "2014-09-25T11:00:00.00Z" );
        final Instant newest = Instant.parse( "2014-09-25T12:00:00.00Z" );

        final Workspace archive = Workspace.from( "archive" );
        final Workspace stage = Workspace.from( "stage" );
        final Workspace prod = Workspace.from( "prod" );

        final ContentVersion oldVersion = ContentVersion.create().
            id( ContentVersionId.from( "b" ) ).
            modified( middle ).
            build();

        final ContentVersion oldestVersion = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( oldest ).
            build();

        final ContentVersion newVersion = ContentVersion.create().
            id( ContentVersionId.from( "c" ) ).
            modified( newest ).
            build();

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( prod, oldVersion ).
            add( stage, newVersion ).
            add( archive, oldestVersion ).
            build();

        final UnmodifiableIterator<Workspace> iterator = result.getContentVersions().keySet().iterator();

        assertEquals( stage, iterator.next() );
        assertEquals( prod, iterator.next() );
        assertEquals( archive, iterator.next() );
    }
}