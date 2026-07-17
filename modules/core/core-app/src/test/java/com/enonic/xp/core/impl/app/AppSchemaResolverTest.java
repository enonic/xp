package com.enonic.xp.core.impl.app;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
            {"cms/macros/mymacro/mymacro.js", "ignored"}, {"assets/styles.yaml", "ignored"},
            {"cms/unknown/mything/mything.yaml", "ignored"}} );

        final Map<String, String> resources = AppSchemaResolver.resolve( byteSource );

        assertEquals( 9, resources.size() );
        assertEquals( "cms-descriptor", resources.get( "cms.yaml" ) );
        assertEquals( "styles", resources.get( "style/style.yaml" ) );
        assertEquals( "content-type-yaml", resources.get( "content-types/mytype/mytype.yaml" ) );
        assertEquals( "layout-yaml", resources.get( "layouts/mylayout/mylayout.yaml" ) );
        assertEquals( "macro-yml", resources.get( "macros/mymacro/mymacro.yaml" ) );
        assertEquals( "part", resources.get( "parts/mypart/mypart.yaml" ) );
        assertEquals( "page", resources.get( "pages/mypage/mypage.yaml" ) );
        assertEquals( "fragment", resources.get( "form-fragments/myfragment/myfragment.yaml" ) );
        assertEquals( "mixin", resources.get( "mixins/mymixin/mymixin.yaml" ) );
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
