package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

/**
 * A supplier of a content named by id, which keeps the id at hand for when the content does not resolve.
 */
record IdentifiedSupplier<T>(String contentId, Supplier<T> supplier)
    implements Supplier<T>
{
    @Override
    public T get()
    {
        return supplier.get();
    }

    /**
     * @return a supplier keeping the id, or the supplier itself when there is no id
     */
    static <T> Supplier<T> of( final String contentId, final Supplier<T> supplier )
    {
        return contentId == null ? supplier : new IdentifiedSupplier<>( contentId, supplier );
    }

    /**
     * @return the id the supplier keeps, or {@code null} for one that keeps none
     */
    static String contentId( final Supplier<?> supplier )
    {
        return supplier instanceof IdentifiedSupplier<?> identified ? identified.contentId() : null;
    }
}
