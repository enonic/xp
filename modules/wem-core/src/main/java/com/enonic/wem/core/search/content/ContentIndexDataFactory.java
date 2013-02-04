package com.enonic.wem.core.search.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.search.IndexData;

@Component
public class ContentIndexDataFactory
{

    public IndexData create( Content content )
    {
        ContentIndexData contentIndexData = new ContentIndexData( content );

        return contentIndexData;
    }

}
