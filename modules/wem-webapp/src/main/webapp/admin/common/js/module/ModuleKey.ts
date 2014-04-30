module api.module {

    export class ModuleKey implements api.Equitable {

        private static SEPARATOR: string = "-";

        private name: string;

        private version: string;

        private refString: string;

        public static fromString(str: string): ModuleKey {
            var sepIndex: number = str.lastIndexOf(ModuleKey.SEPARATOR);
            if (sepIndex == -1) {
                throw new Error("ModuleKey must contain separator '" + ModuleKey.SEPARATOR + "':" + str);
            }

            var name = str.substring(0, sepIndex);
            var version = str.substring(sepIndex + 1, str.length);

            return new ModuleKey(name, version);
        }

        constructor(moduleName: string, moduleVersion: string) {
            this.name = moduleName;
            this.version = moduleVersion;
            this.refString = this.name + ModuleKey.SEPARATOR + this.version;
        }

        getName(): string {
            return this.name;
        }

        getVersion(): string {
            return this.version;
        }

        toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ModuleKey)) {
                return false;
            }

            var other = <ModuleKey>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }

        static toStringArray(keys: ModuleKey[]): string[] {
            var stringArray: string[] = [];
            keys.forEach((key: ModuleKey) => {
                stringArray.push(key.toString());
            });
            return stringArray;
        }

    }
}