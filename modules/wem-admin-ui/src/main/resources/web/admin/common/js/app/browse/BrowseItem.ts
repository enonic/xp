module api.app.browse {

    export class BrowseItem<M extends api.Equitable> implements api.Equitable {

        private model: M;

        private id: string;

        private displayName: string;

        private path: string;

        private iconUrl;

        private iconClass: string;

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

        setIconClass(iconClass: string): api.app.browse.BrowseItem<M> {
            this.iconClass = iconClass;
            return this;
        }

        getIconClass(): string {
            return this.iconClass;
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
                .setIconClass(this.iconClass)
                .setDisplayName(this.displayName)
                .setPath(this.path);
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, BrowseItem)) {
                return false;
            }
            var other = <BrowseItem<M>> o;
            return this.model.equals(other.model) &&
                   this.displayName == other.displayName &&
                   this.path == other.path &&
                   this.iconUrl == other.iconUrl && this.iconClass == other.iconClass;
        }
    }

}
