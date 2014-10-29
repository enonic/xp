package com.enonic.wem.core.security;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.core.entity.Node;

abstract class PrincipalNodeTranslator
{
    public static final String DISPLAY_NAME_KEY = "displayName";

    public static final String PRINCIPAL_TYPE_KEY = "principalType";

    public static final String USERSTORE_KEY = "userStoreKey";

    static void addPrincipalPropertiesToDataSet( final RootDataSet rootDataSet, final Principal principal )
    {
        rootDataSet.setProperty( DISPLAY_NAME_KEY, Value.newString( principal.getDisplayName() ) );
        rootDataSet.setProperty( PRINCIPAL_TYPE_KEY, Value.newString( principal.getKey().getType() ) );
        rootDataSet.setProperty( USERSTORE_KEY, Value.newString( principal.getKey().getUserStore().toString() ) );
    }

    static void populatePrincipalProperties( final Principal.Builder principal, final Node node )
    {
        principal.key( PrincipalKeyNodeTranslator.toKey( node ) );
        principal.displayName( getDisplayNameProperty( node.data() ) );
    }

    private static String getDisplayNameProperty( final RootDataSet rootDataSet )
    {
        return getStringValue( rootDataSet, DISPLAY_NAME_KEY );
    }

    static String getStringValue( final RootDataSet rootDataSet, final String key )
    {
        if ( rootDataSet.getProperty( key ) == null )
        {
            throw new IllegalArgumentException( "Required property " + key + " not found on Node" );
        }

        return rootDataSet.getProperty( key ).getValue().asString();
    }


}
