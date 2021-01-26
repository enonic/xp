package com.enonic.xp.security;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface UserEditor
{
    void edit( EditableUser edit );
}
