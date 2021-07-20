package com.enonic.xp.scheduler;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
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

        final SchedulerName name = SchedulerName.from( "schedulerName" );
        assertEquals( name, CreateScheduledJobParams.create().
            name( name ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            payload( new PropertyTree() ).
            build().
            getName() );
    }

    @Test
    public void testEmptyBuilder()
    {
        final CreateScheduledJobParams params = CreateScheduledJobParams.create().
            name( SchedulerName.from( "name" ) ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            payload( new PropertyTree() ).
            build();

        assertNull( params.getAuthor() );
        assertNull( params.getUser() );
        assertNull( params.getDescription() );
        assertNotNull( params.getPayload() );
        assertNotNull( params.getDescriptor() );
        assertNotNull( params.getCalendar() );
        assertFalse( params.isEnabled() );
    }

    @Test
    public void testBuilder()
    {
        final PrincipalKey author = PrincipalKey.ofUser( IdProviderKey.createDefault(), "author" );
        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.createDefault(), "user" );

        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.BASE, "descriptor" );

        final PropertyTree payload = new PropertyTree();
        payload.addString( "property", "value" );

        final String description = "description";

        final ScheduleCalendar calendar = mock( ScheduleCalendar.class );

        final CreateScheduledJobParams params = CreateScheduledJobParams.create().
            name( SchedulerName.from( "name" ) ).
            author( author ).
            payload( payload ).
            user( user ).
            descriptor( descriptor ).
            description( description ).
            calendar( calendar ).
            enabled( true ).
            build();

        assertEquals( author, params.getAuthor() );
        assertEquals( payload, params.getPayload() );
        assertEquals( user, params.getUser() );
        assertEquals( descriptor, params.getDescriptor() );
        assertEquals( description, params.getDescription() );
        assertEquals( calendar, params.getCalendar() );
        assertTrue( params.isEnabled() );
    }
}

