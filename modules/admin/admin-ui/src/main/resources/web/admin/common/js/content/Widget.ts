module api.content {

    import ApplicationKey = api.application.ApplicationKey;

    export class Widget {

        private url: string;
        private displayName: string;
        private interfaces: string[];
        private widgetDescriptorKey: WidgetDescriptorKey;

        constructor(url: string, displayName: string, interfaces: string[], key: string) {
            this.url = url;
            this.displayName = displayName;
            this.interfaces = interfaces;
            this.widgetDescriptorKey = this.makeWidgetDescriptorKey(key);
        }

        private makeWidgetDescriptorKey(key: string): WidgetDescriptorKey {
            var applicationKey = key.split(":")[0],
                descriptorKeyName = key.split(":")[1];
            return new WidgetDescriptorKey(ApplicationKey.fromString(applicationKey), descriptorKeyName);
        }

        public getUrl(): string {
            return this.url;
        }

        public getDisplayName(): string {
            return this.displayName;
        }

        public getInterfaces(): string[] {
            return this.interfaces;
        }

        public getWidgetDescriptorKey(): api.content.WidgetDescriptorKey {
            return this.widgetDescriptorKey;
        }
    }

    export class WidgetDescriptorKey implements api.Equitable {

        private static SEPARATOR = ":";

        private applicationKey: ApplicationKey;

        private name: string;

        private refString: string;

        public static fromString(str: string): WidgetDescriptorKey {
            var sepIndex: number = str.indexOf(WidgetDescriptorKey.SEPARATOR);
            if (sepIndex == -1) {
                throw new Error("WidgetDescriptorKey must contain separator '" + WidgetDescriptorKey.SEPARATOR + "':" + str);
            }

            var applicationKey = str.substring(0, sepIndex);
            var name = str.substring(sepIndex + 1, str.length);

            return new WidgetDescriptorKey(ApplicationKey.fromString(applicationKey), name);
        }

        constructor(applicationKey: ApplicationKey, name: string) {
            this.applicationKey = applicationKey;
            this.name = name;
            this.refString = applicationKey.toString() + WidgetDescriptorKey.SEPARATOR + name.toString();
        }

        getApplicationKey(): ApplicationKey {
            return this.applicationKey;
        }

        getName(): string {
            return this.name;
        }

        toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, WidgetDescriptorKey)) {
                return false;
            }

            var other = <WidgetDescriptorKey>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }
    }
}