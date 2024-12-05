package com.enonic.xp.repo.impl.vacuum;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class VacuumServiceImplTest
{

    @Test
    public void runTasks()
        throws Exception
    {
        final VacuumServiceImpl service = new VacuumServiceImpl();
        service.activate( mock( VacuumConfig.class, i -> i.getMethod().getDefaultValue() ) );

        service.addTask( new VacuumTask()
        {
            @Override
            public VacuumTaskResult execute( final VacuumTaskParams params )
            {
                return VacuumTaskResult.create().processed().build();
            }

            @Override
            public int order()
            {
                return 10;
            }

            @Override
            public String name()
            {
                return "ATask";
            }

        } );

        service.addTask( new VacuumTask()
        {
            @Override
            public VacuumTaskResult execute( final VacuumTaskParams params )
            {
                return VacuumTaskResult.create().failed().build();
            }

            @Override
            public int order()
            {
                return 0;
            }

            @Override
            public String name()
            {
                return "AnotherTask";
            }
        } );

        final VacuumResult result = NodeHelper.runAsAdmin( () -> service.vacuum( VacuumParameters.create().build() ) );

        assertEquals( 2, result.getResults().size() );
    }
}
