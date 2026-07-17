package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.storage.spi.IndexDocumentRecord;

public record IndexStoreRequest(IndexDocumentRecord doc, String indexTypeName, String indexName)
{
}
