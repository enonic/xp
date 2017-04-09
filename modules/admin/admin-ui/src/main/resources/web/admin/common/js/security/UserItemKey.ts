module api.security {

    export class UserItemKey implements api.Equitable {

        private id: string;

        constructor(id: string) {
            api.util.assert(!api.util.StringHelper.isBlank(id), 'Id cannot be null or empty');
            this.id = id;
        }

        getId(): string {
            return this.id;
        }

        toString(): string {
            return this.id;
        }

        static fromString(str: string): UserItemKey {
            throw Error('Override me');
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserItemKey)) {
                return false;
            }

            let other = <UserItemKey>o;
            return this.id === other.id;
        }

    }
}
