package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;

public final class UnableToDeleteSubTypeException
    extends BaseException
{
    public UnableToDeleteSubTypeException( final QualifiedSubTypeName qualifiedSubTypeName, final String reason )
    {
        super( "Unable to delete sub type [{0}]: " + reason, qualifiedSubTypeName );
    }
}
