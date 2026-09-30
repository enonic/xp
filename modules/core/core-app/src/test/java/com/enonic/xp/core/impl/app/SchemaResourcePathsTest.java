package com.enonic.xp.core.impl.app;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaResourcePathsTest
{
    @ParameterizedTest
    @ValueSource(strings = {"cms/cms.yaml", "cms/cms.yml", "/cms/cms.yaml", "cms/style/style.yaml", "cms/style/style.yml",
        "cms/content-types/mytype/mytype.yaml", "/cms/content-types/mytype/mytype.yml", "cms/form-fragments/f/f.yaml",
        "cms/mixins/m/m.yaml", "cms/parts/p/p.yaml", "cms/layouts/l/l.yaml", "cms/pages/pg/pg.yaml", "cms/macros/mc/mc.yml",
        "cms/i18n/phrases/phrases.properties", "cms/i18n/phrases/phrases_en_US.properties", "/cms/i18n/phrases/phrases.properties",
        "cms/content-types/mytype/mytype.svg", "cms/content-types/mytype/mytype.png", "cms/form-fragments/f/f.svg", "cms/mixins/m/m.png",
        "cms/parts/p/p.svg", "cms/macros/mc/mc.png", "enonic.yaml", "enonic.yml", "/enonic.yaml", "enonic.svg", "/enonic.svg",
        "application.yaml", "application.yml", "/application.yaml", "application.svg", "/application.svg",
        "cms/content-types/mytype.yaml", "/cms/content-types/mytype.yml",
        "cms/form-fragments/f.yaml", "cms/mixins/m.yaml", "cms/parts/p.yaml", "cms/layouts/l.yaml", "cms/pages/pg.yml",
        "cms/macros/mc.yaml", "cms/content-types/mytype.svg", "cms/form-fragments/f.png", "cms/mixins/m.svg", "cms/parts/p.png",
        "cms/macros/mc.svg"})
    void schema_resource_paths( final String path )
    {
        assertTrue( SchemaResourcePaths.isSchemaResourcePath( path ), path );
    }

    @ParameterizedTest
    @ValueSource(strings = {"enonic.yaml", "/enonic.yaml", "enonic.svg", "/enonic.svg"})
    void persisted_root_resources( final String path )
    {
        assertTrue( SchemaResourcePaths.isPersistedRootResource( path ), path );
    }

    @ParameterizedTest
    @ValueSource(strings = {"enonic.yml", "application.yaml", "cms/cms.yaml", "/cms/enonic.yaml", "enonic.png"})
    void non_persisted_root_resources( final String path )
    {
        assertFalse( SchemaResourcePaths.isPersistedRootResource( path ), path );
    }

    @ParameterizedTest
    @ValueSource(strings = {"content-types/mytype/mytype.svg", "mytype.svg"})
    void icon_mime_type_svg( final String path )
    {
        assertEquals( SchemaResourcePaths.SVG_MIME_TYPE, SchemaResourcePaths.iconMimeType( path ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"parts/p/p.png", "p.png"})
    void icon_mime_type_png( final String path )
    {
        assertEquals( SchemaResourcePaths.PNG_MIME_TYPE, SchemaResourcePaths.iconMimeType( path ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"content-types/mytype/mytype.yaml", "i18n/phrases/phrases.properties", "cms.yaml"})
    void icon_mime_type_of_non_icon( final String path )
    {
        assertNull( SchemaResourcePaths.iconMimeType( path ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"cms/content-types/mytype/other.yaml", "cms/content-types/mytype/other.svg", "cms/pages/pg/pg.svg",
        "cms/layouts/l/l.png", "cms/style/style.svg", "cms/cms.svg",
        "cms/parts/p/p.js", "cms/unknown/u/u.yaml", "cms/style/other.yaml", "cms/pages/pg.svg", "cms/layouts/l.png", "cms/parts/p.js",
        "cms/unknown/u.yaml", "cms/content-types/a/b/c.yaml", "cms/content-types/a/b.svg", "cms/style/style/style.yaml", "cms/style.yaml", "cms/style.yml",
        "cms/cms/cms.yaml", "cms/style.svg",
        "i18n/phrases.properties", "i18n/phrases/phrases.properties", "cms/i18n/phrases.properties", "cms/i18n/phrases/nested/p.properties",
        "cms/i18n/phrases/phrases.yaml", "assets/cms/cms.yaml", "site/content-types/mytype/mytype.yaml", "cms", "cms/",
        "application.png", "assets/application.yaml", "cms/application.svg", "enonic.png", "assets/enonic.yaml", "cms/enonic.yaml", "cms/enonic.svg",
        "enonic.yaml/enonic.yaml"})
    void non_schema_resource_paths( final String path )
    {
        assertFalse( SchemaResourcePaths.isSchemaResourcePath( path ), path );
    }
}
