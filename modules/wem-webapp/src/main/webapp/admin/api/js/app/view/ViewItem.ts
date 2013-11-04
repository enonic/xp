module api_app_view {

    export class ViewItem<M> {

        private model:M;

        private displayName:string;

        private path:string;

        private iconUrl;

        constructor(model:M) {
            this.model = model;
        }

        setDisplayName(value:string):ViewItem<M> {
            this.displayName = value;
            return this;
        }

        setPath(value:string):ViewItem<M> {
            this.path = value;
            return this;
        }

        setIconUrl(value:string):ViewItem<M> {
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
    }

}
