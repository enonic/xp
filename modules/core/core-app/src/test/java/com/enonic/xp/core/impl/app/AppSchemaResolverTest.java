package com.enonic.xp.core.impl.app;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppSchemaResolverTest
{
    @Test
    void resolve()
        throws Exception
    {
        final ByteSource byteSource = zip( new String[][]{{"cms/cms.yml", "cms-descriptor"}, {"cms/style/style.yaml", "styles"},
            {"cms/content-types/mytype/mytype.yaml", "content-type-yaml"}, {"cms/content-types/mytype/mytype.yml", "content-type-yml"},
            {"cms/layouts/mylayout/mylayout.yml", "layout-yml"}, {"cms/layouts/mylayout/mylayout.yaml", "layout-yaml"},
            {"cms/macros/mymacro/mymacro.yml", "macro-yml"}, {"cms/parts/mypart/mypart.yaml", "part"},
            {"cms/pages/mypage/mypage.yaml", "page"}, {"cms/form-fragments/myfragment/myfragment.yaml", "fragment"},
            {"cms/mixins/mymixin/mymixin.yaml", "mixin"}, {"cms/content-types/other/wrong.yaml", "ignored"},
            {"cms/macros/mymacro/mymacro.js", "ignored"}, {"cms/content-types/mytype/mytype.svg", "type-icon"},
            {"cms/parts/mypart/mypart.png", "part-icon"}, {"cms/content-types/mytype/other.svg", "ignored"},
            {"cms/pages/mypage/mypage.svg", "ignored"}, {"assets/styles.yaml", "ignored"},
            {"cms/unknown/mything/mything.yaml", "ignored"}, {"cms/i18n/phrases/phrases.properties", "phrases-default"},
            {"cms/i18n/phrases/phrases_en.properties", "phrases-en"}, {"i18n/phrases/phrases.properties", "ignored"},
            {"i18n/phrases.properties", "ignored"}, {"cms/i18n/loose.properties", "ignored"},
            {"cms/i18n/phrases/nested/deep.properties", "ignored"}, {"cms/i18n/phrases/phrases.yaml", "ignored"},
            {"enonic.yaml", "app-descriptor"}, {"enonic.svg", "app-icon"}, {"application.yaml", "ignored"}, {"application.svg", "ignored"},
            {"enonic.png", "ignored"}, {"assets/enonic.yaml", "ignored"}} );

        final Map<String, ByteSource> resources = AppSchemaResolver.resolve( byteSource );

        // the legacy folder structure is persisted flat
        assertEquals( 15, resources.size() );
        assertEquals( "app-descriptor", read( resources, "enonic.yaml" ) );
        assertEquals( "app-icon", read( resources, "enonic.svg" ) );
        assertEquals( "cms-descriptor", read( resources, "cms/cms.yaml" ) );
        assertEquals( "styles", read( resources, "cms/style/style.yaml" ) );
        assertEquals( "content-type-yaml", read( resources, "cms/content-types/mytype.yaml" ) );
        assertEquals( "layout-yaml", read( resources, "cms/layouts/mylayout.yaml" ) );
        assertEquals( "macro-yml", read( resources, "cms/macros/mymacro.yaml" ) );
        assertEquals( "part", read( resources, "cms/parts/mypart.yaml" ) );
        assertEquals( "page", read( resources, "cms/pages/mypage.yaml" ) );
        assertEquals( "fragment", read( resources, "cms/form-fragments/myfragment.yaml" ) );
        assertEquals( "mixin", read( resources, "cms/mixins/mymixin.yaml" ) );
        assertEquals( "phrases-default", read( resources, "cms/i18n/phrases/phrases.properties" ) );
        assertEquals( "phrases-en", read( resources, "cms/i18n/phrases/phrases_en.properties" ) );
        assertEquals( "type-icon", read( resources, "cms/content-types/mytype.svg" ) );
        assertEquals( "part-icon", read( resources, "cms/parts/mypart.png" ) );
    }

    @Test
    void resolve_flat()
        throws Exception
    {
        final ByteSource byteSource = zip( new String[][]{{"cms/cms.yaml", "cms-descriptor"}, {"cms/style/style.yml", "styles"},
            {"cms/style.yaml", "ignored"},
            {"cms/content-types/mytype.yml", "content-type"}, {"cms/content-types/mytype.svg", "type-icon"},
            {"cms/parts/mypart.yaml", "part"}, {"cms/parts/mypart.png", "part-icon"}, {"cms/parts/mypart/mypart.js", "ignored"},
            {"cms/pages/mypage.yaml", "page"}, {"cms/pages/mypage.svg", "ignored"}, {"cms/macros/mymacro.yaml", "macro"}} );

        final Map<String, ByteSource> resources = AppSchemaResolver.resolve( byteSource );

        assertEquals( 8, resources.size() );
        assertEquals( "cms-descriptor", read( resources, "cms/cms.yaml" ) );
        // the style descriptor keeps its folder, cms/style.yaml is not a schema resource
        assertEquals( "styles", read( resources, "cms/style/style.yaml" ) );
        assertEquals( "content-type", read( resources, "cms/content-types/mytype.yaml" ) );
        assertEquals( "type-icon", read( resources, "cms/content-types/mytype.svg" ) );
        assertEquals( "part", read( resources, "cms/parts/mypart.yaml" ) );
        assertEquals( "part-icon", read( resources, "cms/parts/mypart.png" ) );
        assertEquals( "page", read( resources, "cms/pages/mypage.yaml" ) );
        assertEquals( "macro", read( resources, "cms/macros/mymacro.yaml" ) );
    }

    @Test
    void resolve_flat_wins_over_legacy_regardless_of_order()
        throws Exception
    {
        final String[][] legacyFirst = {{"cms/parts/mypart/mypart.yaml", "legacy-part"}, {"cms/parts/mypart.yml", "flat-part"},
            {"cms/parts/mypart/mypart.svg", "legacy-icon"}, {"cms/parts/mypart.svg", "flat-icon"}};
        final String[][] flatFirst = {legacyFirst[1], legacyFirst[0], legacyFirst[3], legacyFirst[2]};

        for ( final String[][] entries : new String[][][]{legacyFirst, flatFirst} )
        {
            final Map<String, ByteSource> resources = AppSchemaResolver.resolve( zip( entries ) );

            assertEquals( 2, resources.size() );
            assertEquals( "flat-part", read( resources, "cms/parts/mypart.yaml" ) );
            assertEquals( "flat-icon", read( resources, "cms/parts/mypart.svg" ) );
        }
    }

    private static String read( final Map<String, ByteSource> resources, final String path )
        throws Exception
    {
        return resources.get( path ).asCharSource( StandardCharsets.UTF_8 ).read();
    }

    @Test
    void resolve_no_schema_resources()
        throws Exception
    {
        final ByteSource byteSource =
            zip( new String[][]{{"assets/application.yaml", "kind: \"Application\""}, {"application.png", "png"}, {"assets/app.js", "js"}} );

        assertTrue( AppSchemaResolver.resolve( byteSource ).isEmpty() );
    }

    @Test
    void resolve_normalizes_yml_to_yaml_only_for_descriptors()
        throws Exception
    {
        final ByteSource byteSource = zip( new String[][]{{"enonic.yml", "app-descriptor"}, {"cms/parts/mypart/mypart.yml", "part"},
            {"cms/parts/mypart/mypart.png", "icon"}} );

        final Map<String, ByteSource> resources = AppSchemaResolver.resolve( byteSource );

        assertEquals( 3, resources.size() );
        assertEquals( "app-descriptor", read( resources, "enonic.yaml" ) );
        assertEquals( "part", read( resources, "cms/parts/mypart.yaml" ) );
        assertEquals( "icon", read( resources, "cms/parts/mypart.png" ) );
    }

    @Test
    void resolve_legacy_app_descriptor_and_icon_persisted_as_enonic()
        throws Exception
    {
        final ByteSource byteSource = zip( new String[][]{{"application.yml", "legacy-descriptor"}, {"application.svg", "legacy-icon"}} );

        final Map<String, ByteSource> resources = AppSchemaResolver.resolve( byteSource );

        assertEquals( 2, resources.size() );
        assertEquals( "legacy-descriptor", read( resources, "enonic.yaml" ) );
        assertEquals( "legacy-icon", read( resources, "enonic.svg" ) );
    }

    @Test
    void resolve_enonic_wins_over_legacy_application_regardless_of_order()
        throws Exception
    {
        // enonic.yml wins over application.yaml: the name counts before the extension
        final String[][] legacyFirst =
            {{"application.yaml", "legacy-descriptor"}, {"enonic.yml", "descriptor"}, {"application.svg", "legacy-icon"},
                {"enonic.svg", "icon"}};
        final String[][] enonicFirst = {legacyFirst[1], legacyFirst[0], legacyFirst[3], legacyFirst[2]};

        for ( final String[][] entries : new String[][][]{legacyFirst, enonicFirst} )
        {
            final Map<String, ByteSource> resources = AppSchemaResolver.resolve( zip( entries ) );

            assertEquals( 2, resources.size() );
            assertEquals( "descriptor", read( resources, "enonic.yaml" ) );
            assertEquals( "icon", read( resources, "enonic.svg" ) );
        }
    }

    @Test
    void resolve_app_descriptor_yaml_wins_over_yml_regardless_of_order()
        throws Exception
    {
        final ByteSource ymlFirst = zip( new String[][]{{"enonic.yml", "yml"}, {"enonic.yaml", "yaml"}} );
        final ByteSource yamlFirst = zip( new String[][]{{"enonic.yaml", "yaml"}, {"enonic.yml", "yml"}} );

        assertEquals( "yaml", read( AppSchemaResolver.resolve( ymlFirst ), "enonic.yaml" ) );
        assertEquals( "yaml", read( AppSchemaResolver.resolve( yamlFirst ), "enonic.yaml" ) );
    }

    private static ByteSource zip( final String[][] entries )
        throws Exception
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream( out ))
        {
            for ( final String[] entry : entries )
            {
                zip.putNextEntry( new ZipEntry( entry[0] ) );
                zip.write( entry[1].getBytes( StandardCharsets.UTF_8 ) );
                zip.closeEntry();
            }
        }
        return ByteSource.wrap( out.toByteArray() );
    }
}