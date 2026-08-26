package com.enonic.xp.core.impl.app;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.enonic.xp.core.impl.app.SchemaNodeLayout.Kind;
import com.enonic.xp.core.impl.app.SchemaNodeLayout.Location;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SchemaNodeLayoutTest
{
    // the descriptor of every kind: the path it is shipped and served at, and the node it is kept in
    @ParameterizedTest
    @CsvSource({ //
        "cms/cms.yaml, CMS, , cms/cms", //
        "/cms/cms.yaml, CMS, , cms/cms", //
        "cms/style/style.yaml, STYLE, , cms/style", //
        "cms/content-types/article.yaml, CONTENT_TYPE, article, cms/contenttype:article", //
        "cms/form-fragments/my-fragment.yaml, FORM_FRAGMENT, my-fragment, cms/formfragment:my-fragment", //
        "cms/mixins/seo.yaml, MIXIN, seo, cms/mixin:seo", //
        "cms/parts/hero.yaml, PART, hero, cms/part:hero", //
        "cms/layouts/two-columns.yaml, LAYOUT, two-columns, cms/layout:two-columns", //
        "cms/pages/default.yaml, PAGE, default, cms/page:default", //
        "cms/macros/youtube.yaml, MACRO, youtube, cms/macro:youtube", //
        "cms/i18n/phrases/phrases_en.properties, PHRASES, phrases_en, cms/phrases:phrases_en", //
        "cms/content-types/my.type.yaml, CONTENT_TYPE, my.type, cms/contenttype:my.type"})
    void descriptors( final String resourcePath, final Kind kind, final String name, final String nodePath )
    {
        final Location location = SchemaNodeLayout.locate( resourcePath );
        assertNotNull( location, resourcePath );
        assertEquals( kind, location.kind() );
        assertEquals( name, location.name() );
        assertEquals( new NodePath( "/" + nodePath ), location.nodePath() );
        assertFalse( location.icon() );
        assertFalse( location.isApplicationNode() );

        // and back: the node name tells the kind and the name, the kind tells the path
        final Location byName = SchemaNodeLayout.locate( location.nodePath().getName() );
        assertEquals( location, byName );
        assertEquals( "/" + ( resourcePath.startsWith( "/" ) ? resourcePath.substring( 1 ) : resourcePath ), kind.resourcePath( name ) );
    }

    @ParameterizedTest
    @CsvSource({ //
        "cms/content-types/article.svg, CONTENT_TYPE, article, svg", //
        "cms/content-types/article.png, CONTENT_TYPE, article, png", //
        "cms/form-fragments/f.svg, FORM_FRAGMENT, f, svg", //
        "cms/mixins/m.svg, MIXIN, m, svg", //
        "cms/parts/hero.svg, PART, hero, svg", //
        "cms/macros/youtube.png, MACRO, youtube, png"})
    void icons( final String resourcePath, final Kind kind, final String name, final String extension )
    {
        final Location location = SchemaNodeLayout.locate( resourcePath );
        assertNotNull( location, resourcePath );
        assertEquals( kind, location.kind() );
        assertEquals( name, location.name() );
        assertTrue( location.icon() );
        assertEquals( kind.nodePath( name ), location.nodePath() );
        assertEquals( "/" + resourcePath, kind.iconPath( name, extension ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"enonic.yaml", "/enonic.yaml"})
    void application_descriptor( final String resourcePath )
    {
        final Location location = SchemaNodeLayout.locate( resourcePath );
        assertTrue( location.isApplicationNode() );
        assertEquals( NodePath.ROOT, location.nodePath() );
        assertNull( location.kind() );
        assertFalse( location.icon() );
    }

    @ParameterizedTest
    @ValueSource(strings = {"enonic.svg", "/enonic.png"})
    void application_icon( final String resourcePath )
    {
        final Location location = SchemaNodeLayout.locate( resourcePath );
        assertTrue( location.isApplicationNode() );
        assertTrue( location.icon() );
    }

    // nothing else of an application is persisted: layouts and pages have no icon, the legacy folder layout is normalized before
    @ParameterizedTest
    @ValueSource(strings = {"cms/layouts/two-columns.svg", "cms/pages/default.png", "cms/content-types/article.txt", "cms/content-types/.yaml",
        "cms/cms.yml", "cms/other.yaml", "cms/style/other.yaml", "cms/i18n/phrases.properties", "assets/app.js", "enonic.yml", "cms",
        "cms/content-types/article/article.yaml"})
    void not_persisted( final String resourcePath )
    {
        assertNull( SchemaNodeLayout.locate( resourcePath ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"contenttype", "contenttype:", "ContentType:article", "ContentType.article", "unknown:article", "cms:article", "style:", "article"})
    void not_a_schema_node_name( final String nodeName )
    {
        assertNull( SchemaNodeLayout.locate( NodeName.from( nodeName ) ) );
    }

    @ParameterizedTest
    @CsvSource({"image/svg+xml, icon.svg, svg", "image/png, icon.png, png"})
    void icon_reference( final String mimeType, final String reference, final String extension )
    {
        assertEquals( BinaryReference.from( reference ), SchemaNodeLayout.iconReference( mimeType ) );
        assertEquals( mimeType, SchemaNodeLayout.iconMimeType( BinaryReference.from( reference ) ) );
        assertEquals( extension, SchemaNodeLayout.iconExtension( BinaryReference.from( reference ) ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/jpeg", "text/plain"})
    void unsupported_icon_mime_type( final String mimeType )
    {
        assertNull( SchemaNodeLayout.iconReference( mimeType ) );
        assertNull( SchemaNodeLayout.iconMimeType( BinaryReference.from( "applicationFile" ) ) );
        assertNull( SchemaNodeLayout.iconMimeType( null ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"LAYOUT", "PAGE", "PHRASES", "CMS", "STYLE"})
    void kinds_without_icon( final Kind kind )
    {
        assertFalse( kind.hasIcon() );
        assertThrows( IllegalArgumentException.class, () -> kind.iconPath( "x", "svg" ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"CONTENT_TYPE", "PART", "MACRO"})
    void name_is_required( final Kind kind )
    {
        assertThrows( IllegalArgumentException.class, () -> kind.nodeName( null ) );
        assertThrows( IllegalArgumentException.class, () -> kind.nodeName( "" ) );
    }

    // the kind name stays the one the descriptors declare, the node name is its lower case form
    @ParameterizedTest
    @CsvSource({"CMS, CMS, cms", "STYLE, Style, style", "CONTENT_TYPE, ContentType, contenttype:x", "FORM_FRAGMENT, FormFragment, formfragment:x",
        "MIXIN, Mixin, mixin:x", "PART, Part, part:x", "LAYOUT, Layout, layout:x", "PAGE, Page, page:x", "MACRO, Macro, macro:x",
        "PHRASES, Phrases, phrases:x"})
    void kind_name_and_node_name( final Kind kind, final String kindName, final String nodeName )
    {
        assertEquals( kindName, kind.kindName() );
        assertEquals( NodeName.from( nodeName ), kind.nodeName( "x" ) );
    }
}
