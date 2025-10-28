package com.enonic.xp.impl.scheduler;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.scheduler.EditableScheduledJob;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class EditableScheduledJobTest
{
    @Test
    void testNull()
    {
        assertThrows( NullPointerException.class, () -> new EditableScheduledJob( null ) );
    }

    @Test
    void testEmpty()
    {
        final ScheduledJobName name = ScheduledJobName.from( "scheduledJobName" );
        final ScheduledJob source = ScheduledJob.create().
            name( name ).
            calendar( mock( ScheduleCalendar.class ) ).
            descriptor( DescriptorKey.from( "app:key" ) ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:creator" ) ).
            createdTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
            modifiedTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
            build();

        final ScheduledJob target = new EditableScheduledJob( source ).build();

        assertEquals( name, target.getName() );
        assertEquals( 0, target.getConfig().getTotalSize() );
        assertNull( target.getUser() );
        assertNull( target.getDescription() );
        assertNotNull( target.getDescriptor() );
        assertNotNull( target.getCalendar() );
        assertNotNull( target.getConfig() );
        assertFalse( source.isEnabled() );
    }

    @Test
    void testNotChanged()
    {
        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.createDefault(), "user" );

        final PrincipalKey creator = PrincipalKey.ofUser( IdProviderKey.createDefault(), "creator" );
        final PrincipalKey modifier = PrincipalKey.ofUser( IdProviderKey.createDefault(), "modifier" );
        final Instant createdTime = Instant.parse( "2021-02-25T10:44:33.170079900Z" );
        final Instant modifiedTime = Instant.parse( "2021-03-25T10:44:33.170079900Z" );

        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.BASE, "descriptor" );

        final PropertyTree config = new PropertyTree();
        config.addString( "property", "value" );

        final String description = "description";

        final ScheduledJob source = ScheduledJob.create().
            name( ScheduledJobName.from( "name" ) ).
            config( config ).
            user( user ).
            descriptor( descriptor ).
            description( description ).
            calendar( mock( ScheduleCalendar.class ) ).
            enabled( true ).
            creator( creator ).
            modifier( modifier ).
            createdTime( createdTime ).
            modifiedTime( modifiedTime ).
            build();

        final ScheduledJob target = new EditableScheduledJob( source ).build();

        assertEquals( source.getName(), target.getName() );
        assertEquals( source.getConfig(), target.getConfig() );
        assertEquals( source.getUser(), target.getUser() );
        assertEquals( source.getDescriptor(), target.getDescriptor() );
        assertEquals( source.getDescription(), target.getDescription() );
        assertEquals( source.isEnabled(), target.isEnabled() );
        assertEquals( source.getCreator(), target.getCreator() );
        assertEquals( source.getCreatedTime(), target.getCreatedTime() );
    }

    @Test
    void testChanged()
    {
        final ScheduledJob source = ScheduledJob.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "app:key" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:creator" ) ).
            createdTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
            modifiedTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
            build();

        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.createDefault(), "user" );

        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.BASE, "descriptor" );

        final PropertyTree config = new PropertyTree();
        config.addString( "property", "value" );

        final String description = "description";

        final EditableScheduledJob editable = new EditableScheduledJob( source );
        editable.config = config;
        editable.user = user;
        editable.descriptor = descriptor;
        editable.description = description;
        editable.calendar = mock( ScheduleCalendar.class );

        editable.enabled = true;

        final ScheduledJob target = editable.build();

        assertEquals( source.getName(), target.getName() );
        assertEquals( config, target.getConfig() );
        assertEquals( user, target.getUser() );
        assertEquals( descriptor, target.getDescriptor() );
        assertEquals( description, target.getDescription() );
        assertTrue( target.isEnabled() );
    }
}

