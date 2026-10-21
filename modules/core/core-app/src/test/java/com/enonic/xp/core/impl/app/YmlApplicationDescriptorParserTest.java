package com.enonic.xp.core.impl.app;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlApplicationDescriptorParserTest
{
    private static final ApplicationKey MYAPP = ApplicationKey.from( "myapp" );

    @Test
    void test()
        throws Exception
    {
        final String yml = readAsString( "/descriptors/application-descriptor.yml" );

        final ApplicationDescriptor descriptor = parse( yml );
        assertNotNull( descriptor );
        assertEquals( MYAPP, descriptor.getKey() );
        assertEquals( "Brief description of the application", descriptor.getDescription() );

        final GenericValue schemaConfig = descriptor.getSchemaConfig();
        assertEquals( "value_1", schemaConfig.property( "property_1" ).asString() );
        assertEquals( "value_2", schemaConfig.property( "property_2" ).asString() );

        final Scopes scopes = descriptor.getScopes();
        assertEquals( Set.of( "myapp:write", "myapp:read" ), scopes.getIds() );
        final Scope write = scopes.get( "myapp:write" ).orElseThrow();
        assertEquals( "Change orders", write.getTitle() );
        assertEquals( "scope.write.title", write.getTitleI18nKey() );
        assertEquals( "Create and change orders", write.getDescription() );
        assertEquals( myapp, write.getApplication() );
        assertEquals( Set.of( "myapp:read" ), write.getIncludes() );
        assertFalse( write.isRequired() );
        final Scope read = scopes.get( "myapp:read" ).orElseThrow();
        assertEquals( "myapp:read", read.getTitle() );
        assertTrue( read.isRequired() );
        assertEquals( Set.of( "myapp:write", "myapp:read" ), scopes.expand( Set.of( "myapp:write" ) ) );
    }

    @Test
    void a_scope_includes_only_other_scopes_of_the_application_and_is_defined_once()
    {
        final ApplicationKey myapp = ApplicationKey.from( "myapp" );
        for ( String scopes : new String[]{"  - name: a\n    includes: [\"content\"]\n", "  - name: a\n    includes: [\"a\"]\n",
            "  - name: a\n  - name: a\n", "  - name: \"a b\"\n"} )
        {
            final ApplicationDescriptor.Builder builder =
                YmlApplicationDescriptorParser.parse( "kind: \"Application\"\nscopes:\n" + scopes, myapp ).key( myapp );
            assertThatThrownBy( builder::build ).as( scopes ).isInstanceOf( IllegalArgumentException.class );
        }
        assertTrue( YmlApplicationDescriptorParser.parse( "kind: \"Application\"\n", myapp ).key( myapp ).build().getScopes().isEmpty() );
    }

    @Test
    void an_application_cannot_define_a_scope_of_xps()
    {
        final ApplicationKey read = ApplicationKey.from( "read" );
        for ( String scopes : new String[]{"  - name: repo\n", "  - name: all\n    includes: [\"content\"]\n  - name: content\n"} )
        {
            final ApplicationDescriptor.Builder builder =
                YmlApplicationDescriptorParser.parse( "kind: \"Application\"\nscopes:\n" + scopes, read ).key( read );
            assertThatThrownBy( builder::build ).as( scopes )
                .isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "would be XP's scope [read:" );
        }
        assertEquals( Set.of( "read:orders" ), YmlApplicationDescriptorParser.parse( "kind: \"Application\"\nscopes:\n  - name: orders\n", read )
            .key( read )
            .build()
            .getScopes()
            .getIds() );
    }

    @Test
    void name_matching_application()
    {
        final ApplicationDescriptor descriptor = parse( "kind: \"Application\"\nname: \"myapp\"\ntitle: \"My app\"\n" );
        assertEquals( MYAPP, descriptor.getKey() );
        assertEquals( "My app", descriptor.getTitle() );
    }

    @Test
    void name_not_matching_application()
    {
        assertThatThrownBy( () -> parse( "kind: \"Application\"\nname: \"otherapp\"\n" ) )
            .isInstanceOf( Exception.class )
            .hasMessageContaining( "Application name \"otherapp\" in descriptor does not match application \"myapp\"" );
    }

    @Test
    void name_invalid()
    {
        assertThatThrownBy( () -> parse( "kind: \"Application\"\nname: \"my-app\"\n" ) )
            .isInstanceOf( Exception.class )
            .hasMessageContaining( "ApplicationKey is invalid: my-app" );
    }

    @Test
    void type_is_not_supported()
    {
        assertThatThrownBy( () -> parse( "kind: \"Application\"\ntype: \"Static\"\n" ) )
            .isInstanceOf( Exception.class )
            .hasMessageContaining( "\"type\"" );
    }

    private static ApplicationDescriptor parse( final String yml )
    {
        final ApplicationDescriptor.Builder descriptorBuilder = YmlApplicationDescriptorParser.parse( yml, MYAPP );
        descriptorBuilder.key( MYAPP );
        return descriptorBuilder.build();
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlApplicationDescriptorParserTest.class.getResource( name ).toURI() ),
                                 StandardCharsets.UTF_8 );
    }
}