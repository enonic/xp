package com.enonic.wem.api.item;

public interface ItemTranslatable<T>
{
    Item toItem();

    T toObject( Item item );
}
