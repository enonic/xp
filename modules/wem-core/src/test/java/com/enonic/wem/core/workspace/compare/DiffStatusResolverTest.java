package com.enonic.wem.core.workspace.compare;

import org.junit.Test;

import com.enonic.wem.api.entity.CompareStatus;
import com.enonic.wem.core.version.VersionBranch;

import static org.junit.Assert.*;

public class DiffStatusResolverTest
{

    @Test
    public void only_in_source()
        throws Exception
    {
        final VersionBranch source = VersionBranch.create().
            add( "1.1.4", "1.1.3" ).
            add( "1.1.3", "1.1.2" ).
            add( "1.1.2", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final VersionBranch target = VersionBranch.create().
            build();

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( source, target ) );

        assertEquals( CompareStatus.Status.NEW, compareStatus.getStatus() );
    }

    @Test
    public void only_in_target()
        throws Exception
    {
        final VersionBranch source = VersionBranch.create().
            build();

        final VersionBranch target = VersionBranch.create().
            add( "1.1.4", "1.1.3" ).
            add( "1.1.3", "1.1.2" ).
            add( "1.1.2", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( source, target ) );

        assertEquals( CompareStatus.Status.DELETED, compareStatus.getStatus() );
    }

    @Test
    public void equal()
        throws Exception
    {
        final VersionBranch source = VersionBranch.create().
            add( "1.1.4", "1.1.3" ).
            add( "1.1.3", "1.1.2" ).
            add( "1.1.2", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final VersionBranch target = VersionBranch.create().
            add( "1.1.4", "1.1.3" ).
            add( "1.1.3", "1.1.2" ).
            add( "1.1.2", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( source, target ) );

        assertEquals( CompareStatus.Status.EQUAL, compareStatus.getStatus() );
    }


    @Test
    public void different_branch()
        throws Exception
    {
        final VersionBranch source = VersionBranch.create().
            add( "1.1.4", "1.1.3" ).
            add( "1.1.3", "1.1.2" ).
            add( "1.1.2", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final VersionBranch target = VersionBranch.create().
            add( "1.1.1", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( source, target ) );

        assertEquals( CompareStatus.Status.CONFLICT, compareStatus.getStatus() );
    }

    @Test
    public void same_branch_newer_in_source()
        throws Exception
    {
        final VersionBranch source = VersionBranch.create().
            add( "1.1.4", "1.1.3" ).
            add( "1.1.3", "1.1.2" ).
            add( "1.1.2", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final VersionBranch target = VersionBranch.create().
            add( "1.1.2", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( source, target ) );

        assertEquals( CompareStatus.Status.NEWER, compareStatus.getStatus() );
    }


    @Test
    public void same_branch_older_in_source()
        throws Exception
    {
        final VersionBranch source = VersionBranch.create().
            add( "1.1.2", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final VersionBranch target = VersionBranch.create().
            add( "1.1.4", "1.1.3" ).
            add( "1.1.3", "1.1.2" ).
            add( "1.1.2", "1.1" ).
            add( "1.1", "1" ).
            add( "1", null ).
            build();

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( source, target ) );

        assertEquals( CompareStatus.Status.OLDER, compareStatus.getStatus() );
    }

}
