package com.enonic.wem.core.content

import com.enonic.wem.api.content.ContentName
import com.enonic.wem.api.content.ContentPath
import spock.lang.Specification

class EmbeddedNodePathFactoryTest
        extends Specification
{

    def "testCreatePath"()
    {

        expect:
        embeddedPath == EmbeddedNodePathFactory.create( ContentPath.from( contentPath ), ContentName.from( contentName ) ).toString();

        where:
        embeddedPath                                       | contentPath       | contentName
        "/content/my/content/path/__embedded/content-name" | "my/content/path" | "content-name"


    }


}
