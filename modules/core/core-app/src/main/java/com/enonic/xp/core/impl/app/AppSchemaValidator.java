package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

/**
 * Validates the schema resources of an application (see {@link AppSchemaResolver}) before they are persisted. Every descriptor must
 * declare the {@code kind} its path reserves (see {@link SchemaResourcePaths#expectedKind(String)}), e.g. {@code cms/style/style.yaml}
 * must be a {@code Style}, and must parse as that kind the way the schema services read it: a property the kind does not know is an
 * error. A single invalid descriptor rejects the whole schema, so nothing of it is persisted and the application is not installed.
 */
final class AppSchemaValidator
{
    private static final YmlParserBase KIND_PARSER = new YmlParserBase();

    private static final DynamicResourceParser PARSER = new DynamicResourceParser();

    // the descriptor of a schema is persisted flat, named by the schema: cms/<root>/<name>.yaml
    private static final Pattern SCHEMA_DESCRIPTOR = Pattern.compile( "^/?" + SchemaResourceNames.CMS_ROOT_NAME + "/[^/]+/(?<name>[^/]+)\\.yaml$" );

    private AppSchemaValidator()
    {
    }

    static void validate( final ApplicationKey applicationKey, final Map<String, ByteSource> resources )
    {
        final List<String> errors = new ArrayList<>();
        resources.forEach( ( path, content ) -> {
            final String expectedKind = SchemaResourcePaths.expectedKind( path );
            if ( expectedKind == null )
            {
                return;
            }
            final String text = readText( content );
            final String kind = readKind( path, text, errors );
            if ( kind == null )
            {
                return;
            }
            if ( !expectedKind.equals( kind ) )
            {
                errors.add( String.format( "%s: invalid kind \"%s\", expected \"%s\"", path, kind, expectedKind ) );
                return;
            }
            try
            {
                parse( applicationKey, kind, path, text );
            }
            catch ( Exception e )
            {
                errors.add( String.format( "%s: %s", path, rootMessage( e ) ) );
            }
        } );

        if ( !errors.isEmpty() )
        {
            throw new ApplicationBundleException( "Invalid application schema: " + String.join( "; ", errors ) );
        }
    }

    private static void parse( final ApplicationKey applicationKey, final String kind, final String path, final String text )
    {
        switch ( kind )
        {
            case "Application" -> YmlApplicationDescriptorParser.parse( text, applicationKey ).build();
            case "CMS" -> PARSER.parseCms( applicationKey, text );
            case "Style" -> PARSER.parseStyles( applicationKey, text );
            case "ContentType" ->
                PARSER.parseSchema( ContentTypeName.from( applicationKey, schemaName( path ) ), DynamicContentSchemaType.CONTENT_TYPE, text );
            case "FormFragment" ->
                PARSER.parseSchema( FormFragmentName.from( applicationKey, schemaName( path ) ), DynamicContentSchemaType.FORM_FRAGMENT, text );
            case "Mixin" -> PARSER.parseSchema( MixinName.from( applicationKey, schemaName( path ) ), DynamicContentSchemaType.MIXIN, text );
            case "Part" -> PARSER.parseComponent( DescriptorKey.from( applicationKey, schemaName( path ) ), DynamicComponentType.PART, text );
            case "Layout" ->
                PARSER.parseComponent( DescriptorKey.from( applicationKey, schemaName( path ) ), DynamicComponentType.LAYOUT, text );
            case "Page" -> PARSER.parseComponent( DescriptorKey.from( applicationKey, schemaName( path ) ), DynamicComponentType.PAGE, text );
            case "Macro" -> PARSER.parseMacro( MacroKey.from( applicationKey, schemaName( path ) ), text );
            default -> throw new IllegalArgumentException( "unknown descriptor kind: " + kind );
        }
    }

    private static String schemaName( final String path )
    {
        final Matcher matcher = SCHEMA_DESCRIPTOR.matcher( path );
        if ( !matcher.matches() )
        {
            throw new IllegalArgumentException( "not a schema descriptor path" );
        }
        return matcher.group( "name" );
    }

    private static String readKind( final String path, final String text, final List<String> errors )
    {
        try
        {
            final String kind = KIND_PARSER.kind( text );
            if ( kind == null )
            {
                errors.add( String.format( "%s: missing kind", path ) );
            }
            return kind;
        }
        catch ( UncheckedIOException e )
        {
            errors.add( String.format( "%s: not a valid descriptor", path ) );
            return null;
        }
    }

    private static String readText( final ByteSource content )
    {
        try
        {
            return content.asCharSource( StandardCharsets.UTF_8 ).read();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    // the parsers wrap the cause of a failure, whose message names the property or value at fault
    private static String rootMessage( final Throwable e )
    {
        Throwable cause = e;
        while ( cause.getCause() != null )
        {
            cause = cause.getCause();
        }
        final String message = cause.getMessage();
        return message != null ? message : cause.getClass().getSimpleName();
    }
}
