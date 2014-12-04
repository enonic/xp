package com.enonic.wem.export.internal.reader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileExportReader
    implements ExportReader
{

    @Override
    public Stream<Path> getChildrenPaths( final Path parent )
    {
        try
        {
            return Files.list( parent );

        }
        catch ( IOException e )
        {
            throw new ExportReaderException( "Fetching children of parent item with path " + parent.toString() + " failed", e );
        }
    }

    public File getFile( final Path path )
    {
        return path.toFile();
    }

    @Override
    public String readItem( final Path path )
    {
        try
        {
            return new String( Files.readAllBytes( path ) );
        }
        catch ( IOException e )
        {
            throw new ExportReaderException( "Could not read item with path " + path.toString(), e );
        }
    }

    @Override
    public List<String> readLines( final Path path )
    {
        try
        {
            return Files.readAllLines( path, Charset.forName( "UTF-8" ) );
        }
        catch ( IOException e )
        {
            throw new ExportReaderException( "Could not read item with path " + path, e );
        }

    }
}
