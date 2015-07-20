module api.module {

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

        private moduleDependencies: api.module.ApplicationKey[] = [];

        private contentTypeDependencies: api.schema.content.ContentTypeName[] = [];

        private metaSteps: api.schema.mixin.MixinNames;

        private minSystemVersion: string;

        private maxSystemVersion: string;

        constructor(builder: ModuleBuilder) {
            super(builder);
            this.applicationKey = builder.applicationKey;
            this.displayName = builder.displayName;
            this.vendorName = builder.vendorName;
            this.vendorUrl = builder.vendorUrl;
            this.url = builder.url;
            this.state = builder.state;
            this.version = builder.version;
            this.config = builder.config;
            this.moduleDependencies = builder.moduleDependencies;
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

        getModuleDependencies(): api.module.ApplicationKey[] {
            return this.moduleDependencies;
        }

        getContentTypeDependencies(): api.schema.content.ContentTypeName[] {
            return this.contentTypeDependencies;
        }

        getMetaSteps(): api.schema.mixin.MixinNames {
            return this.metaSteps;
        }

        static fromJson(json: api.module.json.ModuleJson): Application {
            return new ModuleBuilder().fromJson(json).build();
        }

        static fromJsonArray(jsonArray: api.module.json.ModuleJson[]): Application[] {
            var array: Application[] = [];
            jsonArray.forEach((json: api.module.json.ModuleJson) => {
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
                   api.ObjectHelper.arrayEquals(this.moduleDependencies, other.moduleDependencies) &&
                   api.ObjectHelper.arrayEquals(this.contentTypeDependencies, other.contentTypeDependencies) &&
                   api.ObjectHelper.equals(this.metaSteps, other.metaSteps) &&
                   this.minSystemVersion == other.minSystemVersion &&
                   this.maxSystemVersion == other.maxSystemVersion;
        }
    }

    export class ModuleBuilder extends api.item.BaseItemBuilder {

        applicationKey: ApplicationKey;

        displayName: string;

        vendorName: string;

        vendorUrl: string;

        url: string;

        state: string;

        version: string;

        config: api.form.Form;

        moduleDependencies: api.module.ApplicationKey[];

        contentTypeDependencies: api.schema.content.ContentTypeName[];

        metaSteps: api.schema.mixin.MixinNames;

        minSystemVersion: string;

        maxSystemVersion: string;


        constructor(source?: Application) {
            this.moduleDependencies = [];
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
                this.moduleDependencies = source.getModuleDependencies();
                this.contentTypeDependencies = source.getContentTypeDependencies();
                this.metaSteps = source.getMetaSteps();
                this.minSystemVersion = source.getMinSystemVersion();
                this.maxSystemVersion = source.getMaxSystemVersion();
            }
        }

        fromJson(json: api.module.json.ModuleJson): ModuleBuilder {

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

            if (json.moduleDependencies != null) {
                json.moduleDependencies.forEach((dependency: string) => {
                    this.moduleDependencies.push(api.module.ApplicationKey.fromString(dependency));
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