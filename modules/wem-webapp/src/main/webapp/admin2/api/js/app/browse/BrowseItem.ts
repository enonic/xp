module api_app_browse {

    export class BrowseItem {

        private model:any;

        private displayName:string;

        private path:string;

        private iconUrl;

        constructor(model:any) {
            this.model = model;
        }

        setDisplayName(value:string):api_app_browse.BrowseItem {
            this.displayName = value;
            return this;
        }

        setPath(value:string):api_app_browse.BrowseItem {
            this.path = value;
            return this;
        }

        setIconUrl(value:string):api_app_browse.BrowseItem {
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
