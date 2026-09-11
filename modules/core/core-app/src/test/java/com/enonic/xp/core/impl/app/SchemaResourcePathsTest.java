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
        "cms/parts/p/p.svg", "cms/macros/mc/mc.png"})
    void schema_resource_paths( final String path )
    {
        assertTrue( SchemaResourcePaths.isSchemaResourcePath( path ), path );
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
        "cms/parts/p/p.js", "cms/unknown/u/u.yaml", "cms/content-types/mytype.yaml", "cms/style.yaml", "cms/style/other.yaml",
        "i18n/phrases.properties", "i18n/phrases/phrases.properties", "cms/i18n/phrases.properties", "cms/i18n/phrases/nested/p.properties",
        "cms/i18n/phrases/phrases.yaml", "assets/cms/cms.yaml", "site/content-types/mytype/mytype.yaml", "cms", "cms/"})
    void non_schema_resource_paths( final String path )
    {
        assertFalse( SchemaResourcePaths.isSchemaResourcePath( path ), path );
    }
}
