module api.data {

    export class Property implements api.Equitable {

        public debug: boolean = false;
        
        private parent: PropertySet;

        private array: PropertyArray;

        private name: string;

        private index: number;

        private value: Value;

        private id: PropertyId;

        private propertyIndexChangedListeners: {(event: PropertyIndexChangedEvent):void}[] = [];

        private propertyValueChangedListeners: {(event: PropertyValueChangedEvent):void}[] = [];

        constructor(builder: PropertyBuilder) {
            api.util.assertNotNull(builder.array, "array of a Property cannot be null");
            api.util.assertNotNull(builder.name, "name of a Property cannot be null");
            api.util.assertNotNull(builder.index, "index of a Property cannot be null");
            api.util.assertNotNull(builder.value, "value of a Property cannot be null");

            this.array = builder.array;
            this.parent = builder.array.getParent();
            this.name = builder.name;
            this.index = builder.index;
            this.value = builder.value;
            this.id = builder.id;

            if (this.value.getType().equals(ValueTypes.DATA) && this.value.isNotNull()) {
                var valuePropertySet = this.value.getPropertySet();
                valuePropertySet.setContainerProperty(this);
            }
        }

        setId(id: PropertyId) {
            this.id = id;
        }

        setIndex(newIndex: number) {
            var oldIndex = newIndex;
            this.index = newIndex;

            if (oldIndex != newIndex) {
                this.notifyPropertyIndexChangedEvent(oldIndex, newIndex);
            }
        }

        setValue(value: Value) {
            api.util.assertNotNull(value, "value of a Property cannot be null");
            var oldValue = this.value;
            this.value = value;

            // Register listeners on PropertySet
            if (this.value.getType().equals(ValueTypes.DATA) && this.value.isNotNull()) {
                var propertySet = this.value.getPropertySet();
                propertySet.setContainerProperty(this);
                this.array.registerPropertySetListeners(propertySet);
            }

            // Unregister listeners on PropertySet from oldValue
            if (oldValue.getType().equals(ValueTypes.DATA) && oldValue.isNotNull()) {
                var removedPropertySet = oldValue.getPropertySet();
                removedPropertySet.setContainerProperty(null);
                this.array.unregisterPropertySetListeners(removedPropertySet);
            }

            if (!value.equals(oldValue)) {
                this.notifyPropertyValueChangedEvent(oldValue, value);
            }
        }

        /**
         * Detach this Property from it's array and parent. Should be called when removed from the array.
         */
        detach() {
            this.array = null;
            this.parent = null;
            this.id = null;
            this.propertyIndexChangedListeners = [];
            this.propertyValueChangedListeners = [];
        }

        getId(): PropertyId {
            return this.id;
        }

        getParent(): PropertySet {
            return this.parent;
        }

        hasParentProperty(): boolean {
            return !!this.getParentProperty();
        }

        getParentProperty(): Property {
            return this.parent.getProperty();
        }

        getPath(): PropertyPath {
            if (this.hasParentProperty()) {
                return PropertyPath.fromParent(this.getParentProperty().getPath(), new PropertyPathElement(this.name, this.index))
            }
            else {
                return PropertyPath.fromPathElement(new PropertyPathElement(this.name, this.index));
            }
        }

        getName(): string {
            return this.name;
        }

        getIndex(): number {
            return this.index;
        }

        getType(): ValueType {
            return this.value.getType();
        }

        getValue(): Value {
            return this.value;
        }

        hasNullValue(): boolean {
            return this.value.isNull();
        }

        hasNonNullValue(): boolean {
            return !this.value.isNull();
        }

        getString(): string {
            return this.value.getString();
        }

        getBoolean(): boolean {
            return this.value.getBoolean();
        }

        getDate(): Date {
            return this.value.getDate();
        }

        getSet(): PropertySet {
            return this.value.getPropertySet();
        }

        getGeoPoint(): api.util.GeoPoint {
            return this.value.getGeoPoint();
        }

        getLocalTime(): api.util.LocalTime {
            return this.value.getLocalTime();
        }

        getReference(): api.util.Reference {
            return this.value.getReference();
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Property)) {
                return false;
            }

            var other = <Property>o;

            if (!api.ObjectHelper.equals(this.id, other.id)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            if (!api.ObjectHelper.numberEquals(this.index, other.index)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.value, other.value)) {
                return false;
            }

            return true;
        }

        copy(destinationPropertyArray: PropertyArray, generateNewPropertyId: boolean = false) {

            var destinationTree = destinationPropertyArray.getTree();
            var id: PropertyId = null;
            if (generateNewPropertyId) {
                id = destinationTree ? destinationTree.getNextId() : null;
            }
            else {
                id = this.id;
            }

            var value: Value;
            if (this.value.isPropertySet() && this.value.isNotNull()) {
                var copiedPropertySet = this.value.getPropertySet().copy(destinationTree, generateNewPropertyId);
                value = new Value(copiedPropertySet, ValueTypes.DATA);
            }
            else {
                value = this.value;
            }

            var copy = Property.create().
                setId(id).
                setName(this.name).
                setValue(value).
                setIndex(this.index).
                setArray(destinationPropertyArray).
                build();
            return copy;
        }

        onPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.propertyIndexChangedListeners.push(listener);
        }

        unPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.propertyIndexChangedListeners =
            this.propertyIndexChangedListeners.filter((curr) => (curr != listener));
        }

        private notifyPropertyIndexChangedEvent(previousIndex: number, newIndex: number) {
            var event = new PropertyIndexChangedEvent(this, previousIndex, newIndex);
            if (this.debug) {
                console.debug("Property[" + this.getPath().toString() + "].notifyPropertyIndexChangedEvent: " + event.getPath().toString());
            }
            this.propertyIndexChangedListeners.forEach((listener) => listener(event));
        }

        onPropertyValueChanged(listener: {(event: PropertyValueChangedEvent): void;}) {
            this.propertyValueChangedListeners.push(listener);
        }

        unPropertyValueChanged(listener: {(event: PropertyValueChangedEvent): void;}) {
            this.propertyValueChangedListeners =
            this.propertyValueChangedListeners.filter((curr) => (curr != listener));
        }

        private notifyPropertyValueChangedEvent(previousValue: Value, newValue: Value) {
            var event = new PropertyValueChangedEvent(this, previousValue, newValue);
            if (this.debug) {
                console.debug("Property[" + this.getPath().toString() + "].notifyPropertyValueChangedEvent: " + event.getPath().toString());
            }
            this.propertyValueChangedListeners.forEach((listener) => listener(event));
        }

        public static checkName(name: string) {
            if (name == null) {
                throw new Error("Property name cannot be null");
            }
            if (api.util.StringHelper.isBlank(name)) {
                throw new Error("Property name cannot be blank");
            }
            if (name.indexOf(".") >= 0) {
                throw new Error("Property name cannot contain .");
            }
            if (name.indexOf("[") >= 0 || name.indexOf("]") >= 0) {
                throw new Error("Property name cannot contain [ or ]");
            }
        }

        public static create(): PropertyBuilder {
            return new PropertyBuilder();
        }
    }

    class PropertyBuilder {

        array: PropertyArray;

        name: string;

        index: number;

        value: Value;

        id: PropertyId;

        setArray(value: PropertyArray): PropertyBuilder {
            this.array = value;
            return this;
        }

        setName(value: string): PropertyBuilder {
            this.name = value;
            return this;
        }

        setIndex(value: number): PropertyBuilder {
            this.index = value;
            return this;
        }

        setValue(value: Value): PropertyBuilder {
            this.value = value;
            return this;
        }

        setId(value: PropertyId): PropertyBuilder {
            this.id = value;
            return this;
        }

        build(): Property {
            return new Property(this);
        }
    }
}