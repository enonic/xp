import '../../api.ts';

import Principal = api.security.Principal;
import PrincipalType = api.security.PrincipalType;
import UserStore = api.security.UserStore;
import i18n = api.util.i18n;

export enum UserTreeGridItemType {
    USER_STORE,
    PRINCIPAL,
    GROUPS,
    USERS,
    ROLES
}

export class UserTreeGridItem implements api.Equitable {

    private userStore: UserStore;

    private principal: Principal;

    private type: UserTreeGridItemType;

    private modifiedTime: Date;

    constructor(builder: UserTreeGridItemBuilder) {
        this.userStore = builder.userStore;
        this.principal = builder.principal;
        this.type = builder.type;

        if (this.type === UserTreeGridItemType.PRINCIPAL) {
            this.modifiedTime = this.principal.getModifiedTime();
        }
    }

    setUserStore(userStore: UserStore) {
        this.userStore = userStore;
    }

    getUserStore(): UserStore {
        return this.userStore;
    }

    setPrincipal(principal: Principal) {
        this.principal = principal;
    }

    setType(type: UserTreeGridItemType) {
        this.type = type;
    }

    getType(): UserTreeGridItemType {
        return this.type;
    }

    getPrincipal(): Principal {
        return this.principal;
    }

    getItemDisplayName(): string {
        switch (this.type) {
        case UserTreeGridItemType.USER_STORE:
            return this.userStore.getDisplayName();

        case UserTreeGridItemType.PRINCIPAL:
            return this.principal.getDisplayName();

        case UserTreeGridItemType.ROLES:
            return i18n('field.roles');

        case UserTreeGridItemType.USERS:
            return i18n('field.users');

        case UserTreeGridItemType.GROUPS:
            return i18n('field.groups');

        }
        return '';
    }

    getDataId(): string {
        switch (this.type) {
        case UserTreeGridItemType.USER_STORE:
            return this.userStore.getKey().toString();

        case UserTreeGridItemType.PRINCIPAL:
            return this.principal.getKey().toString();

        case UserTreeGridItemType.GROUPS:
            return this.userStore.getKey().toString() + '/groups';

        case UserTreeGridItemType.ROLES:
            return '/roles';

        case UserTreeGridItemType.USERS:
            return this.userStore.getKey().toString() + '/users';
        default:
            return '';
        }

    }

    isUser(): boolean {
        return this.type === UserTreeGridItemType.USERS;
    }

    isUserGroup(): boolean {
        return this.type === UserTreeGridItemType.GROUPS;
    }

    isUserStore(): boolean {
        return this.type === UserTreeGridItemType.USER_STORE;
    }

    isRole(): boolean {
        return this.type === UserTreeGridItemType.ROLES;
    }

    isPrincipal(): boolean {
        return this.type === UserTreeGridItemType.PRINCIPAL;
    }

    hasChildren(): boolean {
        return (this.isUser() || this.isUserGroup() || this.isUserStore() || this.isRole());
    }

    equals(o: api.Equitable): boolean {
        if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserTreeGridItem)) {
            return false;
        }

        let other = <UserTreeGridItem> o;
        return this.principal === other.getPrincipal() && this.userStore === other.getUserStore();
    }

    static create(): UserTreeGridItemBuilder {
        return new UserTreeGridItemBuilder();
    }

    static fromUserStore(userStore: UserStore): UserTreeGridItem {
        return new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build();
    }

    static fromPrincipal(principal: Principal): UserTreeGridItem {
        return new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build();
    }

    static getParentType(principal: Principal): UserTreeGridItemType {
        switch (principal.getType()) {
        case PrincipalType.GROUP:
            return UserTreeGridItemType.GROUPS;
        case PrincipalType.USER:
            return UserTreeGridItemType.USERS;
        case PrincipalType.ROLE:
            return UserTreeGridItemType.ROLES;
        default:
            return null;
        }
    }
}

export class UserTreeGridItemBuilder {
    userStore: UserStore;
    principal: Principal;
    type: UserTreeGridItemType;

    setUserStore(userStore: UserStore): UserTreeGridItemBuilder {
        this.userStore = userStore;
        return this;
    }

    setPrincipal(principal: Principal): UserTreeGridItemBuilder {
        this.principal = principal;
        return this;
    }

    setType(type: UserTreeGridItemType): UserTreeGridItemBuilder {
        this.type = type;
        return this;
    }

    build(): UserTreeGridItem {
        return new UserTreeGridItem(this);
    }
}
