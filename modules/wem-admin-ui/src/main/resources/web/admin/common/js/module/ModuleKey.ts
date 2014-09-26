module api.module {

    export class ModuleKey implements api.Equitable {

        private name: string;

        public static fromString(moduleName: string): ModuleKey {
            return new ModuleKey(moduleName);
        }

        constructor(moduleName: string) {
            this.name = moduleName;
        }

        getName(): string {
            return this.name;
        }

        toString(): string {
            return this.name;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ModuleKey)) {
                return false;
            }

            var other = <ModuleKey>o;
            return api.ObjectHelper.stringEquals(this.name, other.name);
        }

        static toStringArray(keys: ModuleKey[]): string[] {
            var stringArray: string[] = [];
            keys.forEach((key: ModuleKey) => {
                stringArray.push(key.toString());
            });
            return stringArray;
        }

        static fromModules(modules: ModuleSummary[]): ModuleKey[] {
            return modules.map<ModuleKey>((mod: ModuleSummary) => {
                return mod.getModuleKey();
            });
        }

    }
}