module api.content.page.region {

    export class Region implements api.Equitable, api.Cloneable {

        private name: string;

        private components: Component[] = [];

        private parent: LayoutComponent;

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.parent = builder.parent;
            this.components = builder.components;
            this.components.forEach((component: Component, index: number) => {
                this.checkIllegalLayoutComponentWithinLayoutComponent(component, this.parent);
                component.setParent(this);
                component.setIndex(index);
            });
        }

        private checkIllegalLayoutComponentWithinLayoutComponent(component: Component,
                                                                 parent: LayoutComponent) {
            var hasParentLayoutComponent = !parent ? false : true;
            if (hasParentLayoutComponent && api.ObjectHelper.iFrameSafeInstanceOf(component, LayoutComponent)) {
                throw new Error("Not allowed to have a LayoutComponent within a LayoutComponent: " +
                                component.getPath().toString());
            }
        }

        getName(): string {
            return this.name;
        }

        setParent(value: LayoutComponent) {
            this.parent = value;
        }

        getParent(): LayoutComponent {
            return this.parent;
        }

        getPath(): RegionPath {
            var parentPath = null;
            if (this.parent) {
                parentPath = this.parent.getPath();
            }
            return new RegionPath(parentPath, this.name);
        }

        isEmpty(): boolean {
            return !this.components || this.components.length == 0;
        }

        ensureUniqueComponentName(wantedName: ComponentName): ComponentName {
            return wantedName;
        }

        duplicateComponent(source: Component): Component {

            var duplicateName = source.getName();

            var duplicatedComponent = source.clone(true);
            duplicatedComponent.setName(duplicateName);
            this.addComponentAfter(duplicatedComponent, source);

            return duplicatedComponent;
        }

        addComponent(component: Component) {
            this.checkIllegalLayoutComponentWithinLayoutComponent(component, this.parent);
            this.components.push(component);
            component.setParent(this);
            component.setIndex(this.components.length - 1);
        }

        /*
         *  Add component after target component. Component will only be added if target component is found.
         */
        addComponentAfter(component: Component, precedingComponent: Component) {

            var precedingIndex = -1;
            if (precedingComponent != null) {
                precedingIndex = precedingComponent.getIndex();
                if (precedingIndex == -1 && this.components.length > 1) {
                    return -1;
                }
            }

            component.setParent(this);

            var index = 0;
            if (precedingIndex > -1) {
                index = precedingIndex + 1;
            }
            this.components.splice(index, 0, component);

            // Update indexes
            this.components.forEach((curr: Component, index: number) => {
                curr.setIndex(index);
            });
        }

        removeComponent(component: Component): Component {
            if (!component) {
                return null;
            }

            var componentIndex = component.getIndex();
            if (componentIndex == -1) {
                throw new Error("Component [" + component.getPath().toString() + "] to remove does not exist in region: " +
                                this.getPath().toString());
            }
            this.components.splice(componentIndex, 1);

            // Update indexes
            this.components.forEach((curr: Component, index: number) => {
                curr.setIndex(index);
            });
            return component;
        }

        getComponents(): Component[] {
            return this.components;
        }

        getComponentByIndex(index: number): Component {
            var component = this.components[index];
            api.util.assertState(component.getIndex() == index,
                    "Index of Component is not as expected. Expected [" + index + "], was: " + component.getIndex());
            return  component;
        }

        removeComponents() {
            while (this.components.length > 0) {
                var component = this.components.pop();
                component.setParent(null);
                component.setIndex(-1);
            }
        }

        toJson(): RegionJson {

            var componentJsons: ComponentTypeWrapperJson[] = [];

            this.components.forEach((component: Component) => {
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

            if (!api.ObjectHelper.arrayEquals(this.components, other.components)) {
                return false;
            }

            return true;
        }

        clone(generateNewPropertyIds: boolean = false): Region {
            return new RegionBuilder(this, generateNewPropertyIds).build();
        }
    }

    export class RegionBuilder {

        name: string;

        components: Component[] = [];

        parent: LayoutComponent;

        constructor(source?: Region, generateNewPropertyIds: boolean = false) {
            if (source) {
                this.name = source.getName();
                this.parent = source.getParent(); //TODO; Should clone have same parent at all times?
                source.getComponents().forEach((component: Component) => {
                    this.components.push(component.clone(generateNewPropertyIds));
                });
            }
        }

        public setName(value: string): RegionBuilder {
            this.name = value;
            return this;
        }

        public setParent(value: LayoutComponent): RegionBuilder {
            this.parent = value;
            return this;
        }

        public addComponent(value: Component): RegionBuilder {
            this.components.push(value);
            return this;
        }

        public build(): Region {
            return new Region(this);
        }
    }
}