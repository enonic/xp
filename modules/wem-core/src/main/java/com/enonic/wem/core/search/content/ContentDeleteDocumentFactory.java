package com.enonic.wem.core.search.content;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.core.search.DeleteDocument;

public class ContentDeleteDocumentFactory
{

    private ContentDeleteDocumentFactory()
    {
    }

    public static Collection<DeleteDocument> create( final ContentSelector contentSelector )
    {
        Set<DeleteDocument> deleteDocuments = Sets.newLinkedHashSet();

        if ( contentSelector instanceof ContentId )
        {

        }
        else if ( contentSelector instanceof ContentPath )
        {

        }

        //deleteDocuments.add( new DeleteDocument( IndexConstants.WEM_INDEX.value(), IndexType.CONTENT, content.getId().toString() ) );
        //deleteDocuments.add( new DeleteDocument( IndexConstants.WEM_INDEX.value(), IndexType.BINARIES, content.getId().toString() ) );

        return deleteDocuments;
    }
}
