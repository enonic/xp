module api.content.page.region {

    export class Region implements api.Equitable, api.Cloneable {

        public static debug: boolean = false;

        private name: string;

        private components: Component[] = [];

        private parent: LayoutComponent;

        private changedListeners: {(event: BaseRegionChangedEvent):void}[] = [];

        private componentAddedListeners: {(event: ComponentAddedEvent):void}[] = [];

        private componentRemovedListeners: {(event: ComponentRemovedEvent):void}[] = [];

        private componentPropertyChangedListeners: {(event: ComponentPropertyChangedEvent):void}[] = [];

        private propertyValueChangedListeners: {(event: RegionPropertyValueChangedEvent):void}[] = [];

        private componentChangedEventHandler;

        private componentPropertyChangedEventHandler;

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.parent = builder.parent;

            this.componentChangedEventHandler = (event) => {
                if (Region.debug) {
                    console.debug(this.toString() + ".handleComponentChanged: ", event);
                }
                this.notifyRegionPropertyValueChanged("components");
            };

            this.componentPropertyChangedEventHandler = (event) => this.forwardComponentPropertyChangedEvent(event);

            builder.components.forEach((component: Component) => {
                this.registerComponent(component);
            });
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

        empty() {
            if (Region.debug) {
                console.debug(this.toString() + ".empty()", this.components);
            }

            while (this.components.length > 0) {
                // remove component modifies the components array so we can't rely on forEach
                this.removeComponent(this.components[0]);
            }
        }

        addComponent(component: Component, index?: number): Component {
            if (Region.debug) {
                console.debug(this.toString() + ".addComponent: " + component.toString());
            }

            this.registerComponent(component, index);
            this.notifyComponentAdded(component.getPath());

            return component;
        }

        removeComponent(component: Component): Component {
            if (Region.debug) {
                console.debug(this.toString() + ".removeComponent: " + component.toString(), this.components);
            }

            // parent will be cleared on unregister so grab path before it
            var path = component.getPath();
            this.unregisterComponent(component);
            this.notifyComponentRemoved(path);

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

        toString(): string {
            return "Region[" + this.getPath().toString() + "]";
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

        private checkIllegalLayoutComponentWithinLayoutComponent(component: Component, parent: LayoutComponent) {
            if (!!parent && api.ObjectHelper.iFrameSafeInstanceOf(component, LayoutComponent)) {
                throw new Error("Not allowed to have a LayoutComponent within a LayoutComponent: " +
                                component.getPath().toString());
            }
        }

        private refreshIndexes(start?: number) {
            for (var i = Math.min(0, start); i < this.components.length; i++) {
                this.components[i].setIndex(i);
            }
        }

        private registerComponent(component: Component, index?: number) {
            if (Region.debug) {
                console.debug(this.toString() + ".registerComponent: " + component.toString() + " at " + component.getIndex());
            }
            this.checkIllegalLayoutComponentWithinLayoutComponent(component, this.parent);

            if (index >= 0 && index < this.components.length) {

                this.components.splice(index, 0, component);
                // update indexes for inserted component and components after it
                this.refreshIndexes(index);

            } else {

                this.components.push(component);
                component.setIndex(this.components.length - 1);
            }

            component.setParent(this);

            component.onChanged(this.componentChangedEventHandler);
            component.onPropertyChanged(this.componentPropertyChangedEventHandler);
        }

        private unregisterComponent(component: Component) {
            if (Region.debug) {
                console.debug(this.toString() + ".unregisterComponent: " + component.toString(), this.components);
            }
            component.unChanged(this.componentChangedEventHandler);
            component.unPropertyChanged(this.componentPropertyChangedEventHandler);

            var index = component.getIndex();
            if (index == -1) {
                throw new Error(component.toString() + " to remove does not exist in " + this.toString());
            }

            this.components.splice(index, 1);
            // update indexes for inserted component and components after it
            this.refreshIndexes(index);

            component.setParent(null);
            component.setIndex(-1);
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