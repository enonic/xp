module api {

    export class Name implements api.Equitable {

        public static FORBIDDEN_CHARS: RegExp = /[^a-z0-9\-]+/ig;

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

        public static ensureValidName(possibleInvalidName: string): string {
            if (!possibleInvalidName) {
                return "";
            }

            var generated = possibleInvalidName.replace(/[\s+\.\/]/ig, '-').replace(/-{2,}/g, '-').replace(/^-|-$/g, '').toLowerCase();
            return (generated || '').replace(Name.FORBIDDEN_CHARS, '');

        }
    }
}