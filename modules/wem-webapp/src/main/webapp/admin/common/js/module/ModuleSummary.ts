module api_module {

    export class ModuleSummary extends api_item.BaseItem {

        private moduleKey:ModuleKey;

        private displayName:string;

        private vendorName:string;

        private vendorUrl:string;

        private url:string;

        static fromExtModel(model:Ext_data_Model):ModuleSummary {
            return new api_module.ModuleSummary(<api_module_json.ModuleSummaryJson>model.raw);
        }

        static fromJsonArray(jsonArray:api_module_json.ModuleSummaryJson[]):ModuleSummary[] {
            var array:ModuleSummary[] = [];
            jsonArray.forEach((json:api_module_json.ModuleSummaryJson) => {
                array.push(new ModuleSummary(json));
            });
            return array;
        }

        constructor(json:api_module_json.ModuleSummaryJson){
            super(json, 'key');
            this.moduleKey = ModuleKey.fromString(json.key);

            this.displayName = json.displayName;
            this.vendorName = json.vendorName;
            this.vendorUrl = json.vendorUrl;
            this.url = json.url;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getModuleKey():ModuleKey {
            return this.moduleKey;
        }

        getVersion():string {
            return this.moduleKey.getVersion();
        }

        getName():string
        {
            return this.moduleKey.getName();
        }

        getVendorName():string {
            return this.vendorName;
        }

        getVendorUrl():string {
            return this.vendorUrl;
        }

        getUrl():string {
            return this.url;
        }


    }
}