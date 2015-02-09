module api.module {

    export class ModuleKey implements api.Equitable {

        static BASE: ModuleKey = ModuleKey.fromString('base');
        static PORTAL: ModuleKey = ModuleKey.fromString('portal');
        static MEDIA: ModuleKey = ModuleKey.fromString('media');

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
            return keys.map((key: ModuleKey) => key.toString());
        }

        static fromModules(modules: Module[]): ModuleKey[] {
            return modules.map<ModuleKey>((mod: Module) => mod.getModuleKey());
        }

    }
}