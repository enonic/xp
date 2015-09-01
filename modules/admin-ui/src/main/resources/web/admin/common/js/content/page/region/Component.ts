module api.content.page.region {

    export class Component implements api.Equitable, api.Cloneable {

        public static PROPERTY_NAME = 'name';

        private index: number = -1;

        private name: ComponentName;

        private parent: Region;

        private changedListeners: {(event: ComponentChangedEvent):void}[] = [];

        private propertyChangedListeners: {(event: ComponentPropertyChangedEvent):void}[] = [];

        private propertyValueChangedListeners: {(event: ComponentPropertyValueChangedEvent):void}[] = [];

        private resetListeners: {(event: ComponentResetEvent):void}[] = [];

        constructor(builder: ComponentBuilder<any>) {

            this.name = builder.name;
            this.index = builder.index;
            this.parent = builder.parent;
        }

        setParent(parent: Region) {
            this.parent = parent;
        }

        setIndex(value: number) {
            this.index = value;
        }

        getIndex(): number {
            return this.index;
        }

        hasPath(): boolean {
            return this.parent && this.index >= 0;
        }

        getPath(): ComponentPath {
            return this.hasPath() ? ComponentPath.fromRegionPathAndComponentIndex(this.parent.getPath(), this.index) : null;
        }

        getName(): ComponentName {
            return this.name;
        }

        setName(newValue: ComponentName) {
            var oldValue = this.name;
            this.name = newValue;
            if (!api.ObjectHelper.equals(oldValue, newValue)) {
                this.notifyPropertyChanged(Component.PROPERTY_NAME);
            }
        }

        doReset() {
            throw new Error("Must be implemented by inheritors");
        }

        reset() {
            this.doReset();
            this.notifyResetEvent();
        }

        isEmpty(): boolean {
            throw new Error("Must be implemented by inheritors");
        }

        getParent(): Region {
            return this.parent;
        }

        duplicate(): Component {
            var duplicateName = this.getName();
            var duplicatedComponent = this.clone();
            duplicatedComponent.setName(duplicateName);

            return duplicatedComponent;
        }

        remove() {
            this.parent.removeComponent(this);
        }

        toJson(): ComponentTypeWrapperJson {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        toString(): string {
            return "Component[" + (this.name ? this.name.toString() : "") + "]";
        }

        toComponentJson(): ComponentJson {

            return {
                "name": this.name ? this.name.toString() : null
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Component)) {
                return false;
            }

            var other = <Component>o;

            if (!api.ObjectHelper.equals(this.name, other.name)) {
                return false;
            }

            return true;
        }

        clone(): Component {
            throw new Error("Must be implemented by inheritors");
        }

        onChanged(listener: (event: ComponentChangedEvent)=>void) {
            this.changedListeners.push(listener);
        }

        unChanged(listener: (event: ComponentChangedEvent)=>void) {
            this.changedListeners =
            this.changedListeners.filter((curr: (event: ComponentPropertyChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyChangedEvent(event: ComponentChangedEvent) {
            this.changedListeners.forEach((listener: (event: ComponentChangedEvent)=>void) => {
                listener(event);
            })
        }

        onReset(listener: (event: ComponentResetEvent)=>void) {
            this.resetListeners.push(listener);
        }

        unReset(listener: (event: ComponentResetEvent)=>void) {
            this.resetListeners = this.resetListeners.filter((curr: (event: ComponentResetEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyResetEvent() {
            var event = new ComponentResetEvent(this.getPath());
            this.resetListeners.forEach((listener: (event: ComponentResetEvent)=>void) => {
                listener(event);
            })
        }

        /**
         * Observe when a property of Component have been reassigned.
         */
        onPropertyChanged(listener: (event: ComponentPropertyChangedEvent)=>void) {
            this.propertyChangedListeners.push(listener);
        }

        unPropertyChanged(listener: (event: ComponentPropertyChangedEvent)=>void) {
            this.propertyChangedListeners =
            this.propertyChangedListeners.filter((curr: (event: ComponentPropertyChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        notifyPropertyChanged(propertyName: string) {
            var event = ComponentPropertyChangedEvent.create().setComponent(this).setPropertyName(propertyName).build();
            this.propertyChangedListeners.forEach((listener: (event: ComponentPropertyChangedEvent)=>void) => {
                listener(event);
            });
            this.notifyChangedEvent(event);
        }

        forwardComponentPropertyChangedEvent(event: ComponentPropertyChangedEvent) {
            this.propertyChangedListeners.forEach((listener: (event: ComponentPropertyChangedEvent)=>void) => {
                listener(event);
            });
        }

        /**
         * Observe when a property of Component have changed (happens only for mutable objects).
         */
        onPropertyValueChanged(listener: (event: ComponentPropertyValueChangedEvent)=>void) {
            this.propertyValueChangedListeners.push(listener);
        }

        unPropertyValueChanged(listener: (event: ComponentPropertyValueChangedEvent)=>void) {
            this.propertyValueChangedListeners =
            this.propertyValueChangedListeners.filter((curr: (event: ComponentPropertyValueChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        notifyPropertyValueChanged(propertyName: string) {
            var event = new ComponentPropertyValueChangedEvent(this.getPath(), propertyName);
            this.propertyValueChangedListeners.forEach((listener: (event: ComponentPropertyValueChangedEvent)=>void) => {
                listener(event);
            });
            this.notifyChangedEvent(event);
        }
    }

    export class ComponentBuilder<COMPONENT extends Component> {

        name: ComponentName;

        index: number;

        parent: Region;

        constructor(source?: Component) {
            if (source) {
                this.name = source.getName();
                this.parent = source.getParent();
                this.index = source.getIndex();
            }
        }

        public setIndex(value: number): ComponentBuilder<COMPONENT> {
            this.index = value;
            return this;
        }

        public setName(value: ComponentName): ComponentBuilder<COMPONENT> {
            this.name = value;
            return this;
        }

        public setParent(value: Region): ComponentBuilder<COMPONENT> {
            this.parent = value;
            return this;
        }

        public build(): COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}