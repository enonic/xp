module api.security.acl {

    export class UserStoreAccessControlList implements api.Equitable {

        private entries: {[key: string]: UserStoreAccessControlEntry};

        constructor(entries?: UserStoreAccessControlEntry[]) {
            this.entries = {};
            if (entries) {
                this.addAll(entries);
            }
        }

        getEntries(): UserStoreAccessControlEntry[] {
            var values = [];
            for (var key in this.entries) {
                if (this.entries.hasOwnProperty(key)) {
                    values.push(this.entries[key]);
                }
            }
            return values;
        }

        getEntry(principalKey: PrincipalKey): UserStoreAccessControlEntry {
            return this.entries[principalKey.toString()];
        }

        add(entry: UserStoreAccessControlEntry): void {
            this.entries[entry.getPrincipal().getKey().toString()] = entry;
        }

        addAll(entries: UserStoreAccessControlEntry[]): void {
            entries.forEach((entry) => {
                this.entries[entry.getPrincipal().getKey().toString()] = entry;
            });
        }

        contains(principalKey: PrincipalKey): boolean {
            return this.entries.hasOwnProperty(principalKey.toString());
        }

        remove(principalKey: PrincipalKey): void {
            delete this.entries[principalKey.toString()];
        }

        toJson(): api.security.acl.UserStoreAccessControlEntryJson[] {
            var acl: api.security.acl.UserStoreAccessControlEntryJson[] = [];
            this.getEntries().forEach((entry: api.security.acl.UserStoreAccessControlEntry) => {
                var entryJson = entry.toJson();
                acl.push(entryJson);
            });
            return acl;
        }

        toString(): string {
            return '[' + this.getEntries().map((ace) => ace.toString()).join(', ') + ']';
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStoreAccessControlList)) {
                return false;
            }

            var other = <UserStoreAccessControlList>o;
            return api.ObjectHelper.arrayEquals(this.getEntries().sort(), other.getEntries().sort());
        }

        static fromJson(json: api.security.acl.UserStoreAccessControlEntryJson[]): UserStoreAccessControlList {
            var acl = new UserStoreAccessControlList();
            json.forEach((entryJson: api.security.acl.UserStoreAccessControlEntryJson) => {
                var entry = UserStoreAccessControlEntry.fromJson(entryJson);
                acl.add(entry);
            });
            return acl;
        }

        clone(): UserStoreAccessControlList {
            var result = new UserStoreAccessControlList();
            var clonedEntries = {};
            this.getEntries().forEach((item) => {
                var clonedItem = new UserStoreAccessControlEntry(item.getPrincipal().clone(), item.getAccess());
                result.add(clonedItem);
            });

            return result;
        }
    }
}
