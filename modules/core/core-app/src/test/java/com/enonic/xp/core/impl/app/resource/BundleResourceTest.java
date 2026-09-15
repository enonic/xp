package com.enonic.xp.core.impl.app.resource;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;

import com.enonic.xp.vfs.VirtualFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BundleResourceTest
{
    @Test
    void name_of_root()
    {
        final BundleResource root = new BundleResource( mock( Bundle.class ), "/" );
        assertEquals( "", root.getName() );
    }

    @Test
    void name_of_file()
    {
        final BundleResource file = new BundleResource( mock( Bundle.class ), "/import/features/export.properties" );
        assertEquals( "export.properties", file.getName() );
        assertTrue( file.isFile() );
        assertFalse( file.isFolder() );
    }

    @Test
    void name_of_folder_with_trailing_slash()
    {
        final BundleResource folder = new BundleResource( mock( Bundle.class ), "/import/features/_/" );
        assertEquals( "_", folder.getName() );
        assertTrue( folder.isFolder() );
        assertFalse( folder.isFile() );
    }

    @Test
    void children_names()
        throws Exception
    {
        final Bundle bundle = mock( Bundle.class );
        when( bundle.findEntries( "/import/features/", "*", false ) ).thenReturn( Collections.enumeration(
            List.of( url( "/import/features/_/" ), url( "/import/features/child/" ), url( "/import/features/export.properties" ) ) ) );

        final BundleResource parent = new BundleResource( bundle, "/import/features/" );

        final Map<String, VirtualFile> children =
            parent.getChildren().stream().collect( Collectors.toMap( VirtualFile::getName, Function.identity() ) );

        assertEquals( 3, children.size() );
        assertTrue( children.get( "_" ).isFolder() );
        assertTrue( children.get( "child" ).isFolder() );
        assertTrue( children.get( "export.properties" ).isFile() );
        assertEquals( "/import/features/_", children.get( "_" ).getPath().getPath() );
    }

    private static URL url( final String path )
        throws Exception
    {
        return URI.create( "file:" + path ).toURL();
    }
}
