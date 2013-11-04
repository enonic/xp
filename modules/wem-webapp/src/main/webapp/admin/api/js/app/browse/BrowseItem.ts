module api_app_browse {

    export class BrowseItem<M> {

        private model:M;

        private displayName:string;

        private path:string;

        private iconUrl;

        constructor(model:M) {
            this.model = model;
        }

        setDisplayName(value:string):api_app_browse.BrowseItem<M> {
            this.displayName = value;
            return this;
        }

        setPath(value:string):api_app_browse.BrowseItem<M> {
            this.path = value;
            return this;
        }

        setIconUrl(value:string):api_app_browse.BrowseItem<M> {
            this.iconUrl = value;
            return this;
        }

        getModel():M {
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

        toViewItem():api_app_view.ViewItem<M> {
            return new api_app_view.ViewItem<M>(this.model)
                .setIconUrl(this.iconUrl)
                .setDisplayName(this.displayName)
                .setPath(this.path);
        }
    }

}
