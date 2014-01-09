package com.enonic.wem.core.content

import com.enonic.wem.api.content.data.ContentData
import com.enonic.wem.api.data.DataPath
import com.enonic.wem.api.data.Property
import com.enonic.wem.api.data.Value
import com.enonic.wem.api.entity.EntityPropertyIndexConfig
import com.enonic.wem.api.entity.PropertyIndexConfig
import spock.lang.Specification
import spock.lang.Unroll

class ContentPropertyIndexConfigVisitorTest
        extends Specification
{

    @Unroll
    def "create for #path"()
    {
        given:
        def EntityPropertyIndexConfig.Builder builder = EntityPropertyIndexConfig.newEntityIndexConfig();
        def ContentPropertyIndexConfigVisitor visitor = new ContentPropertyIndexConfigVisitor( builder );
        def ContentData contentData = new ContentData();

        def Property myProperty = contentData.addProperty( path, new Value.String( "doesNotMatter" ) );
        visitor.visit( myProperty );
        def PropertyIndexConfig config = builder.build().getPropertyIndexConfig( DataPath.from( path ) )

        expect:
        config != null
        config.enabled() == enabled
        config.fulltextEnabled() == fulltext
        config.tokenizeEnabled() == tokenized

        where:
        path                                                         | enabled | fulltext | tokenized
        "rootProperty"                                               | true    | false    | false
        ContentNodeTranslator.DISPLAY_NAME_PATH                      | true    | true     | true
        "somePath.displayName"                                       | true    | false    | false
        ContentNodeTranslator.CONTENT_DATA_PATH + "." + "someName"   | true    | true     | true
        ContentNodeTranslator.FORM_PATH + "." + "displayName"        | false   | false    | false
        ContentNodeTranslator.PAGE_CONFIG_PATH + "." + "displayName" | false   | false    | false

    }


}
