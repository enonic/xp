package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;

public record IndexStoreRequest(IndexDocument doc, String indexTypeName, String indexName)
{
}
