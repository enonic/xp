package com.enonic.wem.web.rest2.resource.old;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
public final class UserModel
    implements AccountModel
{
    public final static String USER_NAME = "username";

    public final static String EMAIL = "email";

    public final static String KEY = "key";

    public final static String DISPLAY_NAME = "displayName";

    public final static String USER_INFO = "info";

    public final static String USERSTORE = "userStore";

    public final static String PHOTO_NAME = "photo";

    private String key;

    public UserModel()
    {
    }

    @JsonCreator
    public UserModel( @JsonProperty(USER_NAME) String name, @JsonProperty(EMAIL) String email, @JsonProperty(KEY) String key,
                      @JsonProperty(DISPLAY_NAME) String displayName, @JsonProperty(USERSTORE) String userStore,
                      @JsonProperty(USER_INFO) Map<String, Object> userInfo )
    {
        this.name = name;
        this.email = email;
        this.key = key;
        this.displayName = displayName;
        this.userStore = userStore;
        this.userInfo = new UserInfoModel( userInfo );
        this.groups = new ArrayList<Map<String, String>>();
    }

    private String photo;

    private String name;

    private String email;

    private String qualifiedName;

    private String displayName;

    private String userStore;

    private Date lastModified;

    private boolean hasPhoto = false;

    private boolean builtIn;

    private boolean editable;

    private UserInfoModel userInfo;

    private String lastLogged;

    private List<Map<String, String>> groups;

    private List<Map<String, Object>> graph;

    public List<Map<String, String>> getGroups()
    {
        return groups;
    }

    public void setGroups( List<Map<String, String>> groups )
    {
        this.groups = groups;
    }

    public String getLastLogged()
    {
        return lastLogged;
    }

    public void setLastLogged( String lastLogged )
    {
        this.lastLogged = lastLogged;
    }

    public String getCreated()
    {
        return created;
    }

    public void setCreated( String created )
    {
        this.created = created;
    }

    private String created;

    @JsonProperty(USER_INFO)
    public UserInfoModel getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo( UserInfoModel userInfo )
    {
        this.userInfo = userInfo;
    }

    public String getAccountType()
    {
        return "user";
    }

    public void setKey( String key )
    {
        this.key = key;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public void setEmail( String email )
    {
        this.email = email;
    }


    public void setQualifiedName( String qualifiedName )
    {
        this.qualifiedName = qualifiedName;
    }


    public void setUserStore( String userStore )
    {
        this.userStore = userStore;
    }


    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }


    public void setLastModified( Date lastModified )
    {
        this.lastModified = lastModified;
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    //TODO: need unification, it duplicates name field in JSON response
    public String getUsername()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getQualifiedName()
    {
        return qualifiedName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getUserStore()
    {
        return userStore;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public boolean hasPhoto()
    {
        return this.hasPhoto;
    }

    public void setBuiltIn( boolean builtIn )
    {
        this.builtIn = builtIn;
    }

    public boolean isBuiltIn()
    {
        return builtIn;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable( boolean editable )
    {
        this.editable = editable;
    }

    public void setHasPhoto( boolean hasPhoto )
    {
        this.hasPhoto = hasPhoto;
    }

    public int compareTo( AccountModel o )
    {
        if ( o instanceof UserModel )
        {
            return this.getDisplayName().compareTo( o.getDisplayName() );
        }
        if ( o instanceof GroupModel )
        {
            return 1;
        }
        return this.getName().compareTo( o.getName() );
    }

    public String getPhoto()
    {
        return photo;
    }

    public void setPhoto( String photo )
    {
        this.photo = photo;
    }
}
