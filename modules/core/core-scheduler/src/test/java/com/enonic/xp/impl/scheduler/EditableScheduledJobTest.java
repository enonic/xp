package com.enonic.xp.impl.scheduler;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
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

public class EditableScheduledJobTest
{
    @Test
    public void testNull()
    {
        assertThrows( NullPointerException.class, () -> new EditableScheduledJob( null ) );
    }

    @Test
    public void testEmpty()
    {
        final ScheduledJobName name = ScheduledJobName.from( "scheduledJobName" );
        final ScheduledJob source = ScheduledJob.create().
            name( name ).
            calendar( mock( ScheduleCalendar.class ) ).
            descriptor( DescriptorKey.from( "app:key" ) ).
            build();

        final ScheduledJob target = new EditableScheduledJob( source ).build();

        assertEquals( name, target.getName() );
        assertNull( target.getAuthor() );
        assertEquals( 0, target.getPayload().getTotalSize() );
        assertNull( target.getUser() );
        assertNull( target.getDescription() );
        assertNotNull( target.getDescriptor() );
        assertNotNull( target.getCalendar() );
        assertNotNull( target.getPayload() );
        assertFalse( source.isEnabled() );
    }

    @Test
    public void testNotChanged()
    {
        final PrincipalKey author = PrincipalKey.ofUser( IdProviderKey.createDefault(), "author" );
        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.createDefault(), "user" );

        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.BASE, "descriptor" );

        final PropertyTree payload = new PropertyTree();
        payload.addString( "property", "value" );

        final String description = "description";

        final ScheduledJob source = ScheduledJob.create().
            name( ScheduledJobName.from( "name" ) ).
            author( author ).
            payload( payload ).
            user( user ).
            descriptor( descriptor ).
            description( description ).
            calendar( mock( ScheduleCalendar.class ) ).
            enabled( true ).
            build();

        final ScheduledJob target = new EditableScheduledJob( source ).build();

        assertEquals( source.getName(), target.getName() );
        assertEquals( source.getAuthor(), target.getAuthor() );
        assertEquals( source.getPayload(), target.getPayload() );
        assertEquals( source.getUser(), target.getUser() );
        assertEquals( source.getDescriptor(), target.getDescriptor() );
        assertEquals( source.getDescription(), target.getDescription() );
        assertEquals( source.isEnabled(), target.isEnabled() );
    }

    @Test
    public void testChanged()
    {
        final ScheduledJob source = ScheduledJob.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "app:key" ) ).
            calendar( mock( ScheduleCalendar.class ) ).
            build();

        final PrincipalKey author = PrincipalKey.ofUser( IdProviderKey.createDefault(), "author" );
        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.createDefault(), "user" );

        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.BASE, "descriptor" );

        final PropertyTree payload = new PropertyTree();
        payload.addString( "property", "value" );

        final String description = "description";

        final EditableScheduledJob editable = new EditableScheduledJob( source );
        editable.author = author;
        editable.payload = payload;
        editable.user = user;
        editable.descriptor = descriptor;
        editable.description = description;
        editable.calendar = mock( ScheduleCalendar.class );

        editable.enabled = true;

        final ScheduledJob target = editable.build();

        assertEquals( source.getName(), target.getName() );
        assertEquals( author, target.getAuthor() );
        assertEquals( payload, target.getPayload() );
        assertEquals( user, target.getUser() );
        assertEquals( descriptor, target.getDescriptor() );
        assertEquals( description, target.getDescription() );
        assertTrue( target.isEnabled() );
    }
}

