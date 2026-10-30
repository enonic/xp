package com.enonic.xp.core.impl.content.schema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.core.impl.app.descriptor.DescriptorFacetFactoryImpl;
import com.enonic.xp.core.impl.app.descriptor.DescriptorServiceImpl;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorLoader;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorServiceImpl;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An application in development is served from its source files through its classloader: the files change under it without any
 * signal, so what is cached is checked against them on every read.
 */
class DevModeSchemaTest
    extends ApplicationTestSupport
{
    private static final String FRAGMENT = "cms/form-fragments/address/address.yaml";

    private static final String CONTENT_TYPE = "cms/content-types/withfragment/withfragment.yaml";

    private static final String PART = "cms/parts/hero/hero.yaml";

    private static final String OTHER_FRAGMENT = "cms/form-fragments/address2/address2.yaml";

    private static final ContentTypeName TYPE_NAME = ContentTypeName.from( "devapp:withfragment" );

    @TempDir
    Path sources;

    private ContentTypeService contentTypeService;

    private PartDescriptorService partDescriptorService;

    private long modified = System.currentTimeMillis();

    @Override
    protected void initialize()
        throws Exception
    {
        write( "cms/cms.yaml", "kind: \"CMS\"\n" );
        write( FRAGMENT, fragment( "street" ) );
        write( CONTENT_TYPE, "kind: \"ContentType\"\nsuperType: \"base:structured\"\ntitle:\n  text: \"With fragment\"\n" +
            "form:\n  - include: \"address\"\n" );
        write( PART, "kind: \"Part\"\ntitle:\n  text: \"Hero\"\nform:\n  - include: \"address\"\n" );
        addApplication( "devapp", sources );

        final CmsFormFragmentServiceImpl formFragmentService = new CmsFormFragmentServiceImpl( resourceService );
        this.contentTypeService = new ContentTypeServiceImpl( resourceService, applicationService, formFragmentService );

        final DescriptorServiceImpl descriptorService =
            new DescriptorServiceImpl( new DescriptorFacetFactoryImpl( applicationService, resourceService ) );
        descriptorService.addLoader( new PartDescriptorLoader( resourceService, formFragmentService ) );
        this.partDescriptorService = new PartDescriptorServiceImpl( descriptorService );
    }

    // the content type holds the fragment inlined: an edit of the fragment alone must reach it
    @Test
    void content_type_follows_an_edited_form_fragment()
    {
        assertEquals( List.of( "street" ), inputNames() );

        write( FRAGMENT, fragment( "street", "city" ) );

        assertEquals( List.of( "street", "city" ), inputNames() );
    }

    // a part, cached through the descriptor facet, holds the fragment inlined as well
    @Test
    void part_follows_an_edited_form_fragment()
    {
        assertEquals( List.of( "street" ), partInputNames() );

        write( FRAGMENT, fragment( "street", "city" ) );

        assertEquals( List.of( "street", "city" ), partInputNames() );
    }

    @Test
    void content_type_follows_its_own_edited_file()
    {
        assertEquals( List.of( "street" ), inputNames() );

        write( CONTENT_TYPE, "kind: \"ContentType\"\nsuperType: \"base:structured\"\ntitle:\n  text: \"Without fragment\"\nform: []\n" );

        assertEquals( List.of(), inputNames() );
    }

    // two fragments, each valid, both contributing an input named street: the content type is invalid once expanded, and the
    // failure is the caller's to see, not logged and left out as a content type that fails to parse
    @Test
    void content_type_with_conflicting_fragments_is_reported_on_read()
    {
        write( OTHER_FRAGMENT, fragment( "street" ) );
        write( CONTENT_TYPE, "kind: \"ContentType\"\nsuperType: \"base:structured\"\ntitle:\n  text: \"With fragments\"\n" +
            "form:\n  - include: \"address\"\n  - include: \"address2\"\n" );

        final IllegalArgumentException e = assertThrows( IllegalArgumentException.class, this::inputNames );
        assertTrue( e.getMessage().contains( "Multiple entries with same key: street=street" ), e.getMessage() );
    }

    // the same through the descriptor facet: the part is reported invalid, not replaced by the default descriptor
    @Test
    void part_with_conflicting_fragments_is_reported_on_read()
    {
        write( OTHER_FRAGMENT, fragment( "street" ) );
        write( PART, "kind: \"Part\"\ntitle:\n  text: \"Hero\"\nform:\n  - include: \"address\"\n  - include: \"address2\"\n" );

        final IllegalArgumentException e = assertThrows( IllegalArgumentException.class, this::partInputNames );
        assertTrue( e.getMessage().contains( "Multiple entries with same key: street=street" ), e.getMessage() );
    }

    // a content type that fails to parse is still logged and left out
    @Test
    void content_type_that_fails_to_parse_is_left_out()
    {
        write( CONTENT_TYPE, "kind: \"ContentType\"\ndisplayName: \"Unknown property\"\n" );

        assertNull( contentTypeService.getByName( GetContentTypeParams.from( TYPE_NAME ) ) );
        assertTrue( contentTypeService.getByApplication( TYPE_NAME.getApplicationKey() ).isEmpty() );
    }

    private List<String> inputNames()
    {
        final ContentType contentType = contentTypeService.getByName( GetContentTypeParams.from( TYPE_NAME ) );
        return names( contentType.getForm() );
    }

    private List<String> partInputNames()
    {
        return names( partDescriptorService.getByKey( DescriptorKey.from( "devapp:hero" ) ).getConfig() );
    }

    private static List<String> names( final Form form )
    {
        return StreamSupport.stream( form.spliterator(), false ).map( FormItem::getName ).toList();
    }

    private static String fragment( final String... inputs )
    {
        final StringBuilder form = new StringBuilder( "kind: \"FormFragment\"\ntitle: \"Address\"\nform:\n" );
        for ( final String input : inputs )
        {
            form.append( "  - type: \"TextLine\"\n    name: \"" ).append( input ).append( "\"\n    label: \"" ).append( input ).append( "\"\n" );
        }
        return form.toString();
    }

    // every write moves the modification time on, as an edit some time later does
    private void write( final String path, final String content )
    {
        try
        {
            final Path file = sources.resolve( path );
            Files.createDirectories( file.getParent() );
            Files.writeString( file, content );
            modified += 10_000;
            Files.setLastModifiedTime( file, FileTime.fromMillis( modified ) );
        }
        catch ( IOException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
