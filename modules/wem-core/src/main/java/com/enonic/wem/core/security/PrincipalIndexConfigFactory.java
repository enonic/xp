package com.enonic.wem.core.security;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;

import static com.enonic.wem.core.security.PrincipalNodeTranslator.DISPLAY_NAME_KEY;
import static com.enonic.wem.core.security.PrincipalNodeTranslator.EMAIL_KEY;
import static com.enonic.wem.core.security.PrincipalNodeTranslator.LOGIN_KEY;
import static com.enonic.wem.core.security.PrincipalNodeTranslator.MEMBER_KEY;
import static com.enonic.wem.core.security.PrincipalNodeTranslator.PRINCIPAL_KEY;
import static com.enonic.wem.core.security.PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY;
import static com.enonic.wem.core.security.PrincipalNodeTranslator.USER_STORE_KEY;

class PrincipalIndexConfigFactory
{
    public static IndexConfigDocument create()
    {
        // TODO: User correct analyzer when repository system is created
        return PatternIndexConfigDocument.create().
            analyzer( ContentConstants.CONTENT_DEFAULT_ANALYZER ).
            add( DISPLAY_NAME_KEY, IndexConfig.FULLTEXT ).
            add( PRINCIPAL_TYPE_KEY, IndexConfig.MINIMAL ).
            add( USER_STORE_KEY, IndexConfig.MINIMAL ).
            add( EMAIL_KEY, IndexConfig.FULLTEXT ).
            add( LOGIN_KEY, IndexConfig.MINIMAL ).
            add( PRINCIPAL_KEY, IndexConfig.MINIMAL ).
            add( MEMBER_KEY, IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.MINIMAL ).
            build();
    }

}
