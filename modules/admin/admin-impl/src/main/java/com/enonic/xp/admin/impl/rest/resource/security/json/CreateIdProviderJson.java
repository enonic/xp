package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.acl.IdProviderAccessControlEntry;
import com.enonic.xp.security.acl.IdProviderAccessControlList;

public final class CreateIdProviderJson
{
    private final CreateIdProviderParams createIdProviderParams;

    @JsonCreator
    public CreateIdProviderJson( @JsonProperty("key") final String idProviderKey, @JsonProperty("displayName") final String displayName,
                                 @JsonProperty("description") final String description,
                                 @JsonProperty("idProviderConfig") final IdProviderConfigJson idProviderConfigJson,
                                 @JsonProperty("permissions") final List<IdProviderAccessControlEntryJson> aclEntries )
    {
        final IdProviderAccessControlEntry[] idProviderAclEntries = aclEntries.stream().map( IdProviderAccessControlEntryJson::getEntry ).
            toArray( IdProviderAccessControlEntry[]::new );

        final IdProviderAccessControlList permissions = IdProviderAccessControlList.of( idProviderAclEntries );
        this.createIdProviderParams = CreateIdProviderParams.create().
            key( IdProviderKey.from( idProviderKey ) ).
            displayName( displayName ).
            description( description ).
            idProviderConfig( idProviderConfigJson == null ? null : idProviderConfigJson.getIdProviderConfig() ).
            permissions( permissions ).
            build();
    }

    @JsonIgnore
    public CreateIdProviderParams getCreateIdProviderParams()
    {
        return createIdProviderParams;
    }
}
