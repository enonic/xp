package com.enonic.wem.api.entity

import com.enonic.wem.api.data.DataPath
import spock.lang.Specification

class EntityPatternIndexConfigTest
        extends Specification
{
    def "test patterns"()
    {
        given:
        final EntityIndexConfig config = EntityIndexConfig.newPatternIndexConfig().
                addConfig( EntityPatternIndexConfig.newConfig().
                                   propertyIndexConfig( allConfig() ).
                                   path( DataPath.from( "data" ) ).
                                   build() ).
                addConfig( EntityPatternIndexConfig.newConfig().
                                   propertyIndexConfig( nonConfig() ).
                                   path( DataPath.from( "page" ) ).
                                   build() ).
                addConfig( EntityPatternIndexConfig.newConfig().
                                   propertyIndexConfig( allConfig() ).
                                   path( DataPath.from( "page.displayname" ) ).
                                   build() ).
                addConfig( EntityPatternIndexConfig.newConfig().
                                   propertyIndexConfig( nonConfig() ).
                                   path( DataPath.from( "page.displayname.exclude" ) ).
                                   build() ).
                addConfig( EntityPatternIndexConfig.newConfig().
                                   propertyIndexConfig( allConfig() ).
                                   path( DataPath.from( "page.displayname.exclude.others" ) ).
                                   build() ).
                defaultConfig( defaultConfig() ).
                build();


        expect:
        expected.equals( config.getPropertyIndexConfig( DataPath.from( path ) ) )

        where:
        path                                       | expected
        "dummy"                                    | defaultConfig()
        "page.test.dummy"                          | nonConfig()
        "page.displayname"                         | allConfig()
        "page.displayname.exclude"                 | nonConfig()
        "page.displayname.exclude.me"              | nonConfig()
        "page.displayname.exclude.others"          | allConfig()
        "page.displayname.exclude.others.not.this" | allConfig()
        "data.test.dummy"                          | allConfig()
        "form.test.dummy"                          | defaultConfig()
    }

    private PropertyIndexConfig defaultConfig()
    {
        return PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                fulltextEnabled( false ).
                tokenizedEnabled( false ).
                build();
    }

    private PropertyIndexConfig nonConfig()
    {
        return PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG
    }

    private PropertyIndexConfig allConfig()
    {
        return PropertyIndexConfig.INDEXALL_PROPERTY_CONFIG
    }
}
