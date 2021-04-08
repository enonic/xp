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

public class ScheduledJobTest
{
    @Test
    public void testName()
    {
        assertThrows( NullPointerException.class, () -> ScheduledJob.create().build() );

        final ScheduledJobName name = ScheduledJobName.from( "name" );
        assertEquals( name, ScheduledJob.create().
            name( name ).
            descriptor( DescriptorKey.from( "app:key" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            build().
            getName() );
    }

    @Test
    public void testEmptyBuilder()
    {
        final ScheduledJob job = ScheduledJob.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            build();

        assertNull( job.getAuthor() );
        assertNull( job.getUser() );
        assertNull( job.getDescription() );
        assertNotNull( job.getPayload() );
        assertNotNull( job.getDescriptor() );
        assertNotNull( job.getCalendar() );
        assertFalse( job.isEnabled() );
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

        final ScheduledJob job = ScheduledJob.create().
            name( ScheduledJobName.from( "name" ) ).
            author( author ).
            payload( payload ).
            user( user ).
            descriptor( descriptor ).
            description( description ).
            calendar( mock( ScheduleCalendar.class ) ).
            enabled( true ).
            build();

        assertEquals( author, job.getAuthor() );
        assertEquals( payload, job.getPayload() );
        assertEquals( user, job.getUser() );
        assertEquals( descriptor, job.getDescriptor() );
        assertEquals( description, job.getDescription() );
        assertTrue( job.isEnabled() );
    }
}

