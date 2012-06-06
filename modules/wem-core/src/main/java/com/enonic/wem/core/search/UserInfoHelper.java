package com.enonic.wem.core.search;

import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.user.field.UserFieldTransformer;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.core.user.field.UserInfoTransformer;

public final class UserInfoHelper
{
    public static UserInfo toUserInfo( final UserEntity entity )
    {
        final UserFieldTransformer fieldTransformer = new UserFieldTransformer();
        final UserFields fields = fieldTransformer.fromStoreableMap( entity.getFieldMap() );

        fields.setPhoto( entity.getPhoto() );

        final UserInfoTransformer infoTransformer = new UserInfoTransformer();
        return infoTransformer.toUserInfo( fields );
    }

    public static UserFields toUserFields( final UserInfo userInfo )
    {
        final UserInfoTransformer infoTransformer = new UserInfoTransformer();
        return infoTransformer.toUserFields( userInfo );
    }
}
