package com.enonic.xp.admin.impl.app;

import java.util.List;

import com.google.common.annotations.Beta;

import com.enonic.xp.admin.app.AdminApplicationDescriptor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlModelParser;

@Beta
public final class XmlAdminApplicationDescriptorParser
    extends XmlModelParser<XmlAdminApplicationDescriptorParser>
{
    private AdminApplicationDescriptor.Builder builder;

    public XmlAdminApplicationDescriptorParser builder( final AdminApplicationDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "admin-application" );

        builder.name( root.getChildValue( "name" ) ).
            shortName( root.getChildValue( "short-name" ) ).
            iconUrl( root.getChildValue( "icon" ) );

        final DomElement requiredAccess = root.getChild( "allow" );
        if ( requiredAccess != null )
        {
            final List<DomElement> principals = requiredAccess.getChildren( "principal" );
            for ( DomElement principal : principals )
            {
                this.builder.addAllowedPrincipal( PrincipalKey.from( principal.getValue() ) );
            }
        }
    }
}
