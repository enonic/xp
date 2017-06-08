package com.enonic.xp.lib.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

final class TemporaryFileInputStream
    extends FileInputStream
{
    private final File file;

    TemporaryFileInputStream( final File file )
        throws FileNotFoundException
    {
        super( file );
        this.file = file;
    }

    @Override
    public void close()
        throws IOException
    {
        try
        {
            super.close();
        }
        finally
        {
            try
            {
                file.delete();
            }
            catch ( Exception e )
            {
                // DO NOTHING
            }
        }
    }
}