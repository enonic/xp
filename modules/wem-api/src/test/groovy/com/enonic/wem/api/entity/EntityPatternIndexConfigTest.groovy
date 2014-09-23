package com.enonic.wem.api.entity

import com.enonic.wem.api.data.DataPath
import spock.lang.Specification

class EntityPatternIndexConfigTest
    extends Specification
{
    def "test patterns"()
    {
        given:
        final NodeIndexConfig config = NodeIndexConfig.newPatternIndexConfig().
            addConfig( NodePatternIndexConfig.newConfig().
                           propertyIndexConfig( allConfig() ).
                           path( DataPath.from( "data" ) ).
                           build() ).
            addConfig( NodePatternIndexConfig.newConfig().
                           propertyIndexConfig( nonConfig() ).
                           path( DataPath.from( "page" ) ).
                           build() ).
            addConfig( NodePatternIndexConfig.newConfig().
                           propertyIndexConfig( allConfig() ).
                           path( DataPath.from( "page.displayname" ) ).
                           build() ).
            addConfig( NodePatternIndexConfig.newConfig().
                           propertyIndexConfig( nonConfig() ).
                           path( DataPath.from( "page.displayname.exclude" ) ).
                           build() ).
            addConfig( NodePatternIndexConfig.newConfig().
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
        return PropertyIndexConfig.create().
            enabled( true ).
            fulltextEnabled( false ).
            nGramEnabled( false ).
            build();
    }

    private PropertyIndexConfig nonConfig()
    {
        return PropertyIndexConfig.SKIP
    }

    private PropertyIndexConfig allConfig()
    {
        return PropertyIndexConfig.FULL
    }
}
