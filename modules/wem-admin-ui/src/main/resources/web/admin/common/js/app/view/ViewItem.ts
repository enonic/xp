module api.app.view {

    export class ViewItem<M extends api.Equitable> implements api.Equitable {

        private model: M;

        private displayName: string;

        private path: string;

        private iconUrl;

        constructor(model: M) {
            this.model = model;
        }

        setDisplayName(value: string): ViewItem<M> {
            this.displayName = value;
            return this;
        }

        setPath(value: string): ViewItem<M> {
            this.path = value;
            return this;
        }

        setIconUrl(value: string): ViewItem<M> {
            this.iconUrl = value;
            return this;
        }

        getModel(): M {
            return this.model;
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

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ViewItem)) {
                return false;
            }
            var other = <ViewItem<M>> o;
            return this.model.equals(other.model) &&
                   this.displayName == other.displayName &&
                   this.path == other.path &&
                   this.iconUrl == other.iconUrl;
        }
    }

}
