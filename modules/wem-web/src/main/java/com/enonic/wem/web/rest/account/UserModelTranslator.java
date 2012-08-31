package com.enonic.wem.web.rest.account;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.AbstractMembershipsCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.*;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserStoreDao;
import com.enonic.wem.core.search.UserInfoHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public final class UserModelTranslator
        extends ModelTranslator<UserModel, UserEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(UserModelTranslator.class);

    private static final String SYSTEM_USERSTORE = "system";

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    protected SecurityService securityService;

    public UserModel toModel(final UserEntity entity) {
        final UserModel model = new UserModel();
        model.setKey(entity.getKey().toString());
        model.setName(entity.getName());
        model.setEmail(entity.getEmail());
        model.setQualifiedName(entity.getQualifiedName().toString());
        model.setDisplayName(entity.getDisplayName());

        // TODO: UserEntity does not have LastModified field. Using timestamp instead.
        // model.setLastModified( entity.getLastModified() );
        model.setLastModified(entity.getTimestamp().toDate());

        model.setBuiltIn(entity.isBuiltIn());

        UserStoreEntity userstore = entity.getUserStore();
        boolean isAdmin =
                userstore != null && entity.isEnterpriseAdmin() && SYSTEM_USERSTORE.equals(userstore.getName());

        model.setEditable(!(entity.isAnonymous() || isAdmin));
        //TODO: not implemented
        model.setLastLogged("01-01-2001");
        //TODO: not implemented
        model.setCreated("13-09-1998");
        model.setGroups(buildGroups(entity));
        if (userstore != null) {
            model.setUserStore(userstore.getName());
        } else {
            model.setUserStore("system");
        }

        return model;
    }

    public UserModel toInfoModel(final UserEntity entity) {
        UserModel userModel = toModel(entity);
        UserInfoModel userInfoModel = new UserInfoModel();

        UserInfo userInfo = UserInfoHelper.toUserInfo(entity);
        String birthday = null;
        if (userInfo.getBirthday() != null) {
            birthday = new SimpleDateFormat("yyyy-MM-dd").format(userInfo.getBirthday());
        }
        userInfoModel.setBirthday(birthday);
        userInfoModel.setCountry(userInfo.getCountry());
        userInfoModel.setDescription(userInfo.getDescription());
        userInfoModel.setFax(userInfo.getFax());
        userInfoModel.setFirstName(userInfo.getFirstName());
        userInfoModel.setGlobalPosition(userInfo.getGlobalPosition());
        userInfoModel.setHomePage(userInfo.getHomePage());
        if (userInfo.getHtmlEmail() != null) {
            userInfoModel.setHtmlEmail(userInfo.getHtmlEmail().toString());
        }
        userInfoModel.setInitials(userInfo.getInitials());
        userInfoModel.setLastName(userInfo.getLastName());
        if (userInfo.getLocale() != null) {
            userInfoModel.setLocale(userInfo.getLocale().toString());
        }
        userInfoModel.setMemberId(userInfo.getMemberId());
        userInfoModel.setMiddleName(userInfo.getMiddleName());
        userInfoModel.setMobile(userInfo.getMobile());
        userInfoModel.setNickName(userInfo.getOrganization());
        userInfoModel.setPersonalId(userInfo.getPersonalId());
        userInfoModel.setPhone(userInfo.getPhone());
        userInfoModel.setPrefix(userInfo.getPrefix());
        userInfoModel.setSuffix(userInfo.getSuffix());
        if (userInfo.getTimeZone() != null) {
            userInfoModel.setTimeZone(userInfo.getTimeZone().getDisplayName());
        }
        userInfoModel.setTitle(userInfo.getTitle());
        if (userInfo.getGender() != null) {
            userInfoModel.setGender(userInfo.getGender().toString());
        }
        userInfoModel.setOrganization(userInfo.getOrganization());
        for (Address address : userInfo.getAddresses()) {
            userInfoModel.getAddresses().add(toAddressModel(address));
        }
        userModel.setUserInfo(userInfoModel);
        userModel.setHasPhoto(entity.hasPhoto());
        return userModel;
    }


    public AddressModel toAddressModel(final Address address) {
        AddressModel addressModel = new AddressModel();
        addressModel.setCountry(address.getCountry());
        addressModel.setIsoCountry(address.getIsoCountry());
        addressModel.setRegion(address.getRegion());
        addressModel.setIsoRegion(address.getIsoRegion());
        addressModel.setLabel(address.getLabel());
        addressModel.setPostalAddress(address.getPostalAddress());
        addressModel.setPostalCode(address.getPostalCode());
        addressModel.setStreet(address.getStreet());
        return addressModel;
    }

    public StoreNewUserCommand toNewUserCommand(UserModel userModel) {
        StoreNewUserCommand command = new StoreNewUserCommand();
        UserInfo userInfo = fromInfoModel(userModel.getUserInfo());
        userInfo.setPhoto(readPhoto(userModel.getPhoto()));
        UserStoreEntity userStore = userStoreDao.findByName(userModel.getUserStore());
        if (userStore == null) {
            userStore = userStoreDao.findDefaultUserStore();
        }
        command.setUserFields(UserInfoHelper.toUserFields(userInfo));
        command.setUsername(userModel.getName());
        command.setDisplayName(userModel.getDisplayName());
        command.setEmail(userModel.getEmail());
        command.setPassword("11111");
        command.setUserStoreKey(userStore.getKey());
        command.setAllowAnyUserAccess(true);
        command.setStorer(securityService.getLoggedInPortalUser().getKey());

        updateUserCommandMemberships(command, userModel);

        return command;
    }

    public UpdateUserCommand toUpdateUserCommand(UserModel userModel) {
        UserStoreEntity userStore = userStoreDao.findByName(userModel.getUserStore());
        if (userStore == null) {
            userStore = userStoreDao.findDefaultUserStore();
        }
        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setDeletedStateNotDeleted();
        userSpecification.setName(userModel.getName());
        userSpecification.setUserStoreKey(userStore.getKey());
        UpdateUserCommand command = new UpdateUserCommand(new UserKey(userModel.getKey()), userSpecification);
        UserInfo userInfo = fromInfoModel(userModel.getUserInfo());
        userInfo.setPhoto(readPhoto(userModel.getPhoto()));
        command.setEmail(userModel.getEmail());
        command.setDisplayName(userModel.getDisplayName());
        command.setUserFields(UserInfoHelper.toUserFields(userInfo));
        command.setAllowUpdateSelf(true);
        command.setUpdateOpenGroupsOnly(false);
        command.setupUpdateStrategy();

        updateUserCommandMemberships(command, userModel);
        command.setSyncMemberships(true);

        return command;
    }


    private UserInfo fromInfoModel(UserInfoModel userInfoModel) {
        UserInfo userInfo = new UserInfo();
        if (userInfoModel.getBirthday() != null) {
            try {
                userInfo.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(userInfoModel.getBirthday()));
            } catch (ParseException e) {
                LOG.error("Can't parse date string: " + userInfoModel.getBirthday());
            }
        }
        if (userInfoModel.getCountry() != null) {
            userInfo.setCountry(userInfoModel.getCountry());
        }
        if (userInfoModel.getDescription() != null) {
            userInfo.setDescription(userInfoModel.getDescription());
        }
        if (userInfoModel.getFax() != null) {
            userInfo.setFax(userInfoModel.getFax());
        }
        if (userInfoModel.getFirstName() != null) {
            userInfo.setFirstName(userInfoModel.getFirstName());
        }
        if (userInfoModel.getGender() != null) {
            userInfo.setGender(Gender.valueOf(userInfoModel.getGender()));
        }
        if (userInfoModel.getGlobalPosition() != null) {
            userInfo.setGlobalPosition(userInfoModel.getGlobalPosition());
        }
        if (userInfoModel.getHomePage() != null) {
            userInfo.setHomePage(userInfoModel.getHomePage());
        }
        if (userInfoModel.getHtmlEmail() != null) {
            userInfo.setHtmlEmail(BooleanUtils.toBoolean(userInfoModel.getHtmlEmail()));
        }
        if (userInfoModel.getInitials() != null) {
            userInfo.setInitials(userInfoModel.getInitials());
        }
        if (userInfoModel.getLastName() != null) {
            userInfo.setLastName(userInfoModel.getLastName());
        }
        if (userInfoModel.getLocale() != null) {
            userInfo.setLocale(new Locale(userInfoModel.getLocale()));
        }
        if (userInfoModel.getMemberId() != null) {
            userInfo.setMemberId(userInfoModel.getMemberId());
        }
        if (userInfoModel.getMiddleName() != null) {
            userInfo.setMiddleName(userInfoModel.getMiddleName());
        }
        if (userInfoModel.getMobile() != null) {
            userInfo.setMobile(userInfoModel.getMobile());
        }
        if (userInfoModel.getNickName() != null) {
            userInfo.setNickName(userInfoModel.getNickName());
        }
        if (userInfoModel.getOrganization() != null) {
            userInfo.setOrganization(userInfoModel.getOrganization());
        }
        if (userInfoModel.getPersonalId() != null) {
            userInfo.setPersonalId(userInfoModel.getPersonalId());
        }
        if (userInfoModel.getPhone() != null) {
            userInfo.setPhone(userInfoModel.getPhone());
        }
        if (userInfoModel.getPrefix() != null) {
            userInfo.setPrefix(userInfoModel.getPrefix());
        }
        if (userInfoModel.getSuffix() != null) {
            userInfo.setSuffix(userInfoModel.getSuffix());
        }
        if (userInfoModel.getTimeZone() != null) {
            userInfo.setTimezone(TimeZone.getTimeZone(userInfoModel.getTimeZone()));
        }
        if (userInfoModel.getTitle() != null) {
            userInfo.setTitle(userInfoModel.getTitle());
        }
        if (userInfoModel.getAddresses() != null) {
            List<Address> addresses = new ArrayList<Address>();
            for (AddressModel addressModel : userInfoModel.getAddresses()) {
                addresses.add(fromAddressModel(addressModel));
            }
            userInfo.setAddresses((Address[]) addresses.toArray(new Address[addresses.size()]));
        }
        return userInfo;
    }

    private Address fromAddressModel(AddressModel addressModel) {
        Address address = new Address();
        address.setCountry(addressModel.getCountry());
        address.setIsoCountry(addressModel.getIsoCountry());
        address.setIsoRegion(addressModel.getIsoRegion());
        address.setLabel(addressModel.getLabel());
        address.setPostalAddress(addressModel.getPostalAddress());
        address.setPostalCode(addressModel.getPostalCode());
        address.setRegion(addressModel.getRegion());
        address.setStreet(addressModel.getStreet());
        return address;
    }

    private byte[] readPhoto(final String photoPath) {
        if (photoPath == null) {
            return null;
        }
        final File file = new File(photoPath);
        if (file.exists()) {
            try {
                return FileUtils.readFileToByteArray(file);
            } catch (IOException e) {
                LOG.error("Unable to read photo from file: " + file.getAbsolutePath(), e);
                return null;
            }
        } else {
            return null;
        }
    }

    private void updateUserCommandMemberships(AbstractMembershipsCommand command, UserModel userModel) {
        final List<Map<String, String>> groups = userModel.getGroups();
        if (groups != null) {
            for (Map<String, String> groupFields : groups) {
                command.addMembership(new GroupKey(groupFields.get("key")));
            }
        }
    }

    private List<Map<String, String>> buildGroups(UserEntity entity) {
        List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
        for (GroupEntity group : entity.getAllMemberships()) {
            Map<String, String> groupMap = new HashMap<String, String>();

            // TODO: Group does not have DisplayName field. Using description instead.
            // groupMap.put( "name", group.getDisplayName() );
            groupMap.put("name", group.getDescription());

            groupMap.put("qualifiedName", String.valueOf(group.getQualifiedName()));
            groupMap.put("type", group.isBuiltIn() ? "role" : "group");
            groupMap.put("key", group.getGroupKey().toString());
            groups.add(groupMap);
        }
        Collections.sort(groups, new Comparator<Map<String, String>>() {
            public int compare(Map<String, String> group1, Map<String, String> group2) {
                int result = 0;
                if (group1 == null && group2 != null) {
                    result = 1;
                } else if (group1 != null && group2 == null) {
                    result = -1;
                } else if (group1 != null && group2 != null) {
                    String name1 = group1.get("name"),
                            name2 = group2.get("name");
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
        });
        return groups;
    }

}

