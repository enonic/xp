module api {

    export class Name implements api.Equitable {

        public static FORBIDDEN_CHARS: RegExp = /[^a-z0-9\-]+/ig;

        public static SIMPLIFIED_FORBIDDEN_CHARS: RegExp = /[\/!?\\]/g;

        private value: string;

        constructor(name: string) {

            api.util.assertNotNull(name, "Name cannot be null");

            api.util.assert(!api.util.StringHelper.isEmpty(name), "Name cannot be empty");

            this.value = name;
        }

        getValue(): string {
            return this.value;
        }

        toString(): string {
            return this.value;
        }

        equals(o: api.Equitable): boolean {

            if (!(api.ObjectHelper.iFrameSafeInstanceOf(o, Name))) {
                return false;
            }

            var other = <Name>o;

            if (this.value != other.value) {
                return false;
            }

            return true;
        }
    }
}