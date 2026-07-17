package com.enonic.xp.repo.impl.index.document;

import com.enonic.xp.repo.impl.index.document.indexitem.IndexItems;

public record IndexDocument(String id, IndexItems data, String analyzer)
{
}