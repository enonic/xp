package com.enonic.xp.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;

public class FilesHelper
{
    private static final Map<Path, Object> test = new HashMap<>();

    public static void write( Path path, byte[] bytes )
        throws IOException
    {
        Preconditions.checkNotNull( path, "path is required" );
        Preconditions.checkNotNull( bytes, "bytes are required" );

        lock( path );

        Files.createDirectories( path.getParent() );
        writefilesWithLog( path, bytes );
        //Files.write( path, bytes );
        System.out.println( "writing " + path );

        unlock( path );
    }

    private static void lock( Path path )
    {
        boolean canWrite = false;
        while ( !canWrite )
        {
            synchronized ( test )
            {
                if ( test.get( path ) == null )
                {
                    canWrite = true;
                    test.put( path, new Object() );
                }
                else
                {
                    try
                    {
                        test.wait();
                    }
                    catch ( InterruptedException e )
                    {
                    }
                }
            }

        }
    }

    private static void unlock( Path path )
    {
        synchronized ( test )
        {
            test.remove( path );
            test.notifyAll();
        }
    }

    public static Path writefilesWithLog( Path path, byte[] bytes, OpenOption... options )
        throws IOException
    {
        Objects.requireNonNull( bytes );

        try (OutputStream out = Files.newOutputStream( path, options ))
        {
            int len = bytes.length;
            int rem = len;
            while ( rem > 0 )
            {
                int n = Math.min( rem, 8192 );
                synchronized ( System.out )
                {
                    System.out.println( "writing rem" + rem );
                    System.out.flush();
                }
                out.write( bytes, ( len - rem ), n );
                rem -= n;
            }
        }
        return path;
    }


    public static byte[] readAllBytes( Path path )
        throws IOException
    {
        Preconditions.checkNotNull( path, "path is required" );

        if ( Files.exists( path ) )
        {
            return Files.readAllBytes( path );
        }
        else
        {
            return null;
        }

    }
}
