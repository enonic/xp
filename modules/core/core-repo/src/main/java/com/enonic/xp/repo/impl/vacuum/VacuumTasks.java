package com.enonic.xp.repo.impl.vacuum;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

class VacuumTasks
    implements Iterable<VacuumTask>
{
    private final Set<VacuumTask> tasks = new ConcurrentSkipListSet<>( Comparator.comparingInt( VacuumTask::order ) );

    @Override
    public Iterator<VacuumTask> iterator()
    {
        return tasks.iterator();
    }

    public int size()
    {
        return this.tasks.size();
    }

    public void add( final VacuumTask task )
    {
        this.tasks.add( task );
    }

    public boolean remove( final VacuumTask task )
    {
        return this.tasks.remove( task );
    }
}
