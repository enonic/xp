module api.content.page.region {

    export class Region implements api.Equitable {

        private name: string;

        private pageComponents: api.content.page.PageComponent[] = [];

        private componentByName: {[s:string] : api.content.page.PageComponent;} = {};

        private path: api.content.page.RegionPath;

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.path = builder.path;
            this.pageComponents = builder.pageComponents;

            this.pageComponents.forEach((c: api.content.page.PageComponent)=> {
                if (c instanceof api.content.page.image.ImageComponent) {
                    var imageComponent = <api.content.page.image.ImageComponent>c;
                    this.componentByName[imageComponent.getName().toString()] = imageComponent;
                }
                else if (c instanceof api.content.page.part.PartComponent) {
                    var partComponent = <api.content.page.part.PartComponent>c;
                    this.componentByName[partComponent.getName().toString()] = partComponent;
                }
                else if (c instanceof api.content.page.layout.LayoutComponent) {
                    var layoutComponent = <api.content.page.layout.LayoutComponent>c;
                    this.componentByName[layoutComponent.getName().toString()] = layoutComponent;
                }
                else {
                    throw new Error("Unsupported component for Region: " + api.util.getClassName(c));
                }
            });
        }

        getName(): string {
            return this.name;
        }

        setPath(value: api.content.page.RegionPath) {
            this.path = value;
        }

        getPath(): api.content.page.RegionPath {
            return this.path;
        }

        ensureUniqueComponentName(wantedName: ComponentName): ComponentName {

            var numberOfDuplicates = this.countNumberOfDuplicates(wantedName);
            if (numberOfDuplicates == 0) {
                return wantedName;
            }

            var duplicateCounter = numberOfDuplicates + 1;
            var possibleNewName = wantedName.createDuplicate(duplicateCounter);
            while (this.nameAlreadyInUse(possibleNewName)) {
                possibleNewName = wantedName.createDuplicate(++duplicateCounter);
            }

            return possibleNewName;
        }

        private countNumberOfDuplicates(name: api.content.page.ComponentName): number {

            var count = 0;
            this.pageComponents.forEach((component: api.content.page.PageComponent)=> {
                if (component.getName().isDuplicateOf(name)) {
                    count++;
                }
            });
            return count;
        }

        private nameAlreadyInUse(name: api.content.page.ComponentName) {

            var exisiting = this.componentByName[name.toString()];
            return !exisiting ? false : true;
        }

        /*
         *  Add component after target component. Component will only be added if target component is found.
         *  Returns the index of the added component, -1 if target component was not found.
         */
        addComponentAfter(component: api.content.page.PageComponent, precedingComponent: ComponentName): number {

            api.util.assert(!this.hasComponentWithName(component.getName()),
                    "Component already added to region [" + this.name + "]: " + component.getName().toString());


            var precedingIndex = -1;
            if (precedingComponent != null) {
                precedingIndex = this.getComponentIndex(precedingComponent);
                if (precedingIndex == -1 && this.pageComponents.length > 1) {
                    return -1;
                }
            }

            var componentPath = ComponentPath.fromRegionPathAndComponentName(this.path, component.getName());
            component.setPath(componentPath);
            this.componentByName[component.getName().toString()] = component;

            if (precedingIndex == -1) {
                this.pageComponents.splice(0, 0, component);
                return 0;
            }
            else {
                var index = precedingIndex + 1;
                this.pageComponents.splice(index, 0, component);
                return index;
            }
        }

        removeComponent(component: api.content.page.PageComponent): api.content.page.PageComponent {
            if (!component) {
                return null;
            }
//            api.util.assert(this.hasComponentWithName(component.getName()),
//                "Component doesn't exists in region [" + this.name + "]: " + component.getName().toString());

            this.pageComponents.splice(this.getComponentIndex(component.getName()), 1);
            delete this.componentByName[component.getName().toString()];

            return component;
        }

        getComponentIndex(componentName: ComponentName): number {

            for (var i = 0; i < this.pageComponents.length; i++) {
                var currComponent = this.pageComponents[i];
                if (currComponent.getName().equals(componentName)) {
                    return i;
                }
            }
            return -1;
        }

        hasComponentWithName(name: ComponentName) {
            return this.componentByName[name.toString()] != undefined;
        }

        getComponents(): api.content.page.PageComponent[] {
            return this.pageComponents;
        }

        getComponentByIndex(index: number): api.content.page.PageComponent {
            return this.pageComponents[index];
        }

        getComponentByName(name: api.content.page.ComponentName): api.content.page.PageComponent {
            return this.componentByName[name.toString()];
        }

        getImageComponent(name: api.content.page.ComponentName): api.content.page.image.ImageComponent {
            var c = this.getComponentByName(name);

            var message = "Expected component [" + name.toString() + "] to be an api.content.page.image.ImageComponent: " +
                          api.util.getClassName(c);
            api.util.assert(c instanceof api.content.page.image.ImageComponent, message);
            return <api.content.page.image.ImageComponent>c;
        }

        getLayoutComponent(name: api.content.page.ComponentName): api.content.page.layout.LayoutComponent {
            var c = this.getComponentByName(name);

            var message = "Expected component [" + name.toString() + "] to be a api.content.page.layout.LayoutComponent: " +
                          api.util.getClassName(c);
            api.util.assert(c instanceof api.content.page.layout.LayoutComponent, message);
            return <api.content.page.layout.LayoutComponent>c;
        }

        getPartComponent(name: api.content.page.ComponentName): api.content.page.part.PartComponent {
            var c = this.getComponentByName(name);

            var message = "Expected component [" + name.toString() + "] to be a api.content.page.part.PartComponent: " +
                          api.util.getClassName(c);
            api.util.assert(c instanceof api.content.page.part.PartComponent, message);
            return <api.content.page.part.PartComponent>c;
        }

        toJson(): RegionJson {

            var componentJsons: api.content.page.PageComponentTypeWrapperJson[] = [];

            this.pageComponents.forEach((component: api.content.page.PageComponent) => {
                componentJsons.push(component.toJson());
            });

            return {
                name: this.name,
                components: componentJsons
            };
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof Region)) {
                return false;
            }

            var other = <Region>o;

            if (!api.EquitableHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            if (!api.EquitableHelper.equals(this.path, other.path)) {
                return false;
            }

            if (!api.EquitableHelper.arrayEquals(this.pageComponents, other.pageComponents)) {
                return false;
            }

            return true;
        }
    }

    export class RegionBuilder {

        name: string;

        pageComponents: api.content.page.PageComponent[] = [];

        path: api.content.page.RegionPath;

        constructor(source?: Region) {
            if (source) {
                this.name = source.getName();
                this.path = source.getPath();
                source.getComponents().forEach((component: api.content.page.PageComponent) => {
                    this.pageComponents.push(component);
                });
            }
        }

        public setName(value: string): RegionBuilder {
            this.name = value;
            return this;
        }

        public setPath(value: api.content.page.RegionPath): RegionBuilder {
            this.path = value;
            return this;
        }

        public addComponent(value: api.content.page.PageComponent): RegionBuilder {
            this.pageComponents.push(value);
            return this;
        }

        public build(): Region {
            return new Region(this);
        }
    }
}