package com.enonic.xp.core.impl.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.util.Exceptions;

public class ImmutableFilesHelper
{
    private static final Lock LOCK = new ReentrantLock();

    private static final Map<Path, Condition> CONDITION_MAP = new HashMap<>();


    public static <E extends Exception> ByteSource computeIfAbsent( Path path, SupplierWithException<? extends ByteSource, E> supplier )
        throws E, IOException
    {
        Preconditions.checkNotNull( path, "path is required" );
        Preconditions.checkNotNull( supplier, "supplier is required" );

        ByteSource byteSource = null;

        lock( path );

        try
        {
            final File file = path.toFile();
            if ( file.exists() )
            {
                byteSource = Files.asByteSource( file );
            }
            else
            {
                byteSource = supplier.get();

                if ( byteSource != null )
                {
                    File parentFile = file.getParentFile();
                    if ( !parentFile.exists() )
                    {
                        parentFile.mkdirs();
                    }

                    final ByteSink byteSink = Files.asByteSink( file );
                    byteSource.copyTo( byteSink );
                }
            }
        }
        finally
        {
            unlock( path );
        }

        return byteSource;
    }


    private static void lock( Path path )
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
                }

                //Else this path is already used
                else
                {
                    //Waits
                    try
                    {
                        condition.await();
                    }
                    catch ( InterruptedException e )
                    {
                        throw Exceptions.unchecked( e );
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
            //Marks the path as free
            final Condition condition = CONDITION_MAP.remove( path );

            //Wakes up the waiters
            condition.signalAll();
        }
        finally
        {
            LOCK.unlock();
        }
    }
}
