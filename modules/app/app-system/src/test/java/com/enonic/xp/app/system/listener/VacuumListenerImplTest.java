package com.enonic.xp.app.system.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.task.ProgressReporter;

import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class VacuumListenerImplTest
{
    @Mock
    private ProgressReporter progressReporter;

    private VacuumListenerImpl vacuumListener;

    @BeforeEach
    public void initialize()
        throws Exception
    {
        vacuumListener = new VacuumListenerImpl( progressReporter );
    }

    @Test
    public void taskBegin()
        throws Exception
    {
        vacuumListener.vacuumBegin( 10 );
        vacuumListener.taskBegin( "taskName1", 5L );
        Mockito.verify( progressReporter, Mockito.times( 1 ) ).progress( 0, 10 );

        vacuumListener.vacuumBegin( 20 );
        vacuumListener.taskBegin( "taskName2", 15L );
        Mockito.verify( progressReporter, Mockito.times( 1 ) ).progress( 1, 20 );

        vacuumListener.vacuumBegin( 30 );
        vacuumListener.taskBegin( "taskName3", 25L );
        Mockito.verify( progressReporter, Mockito.times( 1 ) ).progress( 2, 30 );
    }

    @Test
    public void stepBegin()
        throws Exception
    {
        vacuumListener.stepBegin( "stepName", 10L );
        Mockito.verify( progressReporter, Mockito.never() ).progress( anyInt(), anyInt() );
    }

    @Test
    public void processed()
        throws Exception
    {
        vacuumListener.processed( 10L );
        Mockito.verify( progressReporter, Mockito.never() ).progress( anyInt(), anyInt() );
    }

}