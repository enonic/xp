package com.enonic.wem.web.rest.userstore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.enonic.wem.web.rest.account.AccountModel;
import com.enonic.wem.web.rest.account.GroupModel;


@JsonAutoDetect
public final class UserStoreConfigModel
{

    private String key;

    private String name;

    private boolean defaultStore;

    private String connectorName;

    private String plugin;

    private String userPolicy;

    private String groupPolicy;

    private int userCount;

    private int groupCount;

    private Date created;

    private Date lastModified;

    private String configXML;

    private List<UserStoreConfigFieldModel> userFields;

    private List<AccountModel> administrators;


    public UserStoreConfigModel()
    {
        userFields = new ArrayList<UserStoreConfigFieldModel>();
        administrators = new ArrayList<AccountModel>();
    }

    @JsonCreator
    public UserStoreConfigModel( @JsonProperty("administrators") List<Map<String, String>> administrators )
    {
        this();
        if ( administrators != null )
        {
            for ( Map<String, String> administrator : administrators )
            {
                if ( administrator.containsKey( "key" ) )
                {
                    GroupModel groupModel = new GroupModel();
                    groupModel.setKey( administrator.get( "key" ) );
                    this.administrators.add( groupModel );
                }
            }
        }
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public boolean getDefaultStore()
    {
        return defaultStore;
    }

    public void setDefaultStore( boolean defaultStore )
    {
        this.defaultStore = defaultStore;
    }

    public String getConnectorName()
    {
        return connectorName;
    }

    public void setConnectorName( String connectorName )
    {
        this.connectorName = connectorName;
    }

    public String getPlugin()
    {
        return plugin;
    }

    public void setPlugin( String plugin )
    {
        this.plugin = plugin;
    }

    public String getUserPolicy()
    {
        return userPolicy;
    }

    public void setUserPolicy( String userPolicy )
    {
        this.userPolicy = userPolicy;
    }

    public String getGroupPolicy()
    {
        return groupPolicy;
    }

    public void setGroupPolicy( String groupPolicy )
    {
        this.groupPolicy = groupPolicy;
    }

    public int getUserCount()
    {
        return userCount;
    }

    public void setUserCount( int userCount )
    {
        this.userCount = userCount;
    }

    public int getGroupCount()
    {
        return groupCount;
    }

    public void setGroupCount( int groupCount )
    {
        this.groupCount = groupCount;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated( Date created )
    {
        this.created = created;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified( Date lastModified )
    {
        this.lastModified = lastModified;
    }

    public String getConfigXML()
    {
        return configXML;
    }

    public void setConfigXML( String configXML )
    {
        this.configXML = configXML;
    }

    public boolean getRemote()
    {
        return this.connectorName != UserStoreConfigModelTranslator.LOCAL_CONNECTOR_NAME;
    }

    public List<UserStoreConfigFieldModel> getUserFields()
    {
        return userFields;
    }

    public List<AccountModel> getAdministrators()
    {
        return administrators;
    }

    public void setAdministrators( List<AccountModel> administrators )
    {
        this.administrators = administrators;
    }

    public void setUserFields( List<UserStoreConfigFieldModel> userFields )
    {
        this.userFields = userFields;
    }

    public void addUserField( UserStoreConfigFieldModel userField )
    {
        this.userFields.add( userField );
    }

}
