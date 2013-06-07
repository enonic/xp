module api_delete{

    export class DeleteItem {

        private iconUrl:string;

        private displayName:string;

        constructor(iconUrl:string, displayName:string) {
            this.iconUrl = iconUrl;
            this.displayName = displayName;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getIconUrl():string {
            return this.iconUrl;
        }
    }
}
