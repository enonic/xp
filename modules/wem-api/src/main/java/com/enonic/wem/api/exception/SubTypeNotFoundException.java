package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;

public final class SubTypeNotFoundException
    extends BaseException
{
    public SubTypeNotFoundException( final QualifiedSubTypeName qualifiedSubTypeName )
    {
        super( "Sub type [{0}] was not found", qualifiedSubTypeName );
    }
}
