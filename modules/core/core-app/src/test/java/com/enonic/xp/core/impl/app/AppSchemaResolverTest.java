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
            {"cms/i18n/phrases/nested/deep.properties", "ignored"}, {"cms/i18n/phrases/phrases.yaml", "ignored"}} );

        final Map<String, ByteSource> resources = AppSchemaResolver.resolve( byteSource );

        assertEquals( 13, resources.size() );
        assertEquals( "cms-descriptor", read( resources, "cms.yaml" ) );
        assertEquals( "styles", read( resources, "style/style.yaml" ) );
        assertEquals( "content-type-yaml", read( resources, "content-types/mytype/mytype.yaml" ) );
        assertEquals( "layout-yaml", read( resources, "layouts/mylayout/mylayout.yaml" ) );
        assertEquals( "macro-yml", read( resources, "macros/mymacro/mymacro.yaml" ) );
        assertEquals( "part", read( resources, "parts/mypart/mypart.yaml" ) );
        assertEquals( "page", read( resources, "pages/mypage/mypage.yaml" ) );
        assertEquals( "fragment", read( resources, "form-fragments/myfragment/myfragment.yaml" ) );
        assertEquals( "mixin", read( resources, "mixins/mymixin/mymixin.yaml" ) );
        assertEquals( "phrases-default", read( resources, "i18n/phrases/phrases.properties" ) );
        assertEquals( "phrases-en", read( resources, "i18n/phrases/phrases_en.properties" ) );
        assertEquals( "type-icon", read( resources, "content-types/mytype/mytype.svg" ) );
        assertEquals( "part-icon", read( resources, "parts/mypart/mypart.png" ) );
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
        final ByteSource byteSource = zip( new String[][]{{"enonic.yaml", "kind: \"Application\""}, {"assets/app.js", "js"}} );

        assertTrue( AppSchemaResolver.resolve( byteSource ).isEmpty() );
    }

    @Test
    void resolve_normalizes_yml_to_yaml_only_for_descriptors()
        throws Exception
    {
        final ByteSource byteSource =
            zip( new String[][]{{"cms/parts/mypart/mypart.yml", "part"}, {"cms/parts/mypart/mypart.png", "icon"}} );

        final Map<String, ByteSource> resources = AppSchemaResolver.resolve( byteSource );

        assertEquals( 2, resources.size() );
        assertEquals( "part", read( resources, "parts/mypart/mypart.yaml" ) );
        assertEquals( "icon", read( resources, "parts/mypart/mypart.png" ) );
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
