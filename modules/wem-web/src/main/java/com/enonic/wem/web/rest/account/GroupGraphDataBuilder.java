package com.enonic.wem.web.rest.account;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;

import java.util.*;

public class GroupGraphDataBuilder extends AccountGraphDataBuilder {

    private Comparator<Map<String, Object>> groupComparator;

    public GroupGraphDataBuilder() {
        groupComparator = new GroupComparator();
    }

    public Comparator<Map<String, Object>> getGroupComparator() {
        return groupComparator;
    }

    public List<Map<String, Object>> buildGraph(GroupEntity entity) {
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        setParentKey(System.currentTimeMillis() + "_" + String.valueOf(entity.getGroupKey()));
        for (GroupEntity group : entity.getAllMembersRecursively()) {
            Map<String, Object> groupMap;
            if (group.isOfType(GroupType.USER, false)) {
                groupMap = buildGraphData(group.getUser(), entity.isBuiltIn(), entity.getName());
            } else {
                groupMap = buildGraphData(group, entity.isBuiltIn(), entity.getName());
            }
            groups.add(groupMap);
        }
        Collections.sort(groups, getGroupComparator());

        Map<String, Object> userMap = buildGroupData(entity);
        groups.add(0, userMap);

        return groups;
    }

    protected Map<String, Object> buildGroupData(GroupEntity groupEntity) {
        Map<String, Object> groupMap = new HashMap<String, Object>();
        groupMap.put(ID_PARAM, getParentKey());

        groupMap.put(NAME_PARAM, groupEntity.getName());

        groupMap.put(DATA_PARAM,
                createGraphData(String.valueOf(groupEntity.getGroupKey()), groupEntity.isBuiltIn() ? "role" : "group",
                        groupEntity.isBuiltIn(), groupEntity.getName()));
        groupMap.put(ADJACENCIES_PARAM, createGraphAdjacencies(groupEntity.getMembers(false)));
        return groupMap;
    }
}

class GroupComparator implements Comparator<Map<String, Object>> {

    public int compare(Map<String, Object> group1, Map<String, Object> group2) {
        int result = 0;
        if (group1 == null && group2 != null) {
            result = 1;
        } else if (group1 != null && group2 == null) {
            result = -1;
        } else if (group1 != null && group2 != null) {
            String name1 = String.valueOf(group1.get(AccountGraphDataBuilder.NAME_PARAM)),
                    name2 = String.valueOf(group2.get(AccountGraphDataBuilder.NAME_PARAM));
            if (name1 == null && name2 != null) {
                result = 1;
            } else if (name1 != null && name2 == null) {
                result = -1;
            } else if (name1 != null && name2 != null) {
                result = name1.compareTo(name2);
            }
        }
        return result;
    }
}
