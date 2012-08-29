package com.enonic.wem.web.rest.account;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonAutoDetect
public class GroupModel
        implements AccountModel {

    private String key;

    private String name;

    private String qualifiedName;

    private String displayName;

    private String userStore;

    private Date lastModified;

    private String lastLogged;

    private List<AccountModel> members;

    private int membersCount;

    private boolean builtIn;

    private boolean editable;

    private String description;

    private Boolean isPublic;

    private boolean restricted;

    public GroupModel() {
        this.members = new ArrayList<AccountModel>();
    }

    @JsonCreator
    public GroupModel(@JsonProperty("members") List<Map<String, String>> members) {
        this();
        if (members != null) {
            for (Map<String, String> member : members) {
                if (member.containsKey("key")) {
                    GroupModel groupModel = new GroupModel();
                    groupModel.setKey(member.get("key"));
                    this.members.add(groupModel);
                }
            }
        }
    }

    public String getAccountType() {
        return builtIn ? "role" : "group";
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getQualifiedName() {
        return this.qualifiedName;
    }

    public void setUserStore(String userStore) {
        this.userStore = userStore;
    }

    public String getUserStore() {
        return this.userStore;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public boolean hasPhoto() {
        return false;
    }

    public List<AccountModel> getMembers() {
        return members;
    }

    public void setMembers(List<AccountModel> members) {
        this.members = members;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public void setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
    }

    public boolean isBuiltIn() {
        return this.builtIn;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public int compareTo(AccountModel o) {
        if (o instanceof GroupModel) {
            return this.getDisplayName().compareTo(o.getDisplayName());
        }
        if (o instanceof UserModel) {
            return -1;
        }
        return this.getName().compareTo(o.getName());
    }

    public String getLastLogged() {
        return lastLogged;
    }

    public void setLastLogged(String lastLogged) {
        this.lastLogged = lastLogged;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }
}
