package com.enonic.wem.core.index.elastic;

public class IndexDocumentId
{
    final String id;

    public IndexDocumentId( final String idAsString )
    {
        this.id = idAsString;
    }

    public String getId()
    {
        return id;
    }
}
