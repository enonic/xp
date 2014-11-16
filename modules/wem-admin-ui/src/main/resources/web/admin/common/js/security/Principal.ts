module api.security {

    export class Principal implements api.Equitable {

        private key: PrincipalKey;

        private displayName: string;

        private type: PrincipalType;

        private modifiedTime: Date;

        constructor(principalKey: PrincipalKey, displayName: string, type: PrincipalType, modifiedTime?: Date) {
            this.key = principalKey;
            this.displayName = displayName;
            this.type = type;
            this.modifiedTime = modifiedTime || new Date();
        }

        static fromJson(json: PrincipalJson): Principal {
            var key = PrincipalKey.fromString(json.key);
            var date = json.modifiedTime ? api.util.DateHelper.parseUTCDate(json.modifiedTime) : undefined;
            return new Principal(key, json.displayName, key.getType(), date);
        }

        getKey(): PrincipalKey {
            return this.key;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getType(): PrincipalType {
            return this.type;
        }

        isUser(): boolean {
            return this.type === PrincipalType.USER;
        }

        isGroup(): boolean {
            return this.type === PrincipalType.GROUP;
        }

        isRole(): boolean {
            return this.type === PrincipalType.ROLE;
        }

        getModifiedTime(): Date {
            return this.modifiedTime;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Principal)) {
                return false;
            }

            var other = <Principal> o;
            return this.key.equals(other.key) &&
                   this.displayName === other.displayName &&
                   this.type === other.type &&
                   api.ObjectHelper.dateEquals(this.modifiedTime, other.modifiedTime);
        }
    }
}
