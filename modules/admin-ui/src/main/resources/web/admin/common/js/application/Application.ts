module api.application {

    export class Application extends api.item.BaseItem {

        static STATE_STARTED = 'started';
        static STATE_STOPPED = 'stopped';

        private applicationKey: ApplicationKey;

        private displayName: string;

        private vendorName: string;

        private vendorUrl: string;

        private url: string;

        private state: string;

        private version: string;

        private config: api.form.Form;

        private applicationDependencies: api.application.ApplicationKey[] = [];

        private contentTypeDependencies: api.schema.content.ContentTypeName[] = [];

        private metaSteps: api.schema.mixin.MixinNames;

        private minSystemVersion: string;

        private maxSystemVersion: string;

        constructor(builder: ApplicationBuilder) {
            super(builder);
            this.applicationKey = builder.applicationKey;
            this.displayName = builder.displayName;
            this.vendorName = builder.vendorName;
            this.vendorUrl = builder.vendorUrl;
            this.url = builder.url;
            this.state = builder.state;
            this.version = builder.version;
            this.config = builder.config;
            this.applicationDependencies = builder.applicationDependencies;
            this.contentTypeDependencies = builder.contentTypeDependencies;
            this.metaSteps = builder.metaSteps;
            this.minSystemVersion = builder.minSystemVersion;
            this.maxSystemVersion = builder.maxSystemVersion;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getApplicationKey(): ApplicationKey {
            return this.applicationKey;
        }

        getVersion(): string {
            return this.version;
        }

        getName(): string {
            return this.applicationKey.getName();
        }

        getVendorName(): string {
            return this.vendorName;
        }

        getVendorUrl(): string {
            return this.vendorUrl;
        }

        getUrl(): string {
            return this.url;
        }

        getState(): string {
            return this.state;
        }

        isStarted(): boolean {
            return this.state === Application.STATE_STARTED;
        }

        hasChildren(): boolean {
            return false;
        }

        getForm(): api.form.Form {
            return this.config;
        }

        getMinSystemVersion(): string {
            return this.minSystemVersion;
        }

        getMaxSystemVersion(): string {
            return this.maxSystemVersion;
        }

        getapplicationDependencies(): api.application.ApplicationKey[] {
            return this.applicationDependencies;
        }

        getContentTypeDependencies(): api.schema.content.ContentTypeName[] {
            return this.contentTypeDependencies;
        }

        getMetaSteps(): api.schema.mixin.MixinNames {
            return this.metaSteps;
        }

        static fromJson(json: api.application.json.ApplicationJson): Application {
            return new ApplicationBuilder().fromJson(json).build();
        }

        static fromJsonArray(jsonArray: api.application.json.ApplicationJson[]): Application[] {
            var array: Application[] = [];
            jsonArray.forEach((json: api.application.json.ApplicationJson) => {
                array.push(Application.fromJson(json));
            });
            return array;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Application) || !super.equals(o)) {
                return false;
            }
            var other = <Application>o;

            return this.applicationKey.equals(other.applicationKey) &&
                   this.displayName == other.displayName &&
                   this.vendorName == other.vendorName &&
                   this.vendorUrl == other.vendorUrl &&
                   this.url == other.url &&
                   this.state == other.state &&
                   this.version == other.version &&
                   api.ObjectHelper.arrayEquals(this.applicationDependencies, other.applicationDependencies) &&
                   api.ObjectHelper.arrayEquals(this.contentTypeDependencies, other.contentTypeDependencies) &&
                   api.ObjectHelper.equals(this.metaSteps, other.metaSteps) &&
                   this.minSystemVersion == other.minSystemVersion &&
                   this.maxSystemVersion == other.maxSystemVersion;
        }
    }

    export class ApplicationBuilder extends api.item.BaseItemBuilder {

        applicationKey: ApplicationKey;

        displayName: string;

        vendorName: string;

        vendorUrl: string;

        url: string;

        state: string;

        version: string;

        config: api.form.Form;

        applicationDependencies: api.application.ApplicationKey[];

        contentTypeDependencies: api.schema.content.ContentTypeName[];

        metaSteps: api.schema.mixin.MixinNames;

        minSystemVersion: string;

        maxSystemVersion: string;


        constructor(source?: Application) {
            this.applicationDependencies = [];
            this.contentTypeDependencies = [];
            this.metaSteps;
            if (source) {
                super(source);
                this.applicationKey = source.getApplicationKey();
                this.displayName = source.getDisplayName();
                this.vendorName = source.getVendorName();
                this.vendorUrl = source.getVendorUrl();
                this.url = source.getUrl();
                this.state = source.getState();
                this.version = source.getVersion();
                this.config = source.getForm();
                this.applicationDependencies = source.getapplicationDependencies();
                this.contentTypeDependencies = source.getContentTypeDependencies();
                this.metaSteps = source.getMetaSteps();
                this.minSystemVersion = source.getMinSystemVersion();
                this.maxSystemVersion = source.getMaxSystemVersion();
            }
        }

        fromJson(json: api.application.json.ApplicationJson): ApplicationBuilder {

            super.fromBaseItemJson(json, 'key');

            this.applicationKey = ApplicationKey.fromString(json.key);
            this.displayName = json.displayName;
            this.vendorName = json.vendorName;
            this.vendorUrl = json.vendorUrl;
            this.url = json.url;
            this.state = json.state;
            this.version = json.version;

            this.config = json.config != null ? api.form.Form.fromJson(json.config) : null;
            this.minSystemVersion = json.minSystemVersion;
            this.maxSystemVersion = json.maxSystemVersion;

            if (json.applicationDependencies != null) {
                json.applicationDependencies.forEach((dependency: string) => {
                    this.applicationDependencies.push(api.application.ApplicationKey.fromString(dependency));
                });
            }

            if (json.contentTypeDependencies != null) {
                json.contentTypeDependencies.forEach((dependency: string) => {
                    this.contentTypeDependencies.push(new api.schema.content.ContentTypeName(dependency));
                });
            }

            if (json.metaSteps != null) {
                this.metaSteps = api.schema.mixin.MixinNames.create().fromStrings(json.metaSteps).build();
            }

            return this;
        }

        build(): Application {
            return new Application(this);
        }
    }
}