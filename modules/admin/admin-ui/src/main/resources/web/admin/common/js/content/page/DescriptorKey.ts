module api.content.page {

    export class DescriptorKey implements api.Equitable {

        private static SEPARATOR: string = ':';

        private applicationKey: api.application.ApplicationKey;

        private name: DescriptorName;

        private refString: string;

        public static fromString(str: string): DescriptorKey {
            let sepIndex: number = str.indexOf(DescriptorKey.SEPARATOR);
            if (sepIndex == -1) {
                throw new Error("DescriptorKey must contain separator '" + DescriptorKey.SEPARATOR + "':" + str);
            }

            let applicationKey = str.substring(0, sepIndex);
            let name = str.substring(sepIndex + 1, str.length);

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

            let other = <DescriptorKey>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }
    }
}
