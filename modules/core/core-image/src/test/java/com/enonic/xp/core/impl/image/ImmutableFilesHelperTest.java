package com.enonic.xp.core.impl.image;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.*;

public class ImmutableFilesHelperTest
{
    @TempDir
    public Path temporaryFolder;

    private int supplierCall;


    @Test
    public void test_computeIfAbsent()
        throws Exception
    {
        supplierCall = 0;

        final byte[] bytes = new byte[]{2, 3, 5, 7, 13};
        final ByteSource source = ByteSource.wrap( bytes );
        Path path = Paths.get( temporaryFolder.toString(), "file.txt" );

        SupplierWithException<ByteSource, Exception> supplier = () -> {
            supplierCall++;
            return ByteSource.wrap( bytes );
        };

        ByteSource byteSource = ImmutableFilesHelper.computeIfAbsent( path, supplier );
        assertTrue( supplierCall == 1 );
        assertTrue( source.contentEquals( byteSource ) );

        byteSource = ImmutableFilesHelper.computeIfAbsent( path, supplier );
        assertTrue( supplierCall == 1 );
        assertTrue( source.contentEquals( byteSource ) );
    }

    @Test
    public void test_incorrect_computeIfAbsent()
        throws Exception
    {
        Path path = Paths.get( temporaryFolder.toString(), "unknown_file.txt" );
        SupplierWithException<ByteSource, Exception> supplier = () -> null;

        ByteSource byteSource = ImmutableFilesHelper.computeIfAbsent( path, supplier );
        assertNull( byteSource );
    }
}
