module api.application {
    import UploadItem = api.ui.uploader.UploadItem;

    export class Application extends api.item.BaseItem {

        static STATE_STARTED: string = 'started';

        static STATE_STOPPED: string = 'stopped';

        private applicationKey: ApplicationKey;

        private displayName: string;

        private description: string;

        private vendorName: string;

        private vendorUrl: string;

        private url: string;

        private state: string;

        private version: string;

        private local: boolean;

        private config: api.form.Form;

        private authConfig: api.form.Form;

        private applicationDependencies: api.application.ApplicationKey[] = [];

        private contentTypeDependencies: api.schema.content.ContentTypeName[] = [];

        private metaSteps: api.schema.mixin.MixinNames;

        private minSystemVersion: string;

        private maxSystemVersion: string;

        private iconUrl: string;

        constructor(builder: ApplicationBuilder) {
            super(builder);
            this.applicationKey = builder.applicationKey;
            this.displayName = builder.displayName;
            this.description = builder.description;
            this.vendorName = builder.vendorName;
            this.vendorUrl = builder.vendorUrl;
            this.url = builder.url;
            this.state = builder.state;
            this.version = builder.version;
            this.local = builder.local;
            this.config = builder.config;
            this.authConfig = builder.authConfig;
            this.applicationDependencies = builder.applicationDependencies;
            this.contentTypeDependencies = builder.contentTypeDependencies;
            this.metaSteps = builder.metaSteps;
            this.minSystemVersion = builder.minSystemVersion;
            this.maxSystemVersion = builder.maxSystemVersion;
            this.iconUrl = builder.iconUrl;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getDescription(): string {
            return this.description;
        }

        getApplicationKey(): ApplicationKey {
            return this.applicationKey;
        }

        getVersion(): string {
            return this.version;
        }

        isLocal(): boolean {
            return this.local;
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

        getAuthForm(): api.form.Form {
            return this.authConfig;
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

        getIconUrl(): string {
            return this.iconUrl;
        }

        static fromJson(json: api.application.json.ApplicationJson): Application {
            return new ApplicationBuilder().fromJson(json).build();
        }

        static fromJsonArray(jsonArray: api.application.json.ApplicationJson[]): Application[] {
            let array: Application[] = [];
            jsonArray.forEach((json: api.application.json.ApplicationJson) => {
                array.push(Application.fromJson(json));
            });
            return array;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Application) || !super.equals(o)) {
                return false;
            }
            let other = <Application>o;

            return this.applicationKey.equals(other.applicationKey) &&
                   this.displayName === other.displayName &&
                   this.description === other.description &&
                   this.vendorName === other.vendorName &&
                   this.vendorUrl === other.vendorUrl &&
                   this.url === other.url &&
                   this.state === other.state &&
                   this.version === other.version &&
                   this.local === other.local &&
                   api.ObjectHelper.arrayEquals(this.applicationDependencies, other.applicationDependencies) &&
                   api.ObjectHelper.arrayEquals(this.contentTypeDependencies, other.contentTypeDependencies) &&
                   api.ObjectHelper.equals(this.metaSteps, other.metaSteps) &&
                   this.minSystemVersion === other.minSystemVersion &&
                   this.maxSystemVersion === other.maxSystemVersion &&
                   this.iconUrl === other.iconUrl;
        }
    }

    export class ApplicationBuilder extends api.item.BaseItemBuilder {

        applicationKey: ApplicationKey;

        displayName: string;

        description: string;

        vendorName: string;

        vendorUrl: string;

        url: string;

        state: string;

        version: string;

        local: boolean;

        config: api.form.Form;

        authConfig: api.form.Form;

        applicationDependencies: api.application.ApplicationKey[];

        contentTypeDependencies: api.schema.content.ContentTypeName[];

        metaSteps: api.schema.mixin.MixinNames;

        minSystemVersion: string;

        maxSystemVersion: string;

        iconUrl: string;

        constructor(source?: Application) {
            if (source) {
                super(source);
                this.applicationKey = source.getApplicationKey();
                this.displayName = source.getDisplayName();
                this.description = source.getDescription();
                this.vendorName = source.getVendorName();
                this.vendorUrl = source.getVendorUrl();
                this.url = source.getUrl();
                this.state = source.getState();
                this.version = source.getVersion();
                this.local = source.isLocal();
                this.config = source.getForm();
                this.applicationDependencies = source.getapplicationDependencies();
                this.contentTypeDependencies = source.getContentTypeDependencies();
                this.metaSteps = source.getMetaSteps();
                this.minSystemVersion = source.getMinSystemVersion();
                this.maxSystemVersion = source.getMaxSystemVersion();
                this.iconUrl = source.getIconUrl();
            } else {
                this.applicationDependencies = [];
                this.contentTypeDependencies = [];
            }
        }

        fromJson(json: api.application.json.ApplicationJson): ApplicationBuilder {

            super.fromBaseItemJson(json, 'key');

            this.applicationKey = ApplicationKey.fromString(json.key);
            this.displayName = json.displayName;
            this.description = json.description;
            this.vendorName = json.vendorName;
            this.vendorUrl = json.vendorUrl;
            this.url = json.url;
            this.state = json.state;
            this.version = json.version;
            this.local = json.local;

            this.config = json.config != null ? api.form.Form.fromJson(json.config) : null;
            this.authConfig = json.authConfig != null ? api.form.Form.fromJson(json.authConfig) : null;
            this.minSystemVersion = json.minSystemVersion;
            this.maxSystemVersion = json.maxSystemVersion;
            this.iconUrl = json.iconUrl;

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

    export class ApplicationUploadMock {

        private id: string;
        private name: string;
        private uploadItem: UploadItem<Application>;

        constructor(uploadItem: UploadItem<Application>) {
            this.id = uploadItem.getId();
            this.name = uploadItem.getName();
            this.uploadItem = uploadItem;
        }

        getId(): string {
            return this.id;
        }

        getDisplayName(): string {
            return this.name;
        }

        getName(): string {
            return this.name;
        }

        getUploadItem(): UploadItem<Application> {
            return this.uploadItem;
        }

        getApplicationKey(): string {
            return this.name;
        }

        isLocal(): boolean {
            return false;
        }
    }
}
