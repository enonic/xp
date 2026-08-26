package com.enonic.xp.lib.app;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateOrUpdateApplicationDescriptorParams;
import com.enonic.xp.app.EditableApplicationDescriptor;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrUpdateApplicationDescriptorHandlerTest
    extends BaseAppHandlerTest
{
    private static final Instant ICON_TIME = Instant.parse( "2026-10-08T10:00:00Z" );

    private static final Icon SOURCE_ICON = Icon.from( new byte[]{0, 1, 3}, "image/png", ICON_TIME );

    @Test
    void testExample()
    {
        final AtomicReference<EditableApplicationDescriptor> edited = stubService( ApplicationDescriptor.create().key( ApplicationKey.from( "my_app" ) ).build() );

        runScript( "/lib/xp/examples/app/createOrUpdate.js" );
        final EditableApplicationDescriptor edit = edited.get();

        assertEquals( "My App", edit.title );
        assertEquals( "Created from a script", edit.description );
        assertEquals( "Enonic", edit.vendorName );
        assertEquals( GenericValue.newObject().put( "feature", true ).put( "limit", 42 ).build(), edit.schemaConfig );
        assertEquals( "image/svg+xml", edit.icon.getMimeType() );
        assertArrayEquals( "<svg/>".getBytes(), edit.icon.toByteArray() );
    }

    @Test
    void createsWhenMissing()
    {
        final AtomicReference<EditableApplicationDescriptor> edited = stubService( ApplicationDescriptor.create().key( ApplicationKey.from( "new_app" ) ).build() );

        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "createWhenMissing" );
        final EditableApplicationDescriptor edit = edited.get();

        assertEquals( "Fresh", edit.title );
        assertNull( edit.icon );
    }

    @Test
    void keepsUntouchedFields()
    {
        final AtomicReference<EditableApplicationDescriptor> edited = stubService( sourceDescriptor() );

        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "keepUntouched" );
        final EditableApplicationDescriptor edit = edited.get();

        assertEquals( "New title", edit.title );
        assertEquals( "app.title", edit.titleI18nKey );
        assertEquals( "Old description", edit.description );
        assertEquals( "Vendor", edit.vendorName );
        assertEquals( "https://vendor", edit.vendorUrl );
        assertEquals( "https://app", edit.url );
        assertEquals( sourceDescriptor().getSchemaConfig(), edit.schemaConfig );
        assertSame( SOURCE_ICON, edit.icon );
    }

    @Test
    void clearsWithNull()
    {
        final AtomicReference<EditableApplicationDescriptor> edited = stubService( sourceDescriptor() );

        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "clearWithNull" );
        final EditableApplicationDescriptor edit = edited.get();

        assertNull( edit.title );
        assertNull( edit.titleI18nKey );
        assertNull( edit.vendorUrl );
        assertEquals( "Old description", edit.description );
        assertTrue( edit.schemaConfig.properties().isEmpty() );
        assertSame( SOURCE_ICON, edit.icon );
    }

    @Test
    void setsConfig()
    {
        final AtomicReference<EditableApplicationDescriptor> edited = stubService( sourceDescriptor() );

        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "setConfig" );
        final EditableApplicationDescriptor edit = edited.get();

        // nulls are left out, whole numbers stay integers, nested objects and lists survive
        assertEquals( GenericValue.newObject()
                          .put( "text", "value" )
                          .put( "count", 7 )
                          .put( "ratio", 1.5 )
                          .put( "flag", false )
                          .put( "nested", GenericValue.newObject().put( "inner", "x" ).build() )
                          .put( "list", GenericValue.newList().add( GenericValue.stringValue( "a" ) ).add( GenericValue.numberValue( 2 ) ).build() )
                          .build(), edit.schemaConfig );
    }

    @Test
    void rejectsNonObjectConfig()
    {
        stubService( sourceDescriptor() );

        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "rejectNonObjectConfig" );
    }

    @Test
    void removesIcon()
    {
        final AtomicReference<EditableApplicationDescriptor> edited = stubService( sourceDescriptor() );

        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "removeIcon" );
        final EditableApplicationDescriptor edit = edited.get();

        assertNull( edit.icon );
        assertEquals( "Old title", edit.title );
    }

    @Test
    void replacesIcon()
    {
        final AtomicReference<EditableApplicationDescriptor> edited = stubService( sourceDescriptor() );

        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "replaceIcon" );
        final EditableApplicationDescriptor edit = edited.get();

        assertEquals( "image/svg+xml", edit.icon.getMimeType() );
        assertArrayEquals( "<svg>new</svg>".getBytes(), edit.icon.toByteArray() );
    }

    @Test
    void keepsIconWithSameDataAndNewMimeTypeIsAReplace()
    {
        final AtomicReference<EditableApplicationDescriptor> edited = stubService( sourceDescriptor() );

        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "changeIconMimeType" );
        final EditableApplicationDescriptor edit = edited.get();

        // the data of the current icon with another mime type is a new icon
        assertEquals( "image/svg+xml", edit.icon.getMimeType() );
        assertArrayEquals( SOURCE_ICON.toByteArray(), edit.icon.toByteArray() );
    }

    @Test
    void rejectsIconWithoutData()
    {
        stubService( sourceDescriptor() );

        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "rejectIconWithoutData" );
    }

    @Test
    void requiresKeyAndEditor()
    {
        runFunction( "/test/CreateOrUpdateApplicationDescriptorHandlerTest.js", "requireKeyAndEditor" );

        verify( applicationDescriptorService, never() ).createOrUpdate( isA( CreateOrUpdateApplicationDescriptorParams.class ) );
    }

    private static ApplicationDescriptor sourceDescriptor()
    {
        return ApplicationDescriptor.create()
            .key( ApplicationKey.from( "my_app" ) )
            .title( new LocalizedText( "Old title", "app.title" ) )
            .description( "Old description" )
            .vendorName( "Vendor" )
            .vendorUrl( "https://vendor" )
            .url( "https://app" )
            .schemaConfig( GenericValue.newObject().put( "property_1", "value_1" ).build() )
            .icon( SOURCE_ICON )
            .build();
    }

    /**
     * The service applies the editor to the given source and answers with the edited descriptor.
     *
     * @return the edit as the editor left it
     */
    private AtomicReference<EditableApplicationDescriptor> stubService( final ApplicationDescriptor source )
    {
        final AtomicReference<EditableApplicationDescriptor> edited = new AtomicReference<>();
        when( applicationDescriptorService.createOrUpdate( isA( CreateOrUpdateApplicationDescriptorParams.class ) ) ).thenAnswer( invocation -> {
            final CreateOrUpdateApplicationDescriptorParams params = invocation.getArgument( 0 );
            assertEquals( source.getKey(), params.getKey() );
            final EditableApplicationDescriptor edit = new EditableApplicationDescriptor( source );
            params.getEditor().edit( edit );
            edited.set( edit );
            final ApplicationDescriptor.Builder result = ApplicationDescriptor.create()
                .key( source.getKey() )
                .title( new LocalizedText( edit.title, edit.titleI18nKey ) )
                .description( new LocalizedText( edit.description, edit.descriptionI18nKey ) )
                .vendorName( edit.vendorName )
                .vendorUrl( edit.vendorUrl )
                .url( edit.url )
                .schemaConfig( edit.schemaConfig );
            if ( edit.icon != null )
            {
                result.icon( Icon.from( edit.icon.toByteArray(), edit.icon.getMimeType(), ICON_TIME ) );
            }
            return result.build();
        } );
        return edited;
    }
}