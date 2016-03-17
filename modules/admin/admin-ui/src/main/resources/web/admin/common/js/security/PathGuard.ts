module api.security {

    export class PathGuard implements api.Equitable {
        private key: PathGuardKey;
        private displayName: string;
        private description: string;
        private userStoreKey: UserStoreKey;
        private passive: boolean;
        private paths: string[];

        constructor(builder: PathGuardBuilder) {
            this.key = builder.key;
            this.displayName = builder.displayName;
            this.description = builder.description;
            this.userStoreKey = builder.userStoreKey;
            this.passive = builder.passive;
            this.paths = builder.paths;
        }

        getKey(): PathGuardKey {
            return this.key;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getDescription(): string {
            return this.description;
        }

        getUserStoreKey(): UserStoreKey {
            return this.userStoreKey;
        }

        isPassive(): boolean {
            return this.passive;
        }

        getPaths(): string[] {
            return this.paths;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PathGuard)) {
                return false;
            }

            var other = <PathGuard> o;

            return this.key === other.key &&
                   this.displayName === other.displayName &&
                   this.description === other.description &&
                   ((!this.userStoreKey && !other.userStoreKey) || (this.userStoreKey && this.userStoreKey.equals(other.userStoreKey))) &&
                   this.passive === other.passive &&
                   ObjectHelper.anyArrayEquals(this.paths, other.paths)
        }

        clone(): PathGuard {
            return PathGuard.create().
                setKey(this.key).
                setDisplayName(this.displayName).
                setDescription(this.description).
                setUserStoreKey(this.userStoreKey).
                setPassive(this.passive).
                setPaths(this.paths.slice(0)).
                build();
        }

        static create(): PathGuardBuilder {
            return new PathGuardBuilder();
        }

        static fromJson(json: PathGuardJson): PathGuard {
            return new PathGuardBuilder().fromJson(json).build();
        }
    }

    export class PathGuardBuilder {
        key: PathGuardKey;
        displayName: string;
        description: string;
        userStoreKey: UserStoreKey;
        passive: boolean;
        paths: string[];

        constructor() {
        }

        fromJson(json: PathGuardJson): PathGuardBuilder {
            this.key = PathGuardKey.fromString(json.key);
            this.displayName = json.displayName;
            this.description = json.description;
            this.userStoreKey = json.userStoreKey ? UserStoreKey.fromString(json.userStoreKey) : null;
            this.passive = json.passive;
            this.paths = json.paths;
            return this;
        }

        setKey(key: PathGuardKey): PathGuardBuilder {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): PathGuardBuilder {
            this.displayName = displayName;
            return this;
        }

        setDescription(description: string): PathGuardBuilder {
            this.description = description;
            return this;
        }

        setUserStoreKey(userStoreKey: UserStoreKey): PathGuardBuilder {
            this.userStoreKey = userStoreKey;
            return this;
        }

        setPassive(passive: boolean): PathGuardBuilder {
            this.passive = passive;
            return this;
        }

        setPaths(paths: string[]): PathGuardBuilder {
            this.paths = paths;
            return this;
        }

        build(): PathGuard {
            return new PathGuard(this);
        }
    }
}