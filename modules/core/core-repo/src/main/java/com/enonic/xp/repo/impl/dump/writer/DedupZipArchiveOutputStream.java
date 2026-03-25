package com.enonic.xp.repo.impl.dump.writer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import com.google.common.io.ByteSource;

public class DedupZipArchiveOutputStream
{
    public final Set<String> deduplication = new HashSet<>();

    public final ZipArchiveOutputStream zipArchiveOutputStream;

    public DedupZipArchiveOutputStream( final ZipArchiveOutputStream zipArchiveOutputStream )
    {
        this.zipArchiveOutputStream = zipArchiveOutputStream;
    }

    public void put( String name, ByteSource record )
        throws IOException
    {
        final ZipArchiveEntry archiveEntry = new ZipArchiveEntry( name );
        record.sizeIfKnown().toJavaUtil().ifPresent( archiveEntry::setSize );
        zipArchiveOutputStream.putArchiveEntry( archiveEntry );
        record.copyTo( zipArchiveOutputStream );
        zipArchiveOutputStream.closeArchiveEntry();
        deduplication.add( name );
    }

    public void close()
        throws IOException
    {
        zipArchiveOutputStream.close();
    }
}
