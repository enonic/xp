module api_schema{

    export class Schema extends api_item.BaseItem {

        private name:string;

        private qualifiedName:string;

        private displayName:string;

        private icon:string;

        constructor(json:api_schema.SchemaJson) {
            super(json);
            this.name = json.name;
            this.qualifiedName = json.qualifiedName;
            this.displayName = json.displayName;
            this.icon = json.iconUrl;
        }

        getName():string {
            return this.name;
        }

        getSchemaName():string {
            return this.qualifiedName;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getIcon():string {
            return this.icon;
        }

    }
}