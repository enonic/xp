module api.content.page.region {

    export class Region implements api.Equitable, api.Cloneable {

        private name: string;

        private pageComponents: api.content.page.PageComponent[] = [];

        private componentByName: {[s:string] : api.content.page.PageComponent;} = {};

        private parent: api.content.page.ComponentPath;

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.parent = builder.parent;
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

        setParent(path: api.content.page.ComponentPath) {
            this.parent = path;
            this.pageComponents.forEach((component: api.content.page.PageComponent) => {
                component.setParent(this.getPath());
            });
        }

        getParent(): api.content.page.ComponentPath {
            return this.parent;
        }

        getPath(): api.content.page.RegionPath {
            return new api.content.page.RegionPath(this.parent, this.name);
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

        duplicateComponent(name: ComponentName): PageComponent {

            var existing = this.getComponentByName(name);
            if (!existing) {
                return null;
            }

            var duplicateName = this.resolveNameOfDuplicatedComponent(existing.getName());

            var duplicatedComponent = existing.clone();
            duplicatedComponent.setName(duplicateName);
            this.addComponentAfter(duplicatedComponent, existing.getName());

            return duplicatedComponent;
        }

        private resolveNameOfDuplicatedComponent(nameOfSource: api.content.page.ComponentName): api.content.page.ComponentName {

            var nameWithoutCountPostFix = null;
            if (!nameOfSource.hasCountPostfix()) {
                nameWithoutCountPostFix = nameOfSource;
            }
            else {
                nameWithoutCountPostFix = nameOfSource.removeCountPostfix();
            }

            var count = this.countNumberOfDuplicates(nameWithoutCountPostFix);
            var possibleNewName = nameWithoutCountPostFix.createDuplicate(count + 1);

            while (this.nameAlreadyInUse(possibleNewName)) {
                possibleNewName = nameOfSource.createDuplicate(++count);
            }

            return possibleNewName;
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

            component.setParent(this.getPath());
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

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Region)) {
                return false;
            }

            var other = <Region>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.parent, other.parent)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.pageComponents, other.pageComponents)) {
                return false;
            }

            return true;
        }

        clone(): Region {
            return new RegionBuilder(this).build();
        }
    }

    export class RegionBuilder {

        name: string;

        pageComponents: api.content.page.PageComponent[] = [];

        parent: api.content.page.ComponentPath;

        constructor(source?: Region) {
            if (source) {
                this.name = source.getName();
                this.parent = source.getParent();
                source.getComponents().forEach((component: api.content.page.PageComponent) => {
                    this.pageComponents.push(component.clone());
                });
            }
        }

        public setName(value: string): RegionBuilder {
            this.name = value;
            return this;
        }

        public setParent(value: api.content.page.ComponentPath): RegionBuilder {
            this.parent = value;
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