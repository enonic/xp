package com.enonic.wem.api.support.illegaledit;


/**
 * An object that are capable of knowing if an illegal edit is done.
 * Illegal edits can be meta properties like creator and createdTime,
 * or properties that are not meant to change after creation.
 *
 * @param <T>
 */
public interface IllegalEditAware<T>
{
    /**
     * Checks whether the changes between this and given to is legal from an Editor.
     *
     * @param to the object to compare this with.
     * @throws IllegalEditException if any change is illegal.
     */
    public void checkIllegalEdit( T to )
        throws IllegalEditException;
}
