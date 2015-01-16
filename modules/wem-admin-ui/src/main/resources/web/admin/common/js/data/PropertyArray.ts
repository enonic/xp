module api.data {

    export class PropertyArray implements api.Equitable {

        public debug: boolean = false;

        private tree: PropertyTree;

        private parent: PropertySet;

        private name: string;

        private type: ValueType;

        private array: Property[];

        private propertyAddedListeners: {(event: PropertyAddedEvent):void}[] = [];

        private propertyRemovedListeners: {(event: PropertyRemovedEvent):void}[] = [];

        private propertyIndexChangedListeners: {(event: PropertyIndexChangedEvent):void}[] = [];

        private propertyValueChangedListeners: {(event: PropertyValueChangedEvent):void}[] = [];

        constructor(builder: PropertyArrayBuilder) {
            this.tree = builder.parent.getTree();
            this.parent = builder.parent;
            this.name = builder.name;
            this.type = builder.type;
            this.array = [];
        }

        forEach(callBack: {(property: Property, index: number): void;}) {
            this.array.forEach((property: Property, index: number) => {
                callBack(property, index);
            });
        }

        getTree(): PropertyTree {
            return this.tree;
        }

        getParent(): PropertySet {
            return this.parent;
        }

        getParentPropertyPath(): PropertyPath {
            if (this.parent.getProperty() == null) {
                return PropertyPath.ROOT;
            }
            return this.parent.getProperty().getPath();
        }

        getName(): string {
            return this.name;
        }

        getType(): ValueType {
            return this.type;
        }

        private checkType(type: ValueType) {

            if (!this.type.equals(type)) {
                throw new Error("This PropertyArray expects only properties with value of type '" + this.type + "', got: " +
                                type);
            }
        }

        private checkIndex(index: number) {

            if (!(index <= this.array.length)) {
                throw new Error("Index out of bounds: index: " + index + ", size: " + this.array.length);
            }
        }

        newSet(): PropertySet {
            return this.parent.newSet();
        }

        /**
         * Package protected. Not to be used outside module.
         */
        addProperty(property: Property) {

            api.util.assert(property.getName() == this.name,
                "Expected name of added Property to be [" + this.name + "], got: " + property.getName());
            api.util.assert(property.getType().equals(this.getType()),
                "Expected type of added Property to be [" + this.type.toString() + "], got: " + property.getType().toString());
            api.util.assert(property.getIndex() == this.array.length,
                "Expected index of added Property to be [" + this.array.length + "], got: " + property.getIndex());

            this.array.push(property);

            this.registerPropertyListeners(property);
        }

        add(value: Value): Property {
            this.checkType(value.getType());

            var property = Property.create().
                setArray(this).
                setName(this.name).
                setIndex(this.array.length).
                setValue(value).
                setId(this.tree ? this.tree.getNextId() : null).
                build();

            this.array.push(property);

            if (this.tree) {
                this.tree.registerProperty(property);
                // Attached any detached PropertySet...
                if (this.type.equals(ValueTypes.DATA) && value.isNotNull()) {
                    var addedPropertySet = value.getPropertySet();
                    if (addedPropertySet && addedPropertySet.isDetached()) {
                        addedPropertySet.attachToTree(this.tree);
                    }
                }
            }

            this.notifyPropertyAdded(property);
            this.registerPropertyListeners(property);
            return property;
        }

        addSet(): PropertySet {
            var newSet = this.parent.newSet();
            this.add(new Value(newSet, ValueTypes.DATA));
            return newSet;
        }

        set(index: number, value: Value): Property {
            this.checkType(value.getType());
            this.checkIndex(index);

            var property;

            if (this.get(index) != null) {
                property = this.array[index];
                property.setValue(value);
            }
            else {
                property = Property.create().
                    setArray(this).
                    setName(this.name).
                    setIndex(this.array.length).
                    setValue(value).
                    setId(this.tree ? this.tree.getNextId() : null).
                    build();
                this.array[index] = property;

                if (this.tree) {
                    this.tree.registerProperty(property);
                }

                this.notifyPropertyAdded(property);
                this.registerPropertyListeners(property);
            }
            return property;
        }

        move(index: number, destinationIndex: number) {
            var toBeMoved = this.array[index];
            api.util.ArrayHelper.moveElement(index, destinationIndex, this.array);
            toBeMoved.setIndex(destinationIndex);

            this.forEach((property: Property, index: number) => {
                property.setIndex(index);
            });
        }

        remove(index: number) {

            var propertyToRemove = this.get(index);
            if (!propertyToRemove) {
                throw new Error("Property not found: " +
                                PropertyPath.fromParent(this.getParentPropertyPath(), new PropertyPathElement(name, index)));
            }

            this.array.splice(index, 1);

            if (this.tree) {
                this.tree.unregisterProperty(propertyToRemove.getId());
            }

            this.notifyPropertyRemoved(propertyToRemove);
            this.unregisterPropertyListeners(propertyToRemove);
        }

        public exists(index: number): boolean {
            return !!this.get(index);
        }

        public get(index: number): Property {

            if (index >= this.array.length) {
                return null;
            }
            return this.array[index];
        }

        public getValue(index: number): Value {
            var property = this.get(index);
            return !property ? null : property.getValue();
        }

        public getSet(index: number): PropertySet {
            var property = this.get(index);
            return !property ? null : property.getSet();
        }

        public getSize(): number {
            return this.array.length;
        }

        /**
         * Returns a copy of the array of properties.
         */
        public getProperties(): Property[] {
            return this.array.slice(0);
        }

        public equals(o: any): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PropertyArray)) {
                return false;
            }

            var other = <PropertyArray>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.type, other.type)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.array, other.array)) {
                return false;
            }

            return true;
        }

        copy(destinationPropertySet: PropertySet, generateNewPropertyIds: boolean = false): PropertyArray {

            var copy = PropertyArray.create().
                setName(this.name).
                setType(this.type).
                setParent(destinationPropertySet).
                build();

            this.array.forEach((sourceProperty: Property) => {
                copy.addProperty(sourceProperty.copy(copy, generateNewPropertyIds));
            });

            return copy;
        }

        private registerPropertyListeners(property: Property) {
            if (this.debug) {
                console.debug("PropertyArray[" + this.getParentPropertyPath().toString() + "." + this.getName() +
                              "].registerPropertyListeners: " +
                              property.getPath().toString());
            }

            if (this.type.equals(ValueTypes.DATA) && property.hasNonNullValue()) {
                // Ensure events from added PropertySet is forwarded
                this.registerPropertySetListeners(property.getSet());
            }

            if (this.forwardPropertyIndexChangedEvent.bind) {
                property.onPropertyIndexChanged(this.forwardPropertyIndexChangedEvent.bind(this));
                property.onPropertyValueChanged(this.forwardPropertyValueChangedEvent.bind(this));
            }
            else {
                // PhantomJS does not support bind
                property.onPropertyIndexChanged((event) => {
                    this.forwardPropertyIndexChangedEvent(event);
                });
                property.onPropertyValueChanged((event) => {
                    this.forwardPropertyValueChangedEvent(event);
                });
            }
        }

        private unregisterPropertyListeners(property: Property) {
            property.unPropertyIndexChanged(this.forwardPropertyIndexChangedEvent);
            property.unPropertyValueChanged(this.forwardPropertyValueChangedEvent);

            if (property.hasNonNullValue() && property.getType().equals(ValueTypes.DATA)) {
                var propertySet = property.getSet();
                this.unregisterPropertySetListeners(propertySet);
            }
        }

        /**
         * Package protected. Not to be used outside module.
         */
        registerPropertySetListeners(propertySet: PropertySet) {

            if (this.debug) {
                console.debug("PropertyArray[" + this.getParentPropertyPath().toString() + "." + this.getName() +
                              "].registerPropertySetListeners: " +
                              propertySet.getPropertyPath().toString());
            }

            if (this.forwardPropertyAddedEvent.bind) {
                propertySet.onPropertyAdded(this.forwardPropertyAddedEvent.bind(this));
                propertySet.onPropertyRemoved(this.forwardPropertyRemovedEvent.bind(this));
                propertySet.onPropertyIndexChanged(this.forwardPropertyIndexChangedEvent.bind(this));
                propertySet.onPropertyValueChanged(this.forwardPropertyValueChangedEvent.bind(this));
            }
            else {
                // PhantomJS does not support bind
                propertySet.onPropertyAdded((event) => {
                    this.forwardPropertyAddedEvent(event);
                });
                propertySet.onPropertyRemoved((event) => {
                    this.forwardPropertyRemovedEvent(event);
                });
                propertySet.onPropertyIndexChanged((event) => {
                    this.forwardPropertyIndexChangedEvent(event);
                });
                propertySet.onPropertyValueChanged((event) => {
                    this.forwardPropertyValueChangedEvent(event);
                });
            }
        }

        /**
         * Package protected. Not to be used outside module.
         */
        unregisterPropertySetListeners(propertySet: PropertySet) {
            propertySet.unPropertyAdded(this.forwardPropertyAddedEvent);
            propertySet.unPropertyRemoved(this.forwardPropertyRemovedEvent);
            propertySet.unPropertyIndexChanged(this.forwardPropertyIndexChangedEvent);
            propertySet.unPropertyValueChanged(this.forwardPropertyValueChangedEvent);
        }

        onPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.propertyAddedListeners.push(listener);
        }

        unPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.propertyAddedListeners =
            this.propertyAddedListeners.filter((curr) => (curr != listener));
        }

        private notifyPropertyAdded(property: Property) {
            var event = new PropertyAddedEvent(property);
            if (this.debug) {
                console.debug("PropertyArray[" + this.getParentPropertyPath().toString() + "." + this.getName() +
                              "].notifyPropertyAdded: " +
                              event.getPath().toString());
            }
            this.propertyAddedListeners.forEach((listener) => listener(event));
        }

        private forwardPropertyAddedEvent(event: PropertyAddedEvent) {
            if (this.debug) {
                console.debug("PropertyArray[" + this.getParentPropertyPath().toString() + "." + this.getName() +
                              "].forwardPropertyAddedEvent: " +
                              event.getPath().toString());
            }
            this.propertyAddedListeners.forEach((listener) => listener(event));
        }

        onPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.propertyRemovedListeners.push(listener);
        }

        unPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.propertyRemovedListeners =
            this.propertyRemovedListeners.filter((curr) => (curr != listener));
        }

        private notifyPropertyRemoved(property: Property) {
            var event = new PropertyRemovedEvent(property);
            if (this.debug) {
                console.debug("PropertyArray[" + this.getParentPropertyPath().toString() + "." + this.getName() +
                              "].notifyPropertyRemoved: " +
                              event.getPath().toString());
            }
            this.propertyRemovedListeners.forEach((listener) => listener(event));
        }

        private forwardPropertyRemovedEvent(event: PropertyRemovedEvent) {
            if (this.debug) {
                console.debug("PropertyArray[" + this.getParentPropertyPath().toString() + "." + this.getName() +
                              "].forwardPropertyRemovedEvent: " +
                              event.getPath().toString());
            }
            this.propertyRemovedListeners.forEach((listener) => listener(event));
        }

        onPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.propertyIndexChangedListeners.push(listener);
        }

        unPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.propertyIndexChangedListeners =
            this.propertyIndexChangedListeners.filter((curr) => (curr != listener));
        }

        private forwardPropertyIndexChangedEvent(event: PropertyIndexChangedEvent) {
            if (this.debug) {
                console.debug("PropertyArray[" + this.getParentPropertyPath().toString() + "." + this.getName() +
                              "].forwardPropertyIndexChangedEvent: " +
                              event.getPath().toString());
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

        private forwardPropertyValueChangedEvent(event: PropertyValueChangedEvent) {
            if (this.debug) {
                console.debug("PropertyArray[" + this.getParentPropertyPath().toString() + "." + this.getName() +
                              "].forwardPropertyValueChangedEvent: " +
                              event.getPath().toString());
            }
            this.propertyValueChangedListeners.forEach((listener) => listener(event));
        }

        public static create(): PropertyArrayBuilder {
            return new PropertyArrayBuilder();
        }

        public toJson(): PropertyArrayJson {

            var valuesJson: ValueAndPropertyIdJson[] = [];
            this.array.forEach((property: Property) => {
                if (this.type.equals(ValueTypes.DATA)) {
                    var valueSetJson = property.hasNullValue() ? null : property.getSet().toJson();
                    valuesJson.push(<ValueAndPropertyIdJson>{
                        id: property.getId().toString(),
                        set: valueSetJson
                    });
                }
                else {
                    var valueJson = this.type.toJsonValue(property.getValue());
                    valuesJson.push(<ValueAndPropertyIdJson>{
                        id: property.getId().toString(),
                        v: valueJson
                    });
                }

            });
            return <PropertyArrayJson>{
                name: this.name,
                type: this.type.toString(),
                values: valuesJson
            };
        }

        public static fromJson(json: PropertyArrayJson, parentPropertySet: PropertySet, tree: PropertyTree): PropertyArray {

            var type = ValueTypes.fromName(json.type);

            var array = new PropertyArrayBuilder().
                setName(json.name).
                setType(type).
                setParent(parentPropertySet).
                build();

            json.values.forEach((valueAndPropertyIdJson: ValueAndPropertyIdJson, index: number) => {

                var value;

                if (type.equals(ValueTypes.DATA)) {
                    var valueAsPropertySet = tree.newSet();
                    var propertyArrayJsonArray = valueAndPropertyIdJson.set;
                    propertyArrayJsonArray.forEach((propertyArrayJson: PropertyArrayJson) => {

                        valueAsPropertySet.addPropertyArray(PropertyArray.fromJson(propertyArrayJson, valueAsPropertySet, tree))
                    });

                    value = new Value(valueAsPropertySet, ValueTypes.DATA);
                }
                else {
                    value = type.fromJsonValue(valueAndPropertyIdJson.v);
                }

                var property = Property.create().
                    setId(new PropertyId(valueAndPropertyIdJson.id)).
                    setArray(array).
                    setName(json.name).
                    setIndex(index).
                    setValue(value).
                    build();
                array.addProperty(property);
            });
            return array;
        }
    }

    export class PropertyArrayBuilder {

        parent: PropertySet;

        name: string;

        type: ValueType;

        setParent(value: PropertySet): PropertyArrayBuilder {
            this.parent = value;
            return this;
        }

        setName(value: string): PropertyArrayBuilder {
            this.name = value;
            return this;
        }

        setType(value: ValueType): PropertyArrayBuilder {
            this.type = value;
            return this;
        }

        build(): PropertyArray {
            return new PropertyArray(this);
        }
    }
}