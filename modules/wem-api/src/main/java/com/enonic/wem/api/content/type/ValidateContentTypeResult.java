package com.enonic.wem.api.content.type;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class ValidateContentTypeResult
    implements Iterable<InvalidContentTypeException>
{

    private ImmutableList<InvalidContentTypeException> exceptions;


    public InvalidContentTypeException getFirst()
    {
        return exceptions.isEmpty() ? null : exceptions.iterator().next();
    }

    public boolean hasErrors()
    {
        return !exceptions.isEmpty();
    }

    @Override
    public Iterator<InvalidContentTypeException> iterator()
    {
        return exceptions.iterator();
    }

    private ValidateContentTypeResult( ImmutableList<InvalidContentTypeException> exceptions )
    {
        this.exceptions = exceptions;
    }

    public int hashCode()
    {
        return exceptions.hashCode();
    }

    public String toString()
    {
        return exceptions.toString();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof ValidateContentTypeResult ) && this.exceptions.equals( ( (ValidateContentTypeResult) o ).exceptions );
    }


    public static ValidateContentTypeResult from( final List<InvalidContentTypeException> exceptions )
    {
        return new ValidateContentTypeResult( ImmutableList.copyOf( exceptions ) );
    }

    public static ValidateContentTypeResult from( final InvalidContentTypeException... exceptions )
    {
        return new ValidateContentTypeResult( ImmutableList.copyOf( exceptions ) );
    }

    public static ValidateContentTypeResult from( final Iterable<InvalidContentTypeException> exceptions )
    {
        return new ValidateContentTypeResult( ImmutableList.copyOf( exceptions ) );
    }

    public static ValidateContentTypeResult empty()
    {
        final ImmutableList<InvalidContentTypeException> list = ImmutableList.of();
        return new ValidateContentTypeResult( list );
    }

}
