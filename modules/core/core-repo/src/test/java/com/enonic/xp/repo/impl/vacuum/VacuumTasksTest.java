package com.enonic.xp.repo.impl.vacuum;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VacuumTasksTest
{
    @Test
    void order()
    {
        final VacuumTasks tasks = new VacuumTasks();

        tasks.add( createTask( 20 ) );
        tasks.add( createTask( 10 ) );
        tasks.add( createTask( 100 ) );
        tasks.add( createTask( 30 ) );

        final Iterator<VacuumTask> iterator = tasks.iterator();
        assertEquals( 10, iterator.next().order() );
        assertEquals( 20, iterator.next().order() );
        assertEquals( 30, iterator.next().order() );
        assertEquals( 100, iterator.next().order() );
    }

    private VacuumTask createTask( int order )
    {
        return new VacuumTask()
        {
            @Override
            public VacuumTaskResult execute( final VacuumTaskParams params )
            {
                return VacuumTaskResult.create().build();
            }

            @Override
            public int order()
            {
                return order;
            }

            @Override
            public String name()
            {
                return "someTask";
            }
        };
    }
}
