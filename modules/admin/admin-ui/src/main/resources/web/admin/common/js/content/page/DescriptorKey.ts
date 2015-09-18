module api.content.page {

    export class DescriptorKey implements api.Equitable {

        private static SEPARATOR = ":";

        private applicationKey: api.application.ApplicationKey;

        private name: DescriptorName;

        private refString: string;

        public static fromString(str: string): DescriptorKey {
            var sepIndex: number = str.indexOf(DescriptorKey.SEPARATOR);
            if (sepIndex == -1) {
                throw new Error("DescriptorKey must contain separator '" + DescriptorKey.SEPARATOR + "':" + str);
            }

            var applicationKey = str.substring(0, sepIndex);
            var name = str.substring(sepIndex + 1, str.length);

            return new DescriptorKey(api.application.ApplicationKey.fromString(applicationKey), new DescriptorName(name));
        }

        constructor(applicationKey: api.application.ApplicationKey, name: DescriptorName) {
            this.applicationKey = applicationKey;
            this.name = name;
            this.refString = applicationKey.toString() + DescriptorKey.SEPARATOR + name.toString();
        }

        getApplicationKey(): api.application.ApplicationKey {
            return this.applicationKey;
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