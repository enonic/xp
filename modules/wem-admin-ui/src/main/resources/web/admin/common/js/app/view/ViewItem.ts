module api.app.view {

    export class ViewItem<M extends api.Equitable> implements api.Equitable {

        private model: M;

        private displayName: string;

        private iconClass: string;

        private path: string;

        private pathName: string;

        private iconUrl: string;

        private iconSize: number;

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

        setPathName(value: string): ViewItem<M> {
            this.pathName = value;
            return this;
        }

        setIconUrl(value: string): ViewItem<M> {
            this.iconUrl = value;
            return this;
        }

        setIconClass(iconClass: string): ViewItem<M> {
            this.iconClass = iconClass;
            return this;
        }

        setIconSize(value: number): ViewItem<M> {
            this.iconSize = value;
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

        getPathName(): string {
            return this.pathName;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        getIconClass(): string {
            return this.iconClass;
        }

        getIconSize(): number {
            return this.iconSize;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ViewItem)) {
                return false;
            }
            var other = <ViewItem<M>> o;
            return this.model.equals(other.getModel()) &&
                   this.displayName === other.getDisplayName() &&
                   this.path === other.getPath() &&
                   this.pathName === other.getPathName() &&
                   this.iconUrl === other.getIconUrl() &&
                   this.iconClass === other.getIconClass() &&
                   this.iconSize === other.getIconSize();

        }
    }

}
