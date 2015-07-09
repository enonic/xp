package com.enonic.xp.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Preconditions;

public class FilesHelper
{
    private static final Lock LOCK = new ReentrantLock();

    private static final Map<Path, Condition> CONDITION_MAP = new HashMap<>();

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

        LOCK.lock();
        try
        {
            while ( !canWrite )
            {
                final Condition condition = CONDITION_MAP.get( path );
                if ( condition == null )
                {
                    canWrite = true;
                    CONDITION_MAP.put( path, LOCK.newCondition() );
                }
                else
                {
                    try
                    {
                        condition.await();
                    }
                    catch ( InterruptedException e )
                    {
                    }
                }
            }
        }
        finally
        {
            LOCK.unlock();
        }
    }

    private static void unlock( Path path )
    {
        LOCK.lock();
        try
        {
            final Condition condition = CONDITION_MAP.remove( path );
            condition.signalAll();
        }
        finally
        {
            LOCK.unlock();
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
                System.out.println( "writing rem" + rem );
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
