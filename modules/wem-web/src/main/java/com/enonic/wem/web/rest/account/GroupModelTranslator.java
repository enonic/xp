package com.enonic.wem.web.rest.account;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.*;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserStoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GroupModelTranslator
        extends ModelTranslator<GroupModel, GroupEntity> {

    private final int MAX_MEMBERS = 10;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected UserStoreDao userStoreDao;

    @Autowired
    private UserModelTranslator userTranslator;


    @Override
    public GroupModel toModel(final GroupEntity entity) {
        final GroupModel model = new GroupModel();
        model.setKey(entity.getGroupKey().toString());
        model.setName(entity.getName());
        model.setQualifiedName(entity.getQualifiedName().toString());

        // TODO: GroupEntity does not have DisplayName. Using description instead.
        // model.setDisplayName( entity.getDisplayName() );
        model.setDisplayName(entity.getName());

        // TODO: GroupEntity does not have LastModified. Using mock instead.
        // model.setLastModified( entity.getLastModified() );
        model.setLastModified(new Date());

        model.setBuiltIn(entity.isBuiltIn());
        model.setPublic(!entity.isRestricted());
        model.setDescription(entity.getDescription());
        GroupGraphDataBuilder graphBuilder = new GroupGraphDataBuilder();
        model.setGraph(graphBuilder.buildGraph(entity));
        model.setEditable(!isAuthenticatedUsersRole(entity) && !isAnonymousUsersRole(entity));
        if (entity.getUserStore() != null) {
            model.setUserStore(entity.getUserStore().getName());
        } else {
            model.setUserStore("system");
        }
        return model;
    }

    @Override
    public GroupModel toInfoModel(GroupEntity entity) {
        GroupModel model = toModel(entity);
        List<AccountModel> members = new ArrayList<AccountModel>();
        Set<GroupEntity> entityMembers = entity.getMembers(false);

        int count = 0;
        for (GroupEntity member : entityMembers) {
            AccountModel accountModel = null;
            if (member.getType().equals(GroupType.USER)) {
                accountModel = userTranslator.toInfoModel(member.getUser());
            } else {
                accountModel = toModel(member);
            }
            members.add(accountModel);
            // stop after reaching max members
            if (++count == MAX_MEMBERS) {
                break;
            }
        }
        Collections.sort(members);

        model.setMembers(members);
        model.setMembersCount(entityMembers.size());
        return model;
    }


    public StoreNewGroupCommand toNewGroupCommand(GroupModel group) {
        final StoreNewGroupCommand command = new StoreNewGroupCommand();

        UserStoreEntity userStore =
                (group.getUserStore() == null) ? null : userStoreDao.findByName(group.getUserStore());
        if (userStore == null) {
            userStore = userStoreDao.findDefaultUserStore();
        }
        command.setDescription(group.getDescription());
        command.setName(group.getName());
        command.setRestriced(!group.isPublic());
        if (userStore != null) {
            command.setUserStoreKey(userStore.getKey());
            command.setType(GroupType.USERSTORE_GROUP);
        } else {
            command.setType(GroupType.GLOBAL_GROUP);
        }

        for (AccountModel member : group.getMembers()) {
            final String memberKey = member.getKey();
            final GroupKey groupKey = getMemberGroupKey(memberKey);
            command.addMember(groupKey);
        }

        return command;
    }

    public UpdateGroupCommand toUpdateGroupCommand(GroupModel group, UserKey updater) {
        final UpdateGroupCommand command = new UpdateGroupCommand(updater, new GroupKey(group.getKey()));

        UserStoreEntity userStore =
                (group.getUserStore() == null) ? null : userStoreDao.findByName(group.getUserStore());
        if (userStore == null) {
            userStore = userStoreDao.findDefaultUserStore();
        }
        command.setDescription(group.getDescription());
        command.setName(group.getName());
        command.setRestricted(!group.isPublic());
        command.syncMembers();
        for (AccountModel member : group.getMembers()) {
            final String memberKey = member.getKey();
            final GroupKey groupKey = getMemberGroupKey(memberKey);
            final GroupEntity groupMember = securityService.getGroup(groupKey);
            command.addMember(groupMember);
        }
        return command;
    }


    public GroupKey getMemberGroupKey(final String memberKey) {
        final UserKey userKey = new UserKey(memberKey);
        final UserEntity user = securityService.getUser(userKey);
        if (user != null) {
            return user.getUserGroupKey();
        } else {
            return new GroupKey(memberKey);
        }
    }

}
