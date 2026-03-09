package com.enonic.xp.repo.impl.elasticsearch.document;

import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;

public record IndexDocument(String id, IndexItems data, String analyzer)
{
}