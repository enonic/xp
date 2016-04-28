module api.application {

    import ApplicationKey = api.application.ApplicationKey;

    export class MarketApplication {

        private appKey: ApplicationKey;
        private displayName: string;
        private name: string;
        private description: string;
        private iconUrl: string;
        private url: string;
        private latestVersion: string;
        private versions: Object;
        private status: MarketAppStatus = MarketAppStatus.NOT_INSTALLED;

        constructor(builder: MarketApplicationBuilder) {
            this.displayName = builder.displayName;
            this.name = builder.name;
            this.description = builder.description;
            this.iconUrl = builder.iconUrl;
            this.url = builder.url;
            this.latestVersion = builder.latestVersion;
            this.versions = builder.versions;
            this.appKey = builder.appKey;
        }

        static fromJson(appKey: string, json: api.application.json.MarketApplicationJson): MarketApplication {
            return new MarketApplicationBuilder().fromJson(appKey, json).build();
        }

        static fromJsonArray(appsObj: Object): MarketApplication[] {
            var array: MarketApplication[] = [];
            for (var property in appsObj) {
                array.push(MarketApplication.fromJson(property, <api.application.json.MarketApplicationJson>appsObj[property]));
            }
            return array;
        }

        public getDisplayName(): string {
            return this.displayName;
        }

        public getName(): string {
            return this.name;
        }

        public getDescription(): string {
            return this.description;
        }

        public getIconUrl(): string {
            return this.iconUrl;
        }

        public getUrl(): string {
            return this.url;
        }

        public getLatestVersion(): string {
            return this.latestVersion;
        }

        public getLatestVersionDownloadUrl(): string {
            return this.getVersions()[this.getLatestVersion()]["applicationUrl"];
        }

        public getVersions(): Object {
            return this.versions;
        }

        public setStatus(status: MarketAppStatus) {
            this.status = status;
        }

        public getStatus(): MarketAppStatus {
            return this.status;
        }

        public getAppKey(): ApplicationKey {
            return this.appKey;
        }
    }

    export enum MarketAppStatus {
        NOT_INSTALLED,
        INSTALLED,
        OLDER_VERSION_INSTALLED,
        UNKNOWN
    }

    export class MarketAppStatusFormatter {

        public static statusInstallCssClass = "install";
        public static statusInstalledCssClass = "installed";
        public static statusUpdateCssClass = "update";

        public static formatStatus(appStatus: MarketAppStatus): string {

            var status;

            switch (appStatus) {
            case MarketAppStatus.NOT_INSTALLED:
                status = "Install";
                break;
            case MarketAppStatus.INSTALLED:
                status = "Installed";
                break;
            case MarketAppStatus.OLDER_VERSION_INSTALLED:
                status = "Update";
                break;
            case MarketAppStatus.UNKNOWN:
                status = "Unknown";
                break;
            default:
                status = "Unknown"
            }

            if (!!MarketAppStatus[status]) {
                return "Unknown";
            }

            return status;
        }

        public static getStatusCssClass(appStatus: MarketAppStatus): string {

            var cssClass;

            switch (appStatus) {
            case MarketAppStatus.NOT_INSTALLED:
                cssClass = MarketAppStatusFormatter.statusInstallCssClass;
                break;
            case MarketAppStatus.INSTALLED:
                cssClass = MarketAppStatusFormatter.statusInstalledCssClass;
                break;
            case MarketAppStatus.OLDER_VERSION_INSTALLED:
                cssClass = MarketAppStatusFormatter.statusUpdateCssClass;
                break;
            case MarketAppStatus.UNKNOWN:
                cssClass = "unknown";
                break;
            default:
                cssClass = "Unknown"
            }

            if (!!MarketAppStatus[status]) {
                return "unknown";
            }

            return cssClass;
        }

        public static formatPerformedAction(appStatus: MarketAppStatus): string {

            var performedOperation;

            switch (appStatus) {
            case MarketAppStatus.NOT_INSTALLED:
                performedOperation = "installed";
                break;
            case MarketAppStatus.OLDER_VERSION_INSTALLED:
                performedOperation = "updated";
                break;
            default:
                performedOperation = "installed"
            }

            return performedOperation;
        }
    }

    export class MarketApplicationBuilder {

        displayName: string;
        name: string;
        description: string;
        iconUrl: string;
        url: string;
        latestVersion: string;
        versions: Object;
        status: string;
        appKey: ApplicationKey;

        constructor() {
        }

        public fromJson(appKey: string, json: api.application.json.MarketApplicationJson): MarketApplicationBuilder {
            this.appKey = ApplicationKey.fromString(appKey);
            this.displayName = json.displayName;
            this.description = json.description;
            this.iconUrl = json.iconUrl;
            this.url = json.url;
            this.latestVersion = json.latestVersion;
            this.versions = json.versions;
            this.name = json.name;
            return this;
        }

        setLatestVersion(latestVersion: string): MarketApplicationBuilder {
            this.latestVersion = latestVersion;
            return this;
        }

        public build(): MarketApplication {
            return new MarketApplication(this);
        }
    }
}