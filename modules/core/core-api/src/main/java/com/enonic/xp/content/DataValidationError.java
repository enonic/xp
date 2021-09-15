package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.FormItemPath;

public class DataValidationError
    extends ValidationError
{
    private final PropertyPath path;

    public DataValidationError( final PropertyPath path, final String errorMessage, final Object... args )
    {
        super( errorMessage, args );
        this.path = Objects.requireNonNull( path );
    }

    public PropertyPath getPath()
    {
        return path;
    }
}
