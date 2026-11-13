package com.enonic.xp.core.impl.app;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppSchemaValidatorTest
{
    private static final ApplicationKey APP = ApplicationKey.from( "myapp" );

    @Test
    void valid_descriptors_pass()
    {
        AppSchemaValidator.validate( APP, resources( Map.of( //
            "enonic.yaml", "kind: \"Application\"\ntitle: \"My app\"\n", //
            "cms/cms.yaml", "kind: \"CMS\"\n", //
            "cms/style/style.yaml", "kind: \"Style\"\n", //
            "cms/content-types/mytype.yaml", "kind: \"ContentType\"\nsuperType: \"base:structured\"\n", //
            "cms/parts/mypart.yaml", "kind: \"Part\"\n", //
            "cms/macros/mymacro.yaml", "kind: \"Macro\"\n", //
            "cms/content-types/mytype.svg", "<svg/>", //
            "cms/i18n/phrases/phrases.properties", "a=b" ) ) );
    }

    // a descriptor is parsed the way the schema services read it, so a property the kind does not know rejects it
    @Test
    void unknown_property_is_rejected()
    {
        assertThatThrownBy( () -> AppSchemaValidator.validate( APP, resources( Map.of( //
            "cms/cms.yaml", "kind: \"CMS\"\n", //
            "cms/content-types/mytype.yaml",
            "kind: \"ContentType\"\nsuperType: \"base:structured\"\nunknownProperty: \"UNKNOWN\"\n" ) ) ) ) //
            .isInstanceOf( ApplicationBundleException.class )
            .hasMessageStartingWith( "Invalid application schema: cms/content-types/mytype.yaml: " )
            .hasMessageContaining( "unknownProperty" );
    }

    @Test
    void missing_required_property_is_rejected()
    {
        assertThatThrownBy( () -> AppSchemaValidator.validate( APP, resources( Map.of( //
            "cms/content-types/mytype.yaml", "kind: \"ContentType\"\n" ) ) ) ) //
            .isInstanceOf( ApplicationBundleException.class )
            .hasMessageContaining( "cms/content-types/mytype.yaml: Non-built-in content types must have a super type defined" );
    }

    // every invalid descriptor is reported, the kind is checked before the descriptor is parsed
    @Test
    void all_errors_are_reported()
    {
        assertThatThrownBy( () -> AppSchemaValidator.validate( APP, resources( Map.of( //
            "cms/style/style.yaml", "kind: \"CMS\"\n", //
            "cms/content-types/mytype.yaml", "kind: \"ContentType\"\n", //
            "cms/parts/mypart.yaml", "title: \"no kind\"\n", //
            "cms/pages/mypage.yaml", "- not: [a descriptor\n" ) ) ) ) //
            .isInstanceOf( ApplicationBundleException.class )
            .hasMessageContaining( "cms/style/style.yaml: invalid kind \"CMS\", expected \"Style\"" )
            .hasMessageContaining( "cms/content-types/mytype.yaml: Non-built-in content types must have a super type defined" )
            .hasMessageContaining( "cms/parts/mypart.yaml: missing kind" )
            .hasMessageContaining( "cms/pages/mypage.yaml: not a valid descriptor" );
    }

    @Test
    void application_descriptor_is_parsed()
    {
        assertThatThrownBy( () -> AppSchemaValidator.validate( APP, resources( Map.of( //
            "enonic.yaml", "kind: \"Application\"\nunknownProperty: \"UNKNOWN\"\n" ) ) ) ) //
            .isInstanceOf( ApplicationBundleException.class )
            .hasMessageStartingWith( "Invalid application schema: enonic.yaml: " )
            .hasMessageContaining( "unknownProperty" );

        AppSchemaValidator.validate( APP, resources( Map.of( "enonic.yaml", "kind: \"Application\"\n" ) ) );
    }

    private static Map<String, ByteSource> resources( final Map<String, String> resources )
    {
        return resources.entrySet()
            .stream()
            .collect( Collectors.toMap( Map.Entry::getKey,
                                                         e -> ByteSource.wrap( e.getValue().getBytes( StandardCharsets.UTF_8 ) ) ) );
    }
}
