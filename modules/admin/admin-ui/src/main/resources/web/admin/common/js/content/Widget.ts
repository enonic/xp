module api.content {

    import ApplicationKey = api.application.ApplicationKey;

    export class Widget {

        private url: string;
        private displayName: string;
        private interfaces: string[];
        private widgetDescriptorKey: WidgetDescriptorKey;
        private config: { [key: string]: string };

        constructor(url: string, displayName: string, interfaces: string[], key: string, config: { [key: string]: string }) {
            this.url = url;
            this.displayName = displayName;
            this.interfaces = interfaces;
            this.widgetDescriptorKey = this.makeWidgetDescriptorKey(key);
            this.config = config;
        }

        private makeWidgetDescriptorKey(key: string): WidgetDescriptorKey {
            let applicationKey = key.split(":")[0],
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

        public getConfig(): { [key: string]: string } {
            return this.config;
        }
    }

    export class WidgetDescriptorKey implements api.Equitable {

        private static SEPARATOR: string = ":";

        private applicationKey: ApplicationKey;

        private name: string;

        private refString: string;

        public static fromString(str: string): WidgetDescriptorKey {
            let sepIndex: number = str.indexOf(WidgetDescriptorKey.SEPARATOR);
            if (sepIndex == -1) {
                throw new Error("WidgetDescriptorKey must contain separator '" + WidgetDescriptorKey.SEPARATOR + "':" + str);
            }

            let applicationKey = str.substring(0, sepIndex);
            let name = str.substring(sepIndex + 1, str.length);

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

            let other = <WidgetDescriptorKey>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }
    }
}
