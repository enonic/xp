module api.security {

    export class PathGuardKey implements api.Equitable {

        public static ADMIN_LOGIN: PathGuardKey = new PathGuardKey('admin');

        private id: string;

        constructor(id: string) {
            api.util.assert(!api.util.StringHelper.isBlank(id), "PathGuardKey id cannot be null or empty");
            this.id = id;
        }

        toString(): string {
            return this.id;
        }

        getId(): string {
            return this.id;
        }

        static fromString(value: string): PathGuardKey {
            return new PathGuardKey(value);
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PathGuardKey)) {
                return false;
            }

            var other = <PathGuardKey>o;
            return this.id === other.id;
        }
    }
}