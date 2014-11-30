package com.enonic.wem.export.internal.writer;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExportItemPathTest
{

    @Test
    public void build_path()
        throws Exception
    {
        final ExportItemPath path = ExportItemPath.create().
            add( "root" ).
            add( "parent" ).
            add( "child" ).
            build();

        assertEquals( "root/parent/child", path.getPathAsString() );
    }

    @Test
    public void build_path_absolute()
        throws Exception
    {
        final ExportItemPath path = ExportItemPath.create().
            add( "/root" ).
            add( "parent" ).
            add( "child" ).
            build();

        assertEquals( "/root/parent/child", path.getPathAsString() );
    }

    @Test
    public void from()
        throws Exception
    {
        final ExportItemPath path = ExportItemPath.create().
            add( "/root" ).
            add( "parent" ).
            add( "child" ).
            build();

        final ExportItemPath grandChild = ExportItemPath.from( path, "grandchild" );

        assertEquals( "/root/parent/child/grandchild", grandChild.getPathAsString() );
    }

}