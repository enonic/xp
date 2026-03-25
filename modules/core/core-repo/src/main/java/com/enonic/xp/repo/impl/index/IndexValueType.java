package com.enonic.xp.repo.impl.index;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

public interface IndexValueType
{
    String INDEX_VALUE_TYPE_SEPARATOR = ".";

    @NonNull String getPostfix();

    static IndexValueType orderBy( @NonNull String value )
    {
        return new CustomIndexValueType( "_orderby_" + requireNonNull( value ) );
    }

    static IndexValueType stemmed( @NonNull String value )
    {
        return new CustomIndexValueType( "_stemmed_" + requireNonNull( value ) );
    }

    @NullMarked
    record CustomIndexValueType(String value)
        implements IndexValueType
    {
        @Override
        public String getPostfix()
        {
            return value;
        }
    }
}
