package com.enonic.xp.scheduler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModifyScheduledJobParamsTest
{
    @Test
    void testInvalid()
    {
        assertThrows( NullPointerException.class, () -> ModifyScheduledJobParams.create().build() );
        assertThrows( NullPointerException.class,
                      () -> ModifyScheduledJobParams.create().name( ScheduledJobName.from( "scheduledJobName" ) ).build() );
        assertThrows( NullPointerException.class, () -> ModifyScheduledJobParams.create().editor( ( edit ) -> {
        } ).build() );
    }


    @Test
    void testBuilder()
    {
        final ScheduledJobEditor editor = edit -> {
        };
        final ScheduledJobName name = ScheduledJobName.from( "name" );

        final ModifyScheduledJobParams params = ModifyScheduledJobParams.create().
            name( name ).
            editor( editor ).
            build();

        assertEquals( name, params.getName() );
        assertSame( editor, params.getEditor() );
    }
}

