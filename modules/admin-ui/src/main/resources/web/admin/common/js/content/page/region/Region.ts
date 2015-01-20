module api.content.page.region {

    export class Region implements api.Equitable, api.Cloneable {

        public debug: boolean = true;
        
        private name: string;

        private components: Component[] = [];

        private parent: LayoutComponent;

        private changedListeners: {(event: BaseRegionChangedEvent):void}[] = [];

        private componentAddedListeners: {(event: ComponentAddedEvent):void}[] = [];

        private componentRemovedListeners: {(event: ComponentRemovedEvent):void}[] = [];

        private componentPropertyChangedListeners: {(event: ComponentPropertyChangedEvent):void}[] = [];

        private propertyValueChangedListeners: {(event: RegionPropertyValueChangedEvent):void}[] = [];

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.parent = builder.parent;
            this.components = builder.components;
            this.components.forEach((component: Component, index: number) => {
                this.checkIllegalLayoutComponentWithinLayoutComponent(component, this.parent);
                component.setParent(this);
                component.setIndex(index);

                this.registerComponentListeners(component);
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

        private registerComponentListeners(component: Component) {
            if (this.handleComponentChanged.bind) {
                component.onChanged(this.handleComponentChanged.bind(this));
                component.onPropertyChanged(this.forwardComponentPropertyChangedEvent.bind(this));
            }
            else {
                // PhantomJS does not support bind
                component.onChanged((event) => {
                    this.handleComponentChanged(event);
                });
                component.onPropertyChanged((event) => {
                    this.forwardComponentPropertyChangedEvent(event);
                });
            }
        }

        private unregisterComponentListeners(component: Component) {
            component.unChanged(this.handleComponentChanged);
            component.unPropertyChanged(this.forwardComponentPropertyChangedEvent);
        }

        private handleComponentChanged(event: ComponentChangedEvent) {
            if (this.debug) {
                console.debug("Region[" + this.getPath().toString() + "].handleComponentChanged: ", event);
            }
            this.notifyRegionPropertyValueChanged("components");
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
            var parentPath: ComponentPath = null;
            if (this.parent) {
                parentPath = this.parent.getPath();
            }
            return new RegionPath(parentPath, this.name);
        }

        isEmpty(): boolean {
            return !this.components || this.components.length == 0;
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

            this.notifyComponentAdded(component.getPath());

            this.registerComponentListeners(component);
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

            var componentPath = component.getPath();

            this.components.splice(componentIndex, 1);

            // Update indexes
            this.components.forEach((curr: Component, index: number) => {
                curr.setIndex(index);
            });

            component.setParent(null);
            component.setIndex(-1);

            this.notifyComponentRemoved(componentPath);
            this.unregisterComponentListeners(component);

            return component;
        }

        getComponents(): Component[] {
            return this.components;
        }

        getComponentByIndex(index: number): Component {
            var component = this.components[index];
            api.util.assertState(component.getIndex() == index,
                "Index of Component is not as expected. Expected [" + index + "], was: " + component.getIndex());
            return component;
        }

        removeComponents() {

            while (this.components.length > 0) {
                var component = this.components.pop();
                var componentPath = component.getPath();

                this.notifyComponentRemoved(componentPath);
                this.unregisterComponentListeners(component);

                component.setParent(null);
                component.setIndex(-1);
            }
        }

        toJson(): RegionJson {

            var componentJsons: ComponentTypeWrapperJson[] = [];

            this.components.forEach((component: Component) => {
                componentJsons.push(component.toJson());
            });

            return <RegionJson>{
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

        onChanged(listener: (event: BaseRegionChangedEvent)=>void) {
            this.changedListeners.push(listener);
        }

        unChanged(listener: (event: BaseRegionChangedEvent)=>void) {
            this.changedListeners =
            this.changedListeners.filter((curr: (event: BaseRegionChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyChangedEvent(event: BaseRegionChangedEvent) {
            this.changedListeners.forEach((listener: (event: BaseRegionChangedEvent)=>void) => {
                listener(event);
            })
        }

        onComponentAdded(listener: (event: ComponentAddedEvent)=>void) {
            this.componentAddedListeners.push(listener);
        }

        unComponentAdded(listener: (event: ComponentAddedEvent)=>void) {
            this.componentAddedListeners =
            this.componentAddedListeners.filter((curr: (event: ComponentAddedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyComponentAdded(componentPath: ComponentPath) {
            var event = new ComponentAddedEvent(this.getPath(), componentPath);
            this.componentAddedListeners.forEach((listener: (event: ComponentAddedEvent)=>void) => {
                listener(event);
            });
            this.notifyChangedEvent(event);
        }

        onComponentRemoved(listener: (event: ComponentRemovedEvent)=>void) {
            this.componentRemovedListeners.push(listener);
        }

        unComponentRemoved(listener: (event: ComponentRemovedEvent)=>void) {
            this.componentRemovedListeners =
            this.componentRemovedListeners.filter((curr: (event: ComponentRemovedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyComponentRemoved(componentPath: ComponentPath) {
            var event = new ComponentRemovedEvent(this.getPath(), componentPath);
            this.componentRemovedListeners.forEach((listener: (event: ComponentRemovedEvent)=>void) => {
                listener(event);
            });
            this.notifyChangedEvent(event);
        }

        onComponentPropertyChangedEvent(listener: (event: ComponentPropertyChangedEvent)=>void) {
            this.componentPropertyChangedListeners.push(listener);
        }

        unComponentPropertyChangedEvent(listener: (event: ComponentPropertyChangedEvent)=>void) {
            this.componentPropertyChangedListeners =
            this.componentPropertyChangedListeners.filter((curr: (event: ComponentPropertyChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        private forwardComponentPropertyChangedEvent(event: ComponentPropertyChangedEvent) {
            this.componentPropertyChangedListeners.forEach((listener: (event: ComponentPropertyChangedEvent)=>void) => {
                listener(event);
            });
        }

        onRegionPropertyValueChanged(listener: (event: RegionPropertyValueChangedEvent)=>void) {
            this.propertyValueChangedListeners.push(listener);
        }

        unRegionPropertyValueChanged(listener: (event: RegionPropertyValueChangedEvent)=>void) {
            this.propertyValueChangedListeners =
            this.propertyValueChangedListeners.filter((curr: (event: RegionPropertyValueChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyRegionPropertyValueChanged(propertyName: string) {
            var event = new RegionPropertyValueChangedEvent(this.getPath(), propertyName);
            this.propertyValueChangedListeners.forEach((listener: (event: RegionPropertyValueChangedEvent)=>void) => {
                listener(event);
            });
            this.notifyChangedEvent(event);
        }

        static create(source?: Region, generateNewPropertyIds: boolean = false): RegionBuilder {
            return new RegionBuilder(source, generateNewPropertyIds);
        }
    }

    class RegionBuilder {

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