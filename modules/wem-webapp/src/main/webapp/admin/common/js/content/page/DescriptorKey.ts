module api.content.page {

    export class DescriptorKey implements api.Equitable {

        private static SEPARATOR = ":";

        private moduleKey: api.module.ModuleKey;

        private name: DescriptorName;

        private refString: string;

        public static fromString(str: string): DescriptorKey {
            var sepIndex: number = str.indexOf(DescriptorKey.SEPARATOR);
            if (sepIndex == -1) {
                throw new Error("DescriptorKey must contain separator '" + DescriptorKey.SEPARATOR + "':" + str);
            }

            var moduleKey = str.substring(0, sepIndex);
            var name = str.substring(sepIndex + 1, str.length);

            return new DescriptorKey(api.module.ModuleKey.fromString(moduleKey), new DescriptorName(name));
        }

        constructor(moduleKey: api.module.ModuleKey, name: DescriptorName) {
            this.moduleKey = moduleKey;
            this.name = name;
            this.refString = moduleKey.toString() + DescriptorKey.SEPARATOR + name.toString();
        }

        getModuleKey(): api.module.ModuleKey {
            return this.moduleKey;
        }

        getName(): DescriptorName {
            return this.name;
        }

        toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, DescriptorKey)) {
                return false;
            }

            var other = <DescriptorKey>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }
    }
}