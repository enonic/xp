package com.enonic.wem.core.index.content;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;

public class ContentDeleteDocumentFactory
{

    private ContentDeleteDocumentFactory()
    {
    }

    public static Collection<DeleteDocument> create( final ContentId contentId )
    {
        Set<DeleteDocument> deleteDocuments = Sets.newLinkedHashSet();

        deleteDocuments.add( new DeleteDocument( IndexConstants.WEM_INDEX, IndexType.CONTENT, contentId.toString() ) );
        deleteDocuments.add( new DeleteDocument( IndexConstants.WEM_INDEX, IndexType.BINARIES, contentId.toString() ) );

        return deleteDocuments;
    }
}
