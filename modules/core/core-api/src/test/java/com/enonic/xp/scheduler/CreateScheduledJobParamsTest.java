package com.enonic.xp.scheduler;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class CreateScheduledJobParamsTest
{

    @Test
    public void testName()
    {
        assertThrows( NullPointerException.class, () -> CreateScheduledJobParams.create().build() );

        final ScheduledJobName name = ScheduledJobName.from( "scheduledJobName" );
        assertEquals( name, CreateScheduledJobParams.create().
            name( name ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            config( new PropertyTree() ).
            build().
            getName() );
    }

    @Test
    public void testEmptyBuilder()
    {
        final CreateScheduledJobParams params = CreateScheduledJobParams.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            config( new PropertyTree() ).
            build();

        assertNull( params.getUser() );
        assertNull( params.getDescription() );
        assertNotNull( params.getConfig() );
        assertNotNull( params.getDescriptor() );
        assertNotNull( params.getCalendar() );
        assertFalse( params.isEnabled() );
    }

    @Test
    public void testBuilder()
    {
        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.createDefault(), "user" );

        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.BASE, "descriptor" );

        final PropertyTree config = new PropertyTree();
        config.addString( "property", "value" );

        final String description = "description";

        final ScheduleCalendar calendar = mock( ScheduleCalendar.class );

        final CreateScheduledJobParams params = CreateScheduledJobParams.create().
            name( ScheduledJobName.from( "name" ) ).
            config( config ).
            user( user ).
            descriptor( descriptor ).
            description( description ).
            calendar( calendar ).
            enabled( true ).
            build();

        assertEquals( config, params.getConfig() );
        assertEquals( user, params.getUser() );
        assertEquals( descriptor, params.getDescriptor() );
        assertEquals( description, params.getDescription() );
        assertEquals( calendar, params.getCalendar() );
        assertTrue( params.isEnabled() );
    }
}

