module api.content.page {

    import Region = api.content.page.region.Region;

    export class PageComponent implements api.Equitable, api.Cloneable {

        private name: ComponentName;

        private parent: Region;

        constructor(builder?: PageComponentBuilder<any>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.parent = builder.parent;
            }
        }

        setParent(parent: Region) {
            this.parent = parent;
        }

        getPath(): ComponentPath {
            return ComponentPath.fromRegionPathAndComponentIndex(this.parent.getPath(), this.parent.getComponentIndex(this));
        }

        getName(): ComponentName {
            return this.name;
        }

        setName(name: ComponentName) {
            this.name = name;
        }

        reset() {

        }

        getParent(): Region {
            return this.parent;
        }

        duplicateComponent(): PageComponent {

            var region = this.getParent();
            return region.duplicateComponent(this);
        }

        moveToRegion(otherRegion: Region, precedingComponent: PageComponent) {

            this.removeFromParent();
            otherRegion.addComponentAfter(this, precedingComponent);
        }

        removeFromParent() {
            this.parent.removeComponent(this);
        }

        ensureUniqueComponentName(wantedName: ComponentName): ComponentName {
            return this.parent.ensureUniqueComponentName(wantedName);
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

            return true;
        }

        clone(): PageComponent {
            throw new Error("Must be implemented by inheritors");
        }
    }

    export class PageComponentBuilder<COMPONENT extends PageComponent> {

        name: ComponentName;

        parent: Region;

        constructor(source?: PageComponent) {
            if (source) {
                this.name = source.getName();
                this.parent = source.getParent();
            }
        }

        public setName(value: ComponentName): PageComponentBuilder<COMPONENT> {
            this.name = value;
            return this;
        }

        public setParent(value: Region): PageComponentBuilder<COMPONENT> {
            this.parent = value;
            return this;
        }

        public build(): COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}