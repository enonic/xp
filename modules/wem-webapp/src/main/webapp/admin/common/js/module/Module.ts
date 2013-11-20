module api_module {

    export class Module extends api_item.BaseItem {

        private moduleKey:ModuleKey;

        private displayName:string;

        private vendorName:string;

        private vendorUrl:string;

        private url:string;

        static fromExtModel(model:Ext_data_Model):Module {
            return new api_module.Module(model.raw);
        }

        constructor(json:any){
            super(json);
            this.moduleKey = ModuleKey.fromString(json.key);
            this.displayName = json.displayName;
            this.vendorName = json.vendorName;
            this.vendorUrl = json.vendorUrl;
            this.url = json.url;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getModuleKey():string {
            return this.moduleKey.toString();
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