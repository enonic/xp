package com.enonic.wem.api.support.illegalchange;


public interface IllegalChangeAware<T>
{
    /**
     * Checks whether changes between this and given to is legal.
     *
     * @param to the object to compare this with.
     * @throws IllegalChangeException if any change is illegal.
     */
    public void checkIllegalChange( T to )
        throws IllegalChangeException;
}
