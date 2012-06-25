package com.enonic.wem.web.rest.account;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import javax.annotation.Nullable;
import java.util.*;

public class UserGraphDataBuilder extends GroupGraphDataBuilder {

    public List<Map<String, Object>> buildGraph(UserEntity entity) {
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        setParentKey(System.currentTimeMillis() + "_" + String.valueOf(entity.getKey()));
        for (GroupEntity group : entity.getAllMembershipsGroups()) {
            Map<String, Object> groupMap = buildGraphData(group, entity.isBuiltIn(), entity.getName());
            groups.add(groupMap);
        }
        Collections.sort(groups, getGroupComparator());

        Map<String, Object> userMap = buildUserData(entity);
        groups.add(0, userMap);

        return groups;
    }

    protected Map<String, Object> buildUserData(UserEntity userEntity) {
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put(ID_PARAM, getParentKey());
        userMap.put(NAME_PARAM, userEntity.getDisplayName());
        userMap.put(DATA_PARAM, createGraphData(String.valueOf(userEntity.getKey()), "user", userEntity.isBuiltIn(),
                userEntity.getName()));
        userMap.put(ADJACENCIES_PARAM, createGraphAdjacencies(userEntity.getDirectMemberships()));
        return userMap;
    }

    protected List<Map<String, String>> createGraphAdjacencies(Collection<GroupEntity> memberships) {
        Collection<GroupEntity> filteredMemberships = Collections2.filter(memberships, new Predicate<GroupEntity>() {
            public boolean apply(@Nullable GroupEntity input) {
                return !input.isOfType(GroupType.USER, true);
            }
        });
        return super.createGraphAdjacencies(filteredMemberships);
    }
}
