module api.content.page {

    export class PageComponent implements api.Equitable, api.Cloneable {

        private name: ComponentName;

        private parent: RegionPath;

        constructor(builder?: PageComponentBuilder<any>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.parent = builder.parent;
            }
        }

        setParent(path: RegionPath) {
            this.parent = path;
        }

        getPath(): ComponentPath {
            return ComponentPath.fromRegionPathAndComponentName(this.parent, this.name);
        }

        getName(): api.content.page.ComponentName {
            return this.name;
        }

        setName(name: api.content.page.ComponentName) {
            this.name = name;
        }

        reset() {

        }

        getParent(): RegionPath {
            return this.parent;
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

            if (!api.ObjectHelper.equals(this.parent, other.parent)) {
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

        parent: RegionPath;

        public setName(value: api.content.page.ComponentName): PageComponentBuilder<COMPONENT> {
            this.name = value;
            return this;
        }

        public setParent(value: api.content.page.RegionPath): PageComponentBuilder<COMPONENT> {
            this.parent = value;
            return this;
        }

        public build(): COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}