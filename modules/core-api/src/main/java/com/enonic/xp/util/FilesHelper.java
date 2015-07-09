package com.enonic.xp.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Preconditions;

public class FilesHelper
{
    private static final Lock LOCK = new ReentrantLock();

    private static final Map<Path, Condition> CONDITION_MAP = new HashMap<>();

    private static final Map<Path, Integer> READER_COUNTER_MAP = new HashMap<>();

    private enum LOCK_TYPE
    {
        READ,
        WRITE
    }

    public static void write( Path path, byte[] bytes )
        throws IOException
    {
        Preconditions.checkNotNull( path, "path is required" );
        Preconditions.checkNotNull( bytes, "bytes are required" );

        lock( path, LOCK_TYPE.WRITE );

        Files.createDirectories( path.getParent() );
        Files.write( path, bytes );

        unlock( path, LOCK_TYPE.WRITE );
    }

    public static byte[] readAllBytes( Path path )
        throws IOException
    {
        Preconditions.checkNotNull( path, "path is required" );

        lock( path, LOCK_TYPE.READ );

        byte[] bytes = null;
        if ( Files.exists( path ) )
        {
            bytes = Files.readAllBytes( path );
        }

        unlock( path, LOCK_TYPE.READ );

        return bytes;
    }

    private static void lock( Path path, LOCK_TYPE lockType )
    {
        boolean pathLocked = false;

        LOCK.lock();
        try
        {
            while ( !pathLocked )
            {
                final Condition condition = CONDITION_MAP.get( path );

                //If this path is not used
                if ( condition == null )
                {
                    //Marks this path as used
                    pathLocked = true;
                    CONDITION_MAP.put( path, LOCK.newCondition() );

                    //If I am a reader
                    if ( LOCK_TYPE.READ == lockType )
                    {
                        //Marks this path as used by one writer
                        READER_COUNTER_MAP.put( path, 1 );
                    }
                }

                //Else this path is already used
                else
                {
                    //If I am a reader and this path is already used by readers
                    final Integer readerCounter = READER_COUNTER_MAP.get( path );
                    if ( LOCK_TYPE.READ == lockType && readerCounter != null )
                    {
                        //Marks this path as used by one more writer
                        pathLocked = true;
                        READER_COUNTER_MAP.put( path, readerCounter + 1 );
                    }
                    //Else
                    else
                    {
                        //Waits for the last reader or the writer
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
        }
        finally
        {
            LOCK.unlock();
        }
    }

    private static void unlock( Path path, LOCK_TYPE lockType )
    {
        LOCK.lock();
        try
        {
            boolean lastReader = false;

            //If I am a reader
            if ( LOCK_TYPE.READ == lockType )
            {
                //If I am the last reader
                final Integer readerCounter = READER_COUNTER_MAP.get( path );
                if ( readerCounter == 1 )
                {
                    //Removes the reader counter
                    lastReader = true;
                    READER_COUNTER_MAP.remove( path );
                }
                //Else
                else
                {
                    //Decrements the reader counter
                    READER_COUNTER_MAP.put( path, readerCounter - 1 );
                }
            }

            //If I am the last reader or a writer
            if ( lastReader || LOCK_TYPE.WRITE == lockType )
            {
                //Marks the path as free
                final Condition condition = CONDITION_MAP.remove( path );
                //Wakes up the waiters
                condition.signalAll();
            }
        }
        finally
        {
            LOCK.unlock();
        }
    }
}
