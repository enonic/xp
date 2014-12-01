module api.data {

    export class Property implements api.Equitable {

        private parent: PropertySet;

        private name: string;

        private index: number;

        private value: Value;

        private id: PropertyId;

        private propertyChangedListeners: {(event: PropertyChangedEvent):void}[];

        constructor(builder: PropertyBuilder) {
            api.util.assertNotNull(builder.parent, "parent of a Property cannot be null");
            api.util.assertNotNull(builder.name, "name of a Property cannot be null");
            api.util.assertNotNull(builder.index, "index of a Property cannot be null");
            api.util.assertNotNull(builder.value, "value of a Property cannot be null");
            api.util.assertNotNull(builder.id, "id of a Property cannot be null");

            this.parent = builder.parent;
            this.name = builder.name;
            this.index = builder.index;
            this.value = builder.value;
            this.id = builder.id;

            if (this.value.getType().equals(ValueTypes.DATA) && this.value.isNotNull()) {
                this.value.getPropertySet().setContainerProperty(this);
            }

            this.propertyChangedListeners = [];
        }

        setIndex(index: number) {
            this.index = index;
            // TODO: fire event?
        }

        setValue(value: Value) {
            api.util.assertNotNull(value, "value of a Property cannot be null");
            var oldValue = this.value;
            this.value = value;
            if (this.value.getType().equals(ValueTypes.DATA) && this.value.isNotNull()) {
                this.value.getPropertySet().setContainerProperty(this);
            }
            if (!value.equals(oldValue)) {
                this.notifyPropertyChangedEvent(new PropertyChangedEvent(PropertyChangedEventType.CHANGED, this.getPath(), value));
            }
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

        getContentId(): api.content.ContentId {
            return this.value.getContentId();
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

        copy(destinationPropertySet: PropertySet, generateNewPropertyId: boolean = false) {
            var copy = Property.create().
                setId(generateNewPropertyId ? destinationPropertySet.getTree().getNextId() : this.id).
                setName(this.name).
                setValue(this.value).
                setIndex(this.index).
                setParent(destinationPropertySet).
                build();
            return copy;
        }

        prettyPrint(indent?: string) {
            var thisIndent = indent ? indent : "";
            var idAsString = this.getId().toString();
            var valueAsString = this.getValue().getString();
            console.log(thisIndent + idAsString + ": " + valueAsString);
        }

        onPropertyChanged(listener: {(event: PropertyChangedEvent): void;}) {
            this.propertyChangedListeners.push(listener);
        }

        unPropertyChanged(listener: {(event: PropertyChangedEvent): void;}) {
            this.propertyChangedListeners =
            this.propertyChangedListeners.filter((curr) => (curr != listener));
        }

        private notifyPropertyChangedEvent(event: PropertyChangedEvent) {
            this.propertyChangedListeners.forEach((listener) => listener(event));
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

    export class PropertyBuilder {

        parent: PropertySet;

        name: string;

        index: number;

        value: Value;

        id: PropertyId;

        setParent(value: PropertySet): PropertyBuilder {
            this.parent = value;
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