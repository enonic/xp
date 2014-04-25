module api.module {

    export class ModuleSummary extends api.item.BaseItem {

        private moduleKey: ModuleKey;

        private displayName: string;

        private vendorName: string;

        private vendorUrl: string;

        private url: string;

        constructor(builder: ModuleSummaryBuilder) {
            super(builder);
            this.moduleKey = builder.moduleKey;
            this.displayName = builder.displayName;
            this.vendorName = builder.vendorName;
            this.vendorUrl = builder.vendorUrl;
            this.url = builder.url;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getModuleKey(): ModuleKey {
            return this.moduleKey;
        }

        getVersion(): string {
            return this.moduleKey.getVersion();
        }

        getName(): string {
            return this.moduleKey.getName();
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

        static fromExtModel(model: Ext_data_Model): ModuleSummary {
            return ModuleSummary.fromJson(<api.module.json.ModuleSummaryJson>model.raw);
        }

        static fromJson(json: api.module.json.ModuleSummaryJson): ModuleSummary {
            return new ModuleSummaryBuilder().fromJson(json).build();
        }

        static fromJsonArray(jsonArray: api.module.json.ModuleSummaryJson[]): ModuleSummary[] {
            var array: ModuleSummary[] = [];
            jsonArray.forEach((json: api.module.json.ModuleSummaryJson) => {
                array.push(ModuleSummary.fromJson(json));
            });
            return array;
        }
    }

    export class ModuleSummaryBuilder extends api.item.BaseItemBuilder {

        moduleKey: ModuleKey;

        displayName: string;

        vendorName: string;

        vendorUrl: string;

        url: string;

        fromJson(json: api.module.json.ModuleSummaryJson): ModuleSummaryBuilder {

            super.fromBaseItemJson(json, 'key');

            this.moduleKey = ModuleKey.fromString(json.key);
            this.displayName = json.displayName;
            this.vendorName = json.vendorName;
            this.vendorUrl = json.vendorUrl;
            this.url = json.url;
            return this;
        }

        build(): ModuleSummary {
            return new ModuleSummary(this);
        }
    }
}