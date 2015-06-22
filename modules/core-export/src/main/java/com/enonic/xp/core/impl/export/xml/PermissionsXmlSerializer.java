package com.enonic.xp.core.impl.export.xml;

import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.xml.DomBuilder;

public class PermissionsXmlSerializer
{
    private DomBuilder domBuilder;

    private AccessControlList accessControlList;

    private PermissionsXmlSerializer( Builder builder )
    {
        domBuilder = builder.domBuilder;
        accessControlList = builder.accessControlList;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void serialize()
    {

        this.domBuilder.start( "permissions" );

        for ( final AccessControlEntry entry : this.accessControlList )
        {
            serialize( entry );
        }

        this.domBuilder.end();
    }

    private void serialize( final AccessControlEntry entry )
    {
        this.domBuilder.start( "principal" );
        this.domBuilder.attribute( "key", entry.getPrincipal().toString() );

        serialize( entry.getAllowedPermissions(), "allow" );
        serialize( entry.getDeniedPermissions(), "deny" );

        this.domBuilder.end();

    }

    private void serialize( final Iterable<Permission> allowedPermissions, final String type )
    {
        this.domBuilder.start( type );
        this.domBuilder.attribute( "type", "array" );

        for ( final Permission permission : allowedPermissions )
        {
            this.domBuilder.start( "value" );
            this.domBuilder.text( permission.name() );
            this.domBuilder.end();
        }
        this.domBuilder.end();
    }


    public static final class Builder
    {
        private DomBuilder domBuilder;

        private AccessControlList accessControlList;

        private Builder()
        {
        }

        public Builder domBuilder( DomBuilder domBuilder )
        {
            this.domBuilder = domBuilder;
            return this;
        }

        public Builder accessControlList( AccessControlList accessControlList )
        {
            this.accessControlList = accessControlList;
            return this;
        }

        public PermissionsXmlSerializer build()
        {
            return new PermissionsXmlSerializer( this );
        }
    }
}
