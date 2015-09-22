package com.enonic.xp.core.impl.image;

@FunctionalInterface
public interface SupplierWithException<T, X extends Exception>
{
    T get() throws X;
}