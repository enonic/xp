package com.enonic.xp.security;

import com.google.common.annotations.Beta;

@Beta
public interface UserStoreEditor
{
    void edit( final EditableUserStore edit );
}
