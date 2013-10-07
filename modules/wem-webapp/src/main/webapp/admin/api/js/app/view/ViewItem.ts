module api_app_view {

    export class ViewItem {

        private model:any;

        private displayName:string;

        private path:string;

        private iconUrl;

        constructor(model:any) {
            this.model = model;
        }

        setDisplayName(value:string):ViewItem {
            this.displayName = value;
            return this;
        }

        setPath(value:string):ViewItem {
            this.path = value;
            return this;
        }

        setIconUrl(value:string):ViewItem {
            this.iconUrl = value;
            return this;
        }

        getModel():any {
            return this.model;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getPath():string {
            return this.path;
        }

        getIconUrl():string {
            return this.iconUrl;
        }
    }

}
