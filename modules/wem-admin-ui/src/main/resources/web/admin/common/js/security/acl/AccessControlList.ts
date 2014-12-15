module api.security.acl {

    export class AccessControlList implements api.Equitable, api.Cloneable {

        private entries: {[key: string]: AccessControlEntry};

        constructor(entries?: AccessControlEntry[]) {
            this.entries = {};
            if (entries) {
                this.addAll(entries);
            }
        }

        getEntries(): AccessControlEntry[] {
            var values = [];
            for (var key in this.entries) {
                if (this.entries.hasOwnProperty(key)) {
                    values.push(this.entries[key]);
                }
            }
            return values;
        }

        getEntry(principalKey: PrincipalKey): AccessControlEntry {
            return this.entries[principalKey.toString()];
        }

        add(entry: AccessControlEntry): void {
            this.entries[entry.getPrincipalKey().toString()] = entry;
        }

        addAll(entries: AccessControlEntry[]): void {
            entries.forEach((entry) => {
                this.entries[entry.getPrincipalKey().toString()] = entry;
            });
        }

        contains(principalKey: PrincipalKey): boolean {
            return this.entries.hasOwnProperty(principalKey.toString());
        }

        remove(principalKey: PrincipalKey): void {
            delete this.entries[principalKey.toString()];
        }

        toJson(): api.security.acl.AccessControlEntryJson[] {
            var acl: api.security.acl.AccessControlEntryJson[] = [];
            this.getEntries().forEach((entry: api.security.acl.AccessControlEntry) => {
                var entryJson = entry.toJson();
                acl.push(entryJson);
            });
            return acl;
        }

        toString(): string {
            return '[' + this.getEntries().map((ace) => ace.toString()).join(', ') + ']';
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, AccessControlList)) {
                return false;
            }

            var other = <AccessControlList>o;
            return api.ObjectHelper.arrayEquals(this.getEntries().sort(), other.getEntries().sort());
        }

        clone(): AccessControlList {
            var entries: AccessControlEntry[] = [];
            for (var key in this.entries) {
                if (this.entries.hasOwnProperty(key)) {
                    entries.push(this.entries[key].clone());
                }
            }
            return new AccessControlList(entries);
        }

        static fromJson(json: api.security.acl.AccessControlEntryJson[]): AccessControlList {
            var acl = new AccessControlList();
            json.forEach((entryJson: api.security.acl.AccessControlEntryJson) => {
                var entry = AccessControlEntry.fromJson(entryJson);
                acl.add(entry);
            });
            return acl;
        }
    }
}
