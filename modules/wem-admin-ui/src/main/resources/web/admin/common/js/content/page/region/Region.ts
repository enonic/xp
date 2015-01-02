module api.content.page.region {

    export class Region implements api.Equitable, api.Cloneable {

        private name: string;

        private pageComponents: api.content.page.Component[] = [];

        private parent: api.content.page.layout.LayoutComponent;

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.parent = builder.parent;
            this.pageComponents = builder.pageComponents;
            this.pageComponents.forEach((pageComponent: Component, index: number) => {
                this.checkIllegalLayoutComponentWithinLayoutComponent(pageComponent, this.parent);
                pageComponent.setParent(this);
                pageComponent.setIndex(index);
            });
        }

        private checkIllegalLayoutComponentWithinLayoutComponent(pageComponent: Component,
                                                                 parent: api.content.page.layout.LayoutComponent) {
            var hasParentLayoutComponent = !parent ? false : true;
            if (hasParentLayoutComponent && api.ObjectHelper.iFrameSafeInstanceOf(pageComponent, api.content.page.layout.LayoutComponent)) {
                throw new Error("Not allowed to have a LayoutComponent within a LayoutComponent: " +
                                pageComponent.getPath().toString());
            }
        }

        getName(): string {
            return this.name;
        }

        setParent(value: api.content.page.layout.LayoutComponent) {
            this.parent = value;
        }

        getParent(): api.content.page.layout.LayoutComponent {
            return this.parent;
        }

        getPath(): api.content.page.RegionPath {
            var parentPath = null;
            if (this.parent) {
                parentPath = this.parent.getPath();
            }
            return new api.content.page.RegionPath(parentPath, this.name);
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

        addComponent(pageComponent: Component) {
            this.checkIllegalLayoutComponentWithinLayoutComponent(pageComponent, this.parent);
            this.pageComponents.push(pageComponent);
            pageComponent.setParent(this);
            pageComponent.setIndex(this.pageComponents.length - 1);
        }

        /*
         *  Add component after target component. Component will only be added if target component is found.
         */
        addComponentAfter(component: api.content.page.Component, precedingComponent: Component) {

            var precedingIndex = -1;
            if (precedingComponent != null) {
                precedingIndex = precedingComponent.getIndex();
                if (precedingIndex == -1 && this.pageComponents.length > 1) {
                    return -1;
                }
            }

            component.setParent(this);

            var index = 0;
            if (precedingIndex > -1) {
                index = precedingIndex + 1;
            }
            this.pageComponents.splice(index, 0, component);

            // Update indexes
            this.pageComponents.forEach((curr: Component, index: number) => {
                curr.setIndex(index);
            });
        }

        removeComponent(component: api.content.page.Component): api.content.page.Component {
            if (!component) {
                return null;
            }

            var componentIndex = component.getIndex();
            if (componentIndex == -1) {
                throw new Error("Component [" + component.getPath().toString() + "] to remove does not exist in region: " +
                                this.getPath().toString());
            }
            this.pageComponents.splice(componentIndex, 1);

            // Update indexes
            this.pageComponents.forEach((curr: Component, index: number) => {
                curr.setIndex(index);
            });
            return component;
        }

        getComponents(): api.content.page.Component[] {
            return this.pageComponents;
        }

        getComponentByIndex(index: number): api.content.page.Component {
            var pageComponent = this.pageComponents[index];
            api.util.assertState(pageComponent.getIndex() == index,
                    "Index of Component is not as expected. Expected [" + index + "], was: " + pageComponent.getIndex());
            return  pageComponent;
        }

        removePageComponents() {
            while (this.pageComponents.length > 0) {
                var pageComponent = this.pageComponents.pop();
                pageComponent.setParent(null);
                pageComponent.setIndex(-1);
            }
        }

        toJson(): RegionJson {

            var componentJsons: api.content.page.ComponentTypeWrapperJson[] = [];

            this.pageComponents.forEach((component: api.content.page.Component) => {
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

            if (!api.ObjectHelper.arrayEquals(this.pageComponents, other.pageComponents)) {
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

        pageComponents: api.content.page.Component[] = [];

        parent: api.content.page.layout.LayoutComponent;

        constructor(source?: Region, generateNewPropertyIds: boolean = false) {
            if (source) {
                this.name = source.getName();
                this.parent = source.getParent(); //TODO; Should clone have same parent at all times?
                source.getComponents().forEach((component: api.content.page.Component) => {
                    this.pageComponents.push(component.clone(generateNewPropertyIds));
                });
            }
        }

        public setName(value: string): RegionBuilder {
            this.name = value;
            return this;
        }

        public setParent(value: api.content.page.layout.LayoutComponent): RegionBuilder {
            this.parent = value;
            return this;
        }

        public addComponent(value: api.content.page.Component): RegionBuilder {
            this.pageComponents.push(value);
            return this;
        }

        public build(): Region {
            return new Region(this);
        }
    }
}