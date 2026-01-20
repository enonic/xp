package com.enonic.xp.app.system.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;

@ExtendWith(MockitoExtension.class)
class VacuumListenerImplTest
{
    @Mock
    private ProgressReporter progressReporter;

    private VacuumListenerImpl vacuumListener;

    @BeforeEach
    void initialize()
    {
        vacuumListener = new VacuumListenerImpl( progressReporter );
    }

    @Test
    void taskBegin()
    {
        vacuumListener.vacuumBegin( 10 );
        vacuumListener.taskBegin( "taskName1", 5L );
        Mockito.verify( progressReporter ).progress( Mockito.refEq( ProgressReportParams.create().current( 0 ).total( 10 ).build() ) );

        vacuumListener.vacuumBegin( 20 );
        vacuumListener.taskBegin( "taskName2", 15L );
        Mockito.verify( progressReporter ).progress( Mockito.refEq( ProgressReportParams.create().current( 1 ).total( 20 ).build() ) );

        vacuumListener.vacuumBegin( 30 );
        vacuumListener.taskBegin( "taskName3", 25L );
        Mockito.verify( progressReporter ).progress( Mockito.refEq( ProgressReportParams.create().current( 2 ).total( 30 ).build() ) );
    }

    @Test
    void stepBegin()
    {
        vacuumListener.stepBegin( "stepName", 10L );
        Mockito.verify( progressReporter, Mockito.never() ).progress( Mockito.any( ProgressReportParams.class ) );
    }

    @Test
    void processed()
    {
        vacuumListener.processed( 10L );
        Mockito.verify( progressReporter, Mockito.never() ).progress( Mockito.any( ProgressReportParams.class ) );
    }

}
