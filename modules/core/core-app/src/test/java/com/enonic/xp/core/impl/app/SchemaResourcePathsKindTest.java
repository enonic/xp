package com.enonic.xp.core.impl.app;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SchemaResourcePathsKindTest
{
    @ParameterizedTest
    @CsvSource({"enonic.yaml,Application", "cms/cms.yaml,CMS", "/cms/cms.yaml,CMS", "cms/style/style.yaml,Style",
        "cms/content-types/t.yaml,ContentType", "cms/form-fragments/f.yaml,FormFragment", "cms/mixins/m.yaml,Mixin",
        "cms/parts/p.yaml,Part", "cms/pages/pg.yaml,Page", "cms/layouts/l.yaml,Layout", "cms/macros/mc.yaml,Macro",
        "cms/content-types/cms.yaml,ContentType", "cms/parts/style.yaml,Part"})
    void expected_kind( final String path, final String kind )
    {
        assertEquals( kind, SchemaResourcePaths.expectedKind( path ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"enonic.svg", "cms/content-types/t.svg", "cms/parts/p.png", "cms/i18n/phrases/phrases.properties",
        "cms/unknown/u.yaml", "cms/other.yaml", "cms/style.yaml"})
    void no_expected_kind( final String path )
    {
        assertNull( SchemaResourcePaths.expectedKind( path ) );
    }
}
