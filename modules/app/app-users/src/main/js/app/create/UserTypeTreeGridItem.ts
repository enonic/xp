import '../../api.ts';

import Principal = api.security.Principal;
import UserStore = api.security.UserStore;
import UserItem = api.security.UserItem;
import User = api.security.User;
import Group = api.security.Group;

export class UserTypeTreeGridItem implements api.Equitable {

    private userItem: UserItem;

    constructor(builder: UserTypeTreeGridItemBuilder) {
        this.userItem = builder.userItem;
    }

    getUserItem(): UserItem {
        return this.userItem;
    }

    getId(): string {
        return this.userItem.getKey().toString();
    }

    hasChildren(): boolean {
        return this.userItem instanceof User || this.userItem instanceof Group;
    }

    equals(o: api.Equitable): boolean {
        if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserTypeTreeGridItem)) {
            return false;
        }

        const other = <UserTypeTreeGridItem> o;
        return this.userItem === other.getUserItem();
    }

    static create(): UserTypeTreeGridItemBuilder {
        return new UserTypeTreeGridItemBuilder();
    }

    static fromUserItem(userItem: UserItem): UserTypeTreeGridItem {
        return new UserTypeTreeGridItemBuilder().setUserItem(userItem).build();
    }
}

export class UserTypeTreeGridItemBuilder {

    userItem: UserItem;

    setUserItem(userItem: UserItem): UserTypeTreeGridItemBuilder {
        this.userItem = userItem;
        return this;
    }

    build(): UserTypeTreeGridItem {
        return new UserTypeTreeGridItem(this);
    }
}
