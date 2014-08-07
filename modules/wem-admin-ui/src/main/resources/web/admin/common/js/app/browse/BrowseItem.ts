module api.app.browse {

    export class BrowseItem<M> {

        private model: M;

        private id: string;

        private displayName: string;

        private path: string;

        private iconUrl;

        constructor(model: M) {
            this.model = model;
        }

        setId(value: string): api.app.browse.BrowseItem<M> {
            this.id = value;
            return this;
        }

        setDisplayName(value: string): api.app.browse.BrowseItem<M> {
            this.displayName = value;
            return this;
        }

        setPath(value: string): api.app.browse.BrowseItem<M> {
            this.path = value;
            return this;
        }

        setIconUrl(value: string): api.app.browse.BrowseItem<M> {
            this.iconUrl = value;
            return this;
        }

        getModel(): M {
            return this.model;
        }

        getId(): string {
            return this.id;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getPath(): string {
            return this.path;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        toViewItem(): api.app.view.ViewItem<M> {
            return new api.app.view.ViewItem<M>(this.model)
                .setIconUrl(this.iconUrl)
                .setDisplayName(this.displayName)
                .setPath(this.path);
        }
    }

}
