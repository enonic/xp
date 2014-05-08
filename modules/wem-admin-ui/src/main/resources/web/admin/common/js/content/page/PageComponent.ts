module api.content.page {

    export class PageComponent implements api.Equitable, api.Cloneable {

        private name: ComponentName;

        private region: RegionPath;

        private path: ComponentPath;

        constructor(builder?: PageComponentBuilder<any>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.region = builder.region;
                if (this.region && this.name) {
                    this.path = ComponentPath.fromRegionPathAndComponentName(this.region, this.name);
                }
            }
        }

        setPath(path: ComponentPath) {
            this.path = path;
            if (path != null) {
                this.region = path.getRegionPath();
            }
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getName(): api.content.page.ComponentName {
            return this.name;
        }

        setName(name: api.content.page.ComponentName) {
            this.name = name;
        }

        reset() {

        }

        getRegion(): RegionPath {
            return this.region;
        }

        toJson(): api.content.page.PageComponentTypeWrapperJson {
            throw new Error("Must be implemented by inheritor: " + api.util.getClassName(this));
        }

        toPageComponentJson(): PageComponentJson {

            return {
                "name": this.name.toString()
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PageComponent)) {
                return false;
            }

            var other = <PageComponent>o;

            if (!api.ObjectHelper.equals(this.name, other.name)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.region, other.region)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.path, other.path)) {
                return false;
            }

            return true;
        }

        clone(): PageComponent {
            throw new Error("Must be implemented by inheritors");
        }
    }

    export class PageComponentBuilder<COMPONENT extends PageComponent> {

        name: api.content.page.ComponentName;

        region: RegionPath;

        public setName(value: api.content.page.ComponentName): PageComponentBuilder<COMPONENT> {
            this.name = value;
            return this;
        }

        public setRegion(value: api.content.page.RegionPath): PageComponentBuilder<COMPONENT> {
            this.region = value;
            return this;
        }

        public build(): COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}