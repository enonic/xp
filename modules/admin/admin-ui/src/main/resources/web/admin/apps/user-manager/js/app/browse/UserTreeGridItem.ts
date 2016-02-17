module app.browse {

    import Principal = api.security.Principal;
    import UserStore = api.security.UserStore;
    import PathGuard = api.security.PathGuard;

    export enum UserTreeGridItemType {
        USER_STORE,
        PRINCIPAL,
        GROUPS,
        USERS,
        ROLES,
        PATH_GUARDS,
        PATH_GUARD
    }

    export class UserTreeGridItem implements api.Equitable {

        private userStore: UserStore;

        private principal: Principal;

        private pathGuard: PathGuard;

        private type: UserTreeGridItemType;

        private modifiedTime: Date;

        constructor(builder: UserTreeGridItemBuilder) {
            this.userStore = builder.userStore;
            this.principal = builder.principal;
            this.pathGuard = builder.pathGuard;
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

        getPathGuard(): PathGuard {
            return this.pathGuard;
        }

        getItemDisplayName(): string {
            switch (this.type) {
            case UserTreeGridItemType.USER_STORE:
                return this.userStore.getDisplayName();

            case UserTreeGridItemType.PRINCIPAL:
                return this.principal.getDisplayName();

            case UserTreeGridItemType.PATH_GUARD:
                return this.pathGuard.getDisplayName();

            case UserTreeGridItemType.ROLES:
                return 'Roles';

            case UserTreeGridItemType.USERS:
                return 'Users';

            case UserTreeGridItemType.GROUPS:
                return 'Groups';

            case UserTreeGridItemType.PATH_GUARDS:
                return 'Path guards';

            }

        }

        getDataId(): string {
            switch (this.type) {
            case UserTreeGridItemType.USER_STORE:
                return this.userStore.getKey().toString();

            case UserTreeGridItemType.PRINCIPAL:
                return this.principal.getKey().toString();

            case UserTreeGridItemType.PATH_GUARD:
                return this.pathGuard.getKey().toString();

            case UserTreeGridItemType.GROUPS:
                return this.userStore.getKey().toString() + '/groups';

            case UserTreeGridItemType.ROLES:
                return '/roles';

            case UserTreeGridItemType.USERS:
                return this.userStore.getKey().toString() + '/users';

            case UserTreeGridItemType.PATH_GUARDS:
                return '/pathguard';

            }

        }

        hasChildren(): boolean {
            return (this.type === UserTreeGridItemType.USER_STORE || this.type === UserTreeGridItemType.GROUPS ||
                    this.type === UserTreeGridItemType.ROLES || this.type === UserTreeGridItemType.USERS ||
                    this.type === UserTreeGridItemType.PATH_GUARDS);
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserTreeGridItem)) {
                return false;
            }

            var other = <UserTreeGridItem> o;
            return this.principal === other.getPrincipal() && this.userStore == other.getUserStore();
        }

    }

    export class UserTreeGridItemBuilder {
        userStore: UserStore;
        principal: Principal;
        pathGuard: PathGuard;
        type: UserTreeGridItemType;

        constructor() {
        }


        setUserStore(userStore: UserStore): UserTreeGridItemBuilder {
            this.userStore = userStore;
            return this;
        }

        setPrincipal(principal: Principal): UserTreeGridItemBuilder {
            this.principal = principal;
            return this;
        }

        setPathGuard(pathGuard: PathGuard): UserTreeGridItemBuilder {
            this.pathGuard = pathGuard;
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
}
