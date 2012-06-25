package com.enonic.wem.web.rest.account;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;

import java.util.*;


public abstract class AccountGraphDataBuilder {

    public static final String KEY_PARAM = "key";
    public static final String TYPE_PARAM = "type";
    public static final String BUILTIN_PARAM = "builtIn";
    public static final String NAME_PARAM = "name";
    public static final String NODETO_PARAM = "nodeTo";
    public static final String DATA_PARAM = "data";
    public static final String ADJACENCIES_PARAM = "adjacencies";
    public static final String ID_PARAM = "id";

    private String parentKey;

    public AccountGraphDataBuilder() {
        parentKey = "";
    }

    protected Map<String, String> createGraphData(String key, String type, boolean builtIn, String name) {
        Map<String, String> data = new HashMap<String, String>(2);
        data.put(KEY_PARAM, key);
        data.put(TYPE_PARAM, type);
        data.put(BUILTIN_PARAM, String.valueOf(builtIn));
        data.put(NAME_PARAM, name);
        return data;
    }

    protected List<Map<String, String>> createGraphAdjacencies(Collection<GroupEntity> memberships) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>(memberships.size());
        for (GroupEntity membership : memberships) {
            if (getParentKey().contains(String.valueOf(membership.getGroupKey())))
                continue;
            String nodeId = getMemberKey(membership);
            Map<String, String> adjacencies = new HashMap<String, String>(1);
            adjacencies.put(NODETO_PARAM, nodeId);
            list.add(adjacencies);
        }
        return list;
    }


    protected Map<String, Object> buildGraphData(GroupEntity groupEntity, boolean builtIn, String name) {
        Map<String, Object> groupMap = new HashMap<String, Object>();
        String groupKey = parentKey + "_" + String.valueOf(groupEntity.getGroupKey());
        groupMap.put(ID_PARAM, groupKey);

        groupMap.put(NAME_PARAM, groupEntity.getName());

        groupMap.put(DATA_PARAM,
                createGraphData(String.valueOf(groupEntity.getGroupKey()), groupEntity.isBuiltIn() ? "role" : "group",
                        builtIn, name));
        groupMap.put(ADJACENCIES_PARAM, createGraphAdjacencies(groupEntity.getMembers(false)));
        return groupMap;
    }

    protected Map<String, Object> buildGraphData(UserEntity userEntity, boolean builtIn, String name) {
        Map<String, Object> groupMap = new HashMap<String, Object>();
        String groupKey = getParentKey() + "_" + String.valueOf(userEntity.getKey());
        groupMap.put(ID_PARAM, groupKey);

        groupMap.put(NAME_PARAM, userEntity.getName());

        groupMap.put(DATA_PARAM,
                createGraphData(String.valueOf(userEntity.getKey()), "user",
                        builtIn, name));
        return groupMap;
    }

    protected void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    protected String getParentKey() {
        return this.parentKey;
    }

    protected String getMemberKey(GroupEntity member) {
        String nodeId = getParentKey() + "_";
        if (!member.isOfType(GroupType.USER, true)) {
            nodeId += String.valueOf(member.getGroupKey());
        } else {
            nodeId += String.valueOf(member.getUser().getKey());
        }
        return nodeId;
    }
}


