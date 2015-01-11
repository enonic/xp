module api.data {

    export class PropertyArray implements api.Equitable {

        private tree: PropertyTree;

        private parent: PropertySet;

        private name: string;

        private type: ValueType;

        private array: Property[];

        constructor(builder: PropertyArrayBuilder) {
            this.tree = builder.parent.getTree();
            this.parent = builder.parent;
            this.name = builder.name;
            this.type = builder.type;
            this.array = builder.array;
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

        add(value: Value): Property {
            this.checkType(value.getType());

            var property = Property.create().
                setParent(this.parent).
                setName(this.name).
                setIndex(this.array.length).
                setValue(value).
                setId(this.tree.getNextId()).
                build();

            this.array.push(property);
            if (this.tree) {
                this.tree.registerProperty(property);
            }
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
                    setParent(this.parent).
                    setName(this.name).
                    setIndex(this.array.length).
                    setValue(value).
                    setId(this.tree.getNextId()).
                    build();
                this.array[index] = property;

                if (this.tree) {
                    this.tree.registerProperty(property);
                }
            }
            return property;
        }

        move(index: number, destinationIndex: number) {
            var toBeMoved = this.array[index];
            toBeMoved.setIndex(destinationIndex);
            api.util.ArrayHelper.moveElement(index, destinationIndex, this.array);
        }

        remove(index: number) {
            this.array.splice(index, 1);
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

            return false;
        }

        copy(destinationPropertySet: PropertySet, generateNewPropertyIds: boolean = false): PropertyArray {

            var builder = PropertyArray.create().
                setName(this.name).
                setType(this.type).
                setParent(destinationPropertySet);

            this.array.forEach((property: Property) => {
                builder.add(property.copy(destinationPropertySet, generateNewPropertyIds));
            });

            return builder.build();
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
                        v: valueJson,
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

            var builder = new PropertyArrayBuilder();
            builder.setName(json.name);
            builder.setType(type);
            builder.setParent(parentPropertySet);
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
                    setParent(parentPropertySet).
                    setName(json.name).
                    setIndex(index).
                    setValue(value).
                    build();
                builder.add(property);
            });
            return builder.build();
        }
    }

    export class PropertyArrayBuilder {

        parent: PropertySet;

        name: string;

        type: ValueType;

        array: Property[];

        constructor() {
            this.array = [];
        }

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

        add(value: Property): PropertyArrayBuilder {
            this.array.push(value);
            return this;
        }

        build(): PropertyArray {
            return new PropertyArray(this);
        }
    }
}