package com.enonic.xp.scheduler;


import java.time.Instant;

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

class ScheduledJobTest
{
    @Test
    void testName()
    {
        assertThrows( NullPointerException.class, () -> ScheduledJob.create().build() );

        final ScheduledJobName name = ScheduledJobName.from( "name" );
        assertEquals( name, ScheduledJob.create().
            name( name ).
            descriptor( DescriptorKey.from( "app:key" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:creator" ) ).
            createdTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
            modifiedTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
            build().
            getName() );
    }

    @Test
    void testEmptyBuilder()
    {
        final ScheduledJob job = ScheduledJob.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:creator" ) ).
            createdTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
            modifiedTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
            build();

        assertNull( job.getUser() );
        assertNull( job.getDescription() );
        assertNotNull( job.getConfig() );
        assertNotNull( job.getDescriptor() );
        assertNotNull( job.getCalendar() );
        assertNotNull( job.getCreatedTime() );
        assertNotNull( job.getModifiedTime() );
        assertNotNull( job.getCreator() );
        assertNotNull( job.getModifier() );
        assertFalse( job.isEnabled() );
    }

    @Test
    void testBuilder()
    {
        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.createDefault(), "user" );
        final PrincipalKey creator = PrincipalKey.ofUser( IdProviderKey.createDefault(), "creator" );
        final PrincipalKey modifier = PrincipalKey.ofUser( IdProviderKey.createDefault(), "modifier" );

        final Instant createdTime = Instant.parse( "2016-11-02T10:36:00Z" );
        final Instant modifiedTime = Instant.parse( "2020-11-02T10:36:00Z" );

        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.BASE, "descriptor" );

        final PropertyTree config = new PropertyTree();
        config.addString( "property", "value" );

        final String description = "description";

        final ScheduledJob job = ScheduledJob.create().
            name( ScheduledJobName.from( "name" ) ).
            config( config ).
            user( user ).
            descriptor( descriptor ).
            description( description ).
            calendar( mock( ScheduleCalendar.class ) ).
            enabled( true ).
            creator( creator ).modifier( modifier ).
            createdTime( createdTime ).
            modifiedTime( modifiedTime ).
            build();

        assertEquals( config, job.getConfig() );
        assertEquals( user, job.getUser() );
        assertEquals( descriptor, job.getDescriptor() );
        assertEquals( description, job.getDescription() );
        assertTrue( job.isEnabled() );
        assertEquals( creator, job.getCreator() );
        assertEquals( modifier, job.getModifier() );
        assertEquals( createdTime, job.getCreatedTime() );
        assertEquals( modifiedTime, job.getModifiedTime() );
    }
}

