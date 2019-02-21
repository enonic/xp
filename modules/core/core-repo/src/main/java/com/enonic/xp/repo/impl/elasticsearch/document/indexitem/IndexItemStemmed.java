package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexStemmedController;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;

public class IndexItemStemmed
    extends IndexItem<IndexValueString>
{
    private final String language;

    public IndexItemStemmed( final IndexPath indexPath, final String value, final String language )
    {
        super( indexPath, IndexValue.create( value ) );
        this.language = language;
    }

    @Override
    public IndexValueTypeInterface valueType()
    {
        return IndexStemmedController.resolveIndexValueType( this.language );
    }
}
