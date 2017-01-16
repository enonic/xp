module api.macro {

    export class MacroKey implements api.Equitable {

        private static SEPARATOR: string = ':';

        private applicationKey: api.application.ApplicationKey;

        private name: string;

        private refString: string;

        constructor(applicationKey: api.application.ApplicationKey, name: string) {
            this.applicationKey = applicationKey;
            this.name = name;
            this.refString = applicationKey.toString() + MacroKey.SEPARATOR + name.toString();
        }

        public static fromString(str: string): MacroKey {
            let sepIndex: number = str.indexOf(this.SEPARATOR);
            if (sepIndex == -1) {
                throw new Error("MacroKey must contain separator '" + this.SEPARATOR + "':" + str);
            }

            let applicationKey = str.substring(0, sepIndex);
            let name = str.substring(sepIndex + 1, str.length);

            return new MacroKey(api.application.ApplicationKey.fromString(applicationKey), name);
        }

        public getApplicationKey(): api.application.ApplicationKey {
            return this.applicationKey;
        }

        public getName(): string {
            return this.name;
        }

        public getRefString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, MacroKey)) {
                return false;
            }

            let other = <MacroKey>o;

            if (this.name != other.name) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.applicationKey, other.applicationKey)) {
                return false;
            }

            return true;
        }

    }
}
