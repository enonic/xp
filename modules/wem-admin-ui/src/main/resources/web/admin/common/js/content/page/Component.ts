module api.content.page {

    import Region = api.content.page.region.Region;

    export class Component implements api.Equitable, api.Cloneable {

        public static PROPERTY_NAME = 'name';

        private index: number = -1;

        private name: ComponentName;

        private parent: Region;

        private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

        constructor(builder?: ComponentBuilder<any>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.parent = builder.parent;
            }
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

        getPath(): ComponentPath {
            return ComponentPath.fromRegionPathAndComponentIndex(this.parent.getPath(), this.index);
        }

        getName(): ComponentName {
            return this.name;
        }

        setName(newValue: ComponentName) {
            var oldValue = this.name;
            this.name = newValue;
            if (!newValue.equals(oldValue)) {
                this.notifyPropertyChanged(Component.PROPERTY_NAME, oldValue, newValue);
            }
        }

        reset() {

        }

        isEmpty(): boolean {
            throw new Error("Must be implemented by inheritors");
        }

        getParent(): Region {
            return this.parent;
        }

        duplicateComponent(): Component {

            var region = this.getParent();
            return region.duplicateComponent(this);
        }

        removeFromParent() {
            this.parent.removeComponent(this);
        }

        onPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners.push(listener);
        }

        unPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners =
            this.propertyChangedListeners.filter((curr: (event: api.PropertyChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyPropertyChanged(property: string, oldValue: ComponentName, newValue: ComponentName) {
            var event = new api.PropertyChangedEvent(property, oldValue, newValue);
            this.propertyChangedListeners.forEach((listener: (event: api.PropertyChangedEvent)=>void) => {
                listener(event);
            })
        }

        toJson(): api.content.page.ComponentTypeWrapperJson {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        toComponentJson(): ComponentJson {

            return {
                "name": this.name.toString()
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

        clone(generateNewPropertyIds: boolean = false): Component {
            throw new Error("Must be implemented by inheritors");
        }
    }

    export class ComponentBuilder<COMPONENT extends Component> {

        name: ComponentName;

        parent: Region;

        constructor(source?: Component) {
            if (source) {
                this.name = source.getName();
                this.parent = source.getParent();
            }
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