package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.ByteSource;

public class ImmutableFilesHelperTest
{
    private TemporaryFolder temporaryFolder;

    private int supplierCall;

    @BeforeEach
    public void setUp()
        throws IOException
    {
        temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
    }

    @Test
    public void test_computeIfAbsent()
        throws Exception
    {
        supplierCall = 0;

        final byte[] bytes = new byte[]{2, 3, 5, 7, 13};
        final ByteSource source = ByteSource.wrap( bytes );
        Path path = Paths.get( temporaryFolder.getRoot().toString(), "file.txt" );

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
        Path path = Paths.get( temporaryFolder.getRoot().toString(), "unknown_file.txt" );
        SupplierWithException<ByteSource, Exception> supplier = () -> null;

        ByteSource byteSource = ImmutableFilesHelper.computeIfAbsent( path, supplier );
        assertNull( byteSource );
    }
}
