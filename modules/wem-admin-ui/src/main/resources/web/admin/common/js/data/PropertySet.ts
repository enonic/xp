module api.data {

    export class PropertySet implements api.Equitable {

        private tree: PropertyTree = null;

        private property: Property = null;

        private propertyArrayByName: {[s:string] : PropertyArray;} = {};

        private _ifNotNull: boolean = false;

        constructor(tree: PropertyTree) {
            this.tree = tree;
        }

        setContainerProperty(value: Property) {
            this.property = value;
        }

        getTree(): PropertyTree {
            return this.tree;
        }

        /**
         * If invoked, then the next added or set property with a null will be ignored.
         * The second add or set after this method is called will not be affected.
         */
        public ifNotNull(): PropertySet {
            this._ifNotNull = true;
            return this;
        }

        addPropertyArray(propertyArray: PropertyArray) {
            api.util.assertState(this.tree === propertyArray.getTree(),
                "Added PropertyArray must be attached to the same PropertyTree as this PropertySet");
            api.util.assert(this == propertyArray.getParent(), "propertyArray must have this PropertySet as parent");
            this.propertyArrayByName[propertyArray.getName()] = propertyArray;
        }

        addProperty(name: string, value: Value): Property {

            if (this._ifNotNull && value.isNull()) {
                this._ifNotNull = false;
                return null;
            }

            var array = this.getOrCreatePropertyArray(name, value.getType());
            var property = array.add(value);
            this.tree.registerProperty(property);
            return property;
        }

        setPropertyByPath(path: any, value: Value): Property {
            if (api.ObjectHelper.iFrameSafeInstanceOf(path, PropertyPath)) {
                return this.doSetProperty(<PropertyPath>path, value)
            }
            else {
                return this.doSetProperty(PropertyPath.fromString(path.toString()), value);
            }
        }

        private doSetProperty(path: PropertyPath, value: Value): Property {
            var firstPathElement = path.getFirstElement();
            if (path.elementCount() > 1) {
                var propertySet = this.getOrCreateSet(firstPathElement.getName(), firstPathElement.getIndex());
                return propertySet.setPropertyByPath(path.removeFirstPathElement(), value);
            }
            else {
                return this.setProperty(firstPathElement.getName(), firstPathElement.getIndex(), value);
            }
        }

        private getOrCreateSet(name: string, index: number): PropertySet {
            var existingProperty = this.getProperty(name, index);
            if (!existingProperty) {
                var newSet = new PropertySet(this.tree);
                this.setProperty(name, index, new Value(newSet, ValueTypes.DATA));
                return newSet;
            }
            else {
                return existingProperty.getSet();
            }
        }

        setProperty(name: string, index: number, value: Value): Property {

            var array = this.getOrCreatePropertyArray(name, value.getType());
            return array.set(index, value);
        }

        private getOrCreatePropertyArray(name: string, type: ValueType): PropertyArray {

            var array = this.propertyArrayByName[name];
            if (!array) {
                array = PropertyArray.create().
                    setParent(this).
                    setName(name).
                    setType(type).
                    build();
                this.propertyArrayByName[name] = array;
            }
            return array;
        }

        removeProperty(name: string, index: number) {
            var array: PropertyArray = this.propertyArrayByName[name];
            if (array) {
                var property = array.get(index);
                if (!property) {
                    throw new Error("Property not found: " +
                                    PropertyPath.fromParent(this.property.getPath(), new PropertyPathElement(name, index)));
                }
                this.tree.unregisterProperty(property.getId());
                array.remove(index);
            }
        }


        getSize(): number {
            var size = 0;
            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, propertyArray: PropertyArray) => {
                size += propertyArray.getSize();
            });

            return size;
        }

        countProperties(name: string): number {
            var array = this.propertyArrayByName[name];
            if (!array) {
                return 0;
            }
            return array.getSize();
        }

        getPropertyPath(): PropertyPath {
            return !this.property ? PropertyPath.ROOT : this.property.getPath();
        }

        /**
         * If no arguments are given then this PropertySet's Property is returned.
         * If name and index are given then property with that name and index is returned.
         * @param identifier
         * @param index
         * @returns {*}
         */
        getProperty(identifier?: any, index?: number): Property {

            if (identifier == undefined && index == undefined) {
                return this.property;
            }
            else if (index != undefined) {
                Property.checkName(identifier);
                var array = this.propertyArrayByName[identifier];
                if (!array) {
                    return null;
                }
                return array.get(index);
            }
            else {
                return this.getPropertyByPath(identifier);
            }
        }

        private getPropertyByPath(path: any): Property {

            if (api.ObjectHelper.iFrameSafeInstanceOf(path, PropertyPath)) {
                return this.doGetPropertyByPath(<PropertyPath>path);
            }
            else {
                return this.doGetPropertyByPath(PropertyPath.fromString(path.toString()));
            }
        }

        private doGetPropertyByPath(path: PropertyPath): Property {

            var firstElement = path.getFirstElement();
            if (path.elementCount() > 1) {
                var property = this.getProperty(firstElement.getName(), firstElement.getIndex());
                if (!property) {
                    return null;
                }
                var propertySet = property.getSet();
                return propertySet.getPropertyByPath(path.removeFirstPathElement());
            }
            else {
                return this.getProperty(firstElement.getName(), firstElement.getIndex());
            }
        }

        getPropertyArray(name: string): PropertyArray {
            return this.propertyArrayByName[name];
        }

        /**
         * Calls the given callback for each property in the set.
         */
        forEach(callback: (property: Property, index?: number) => void) {
            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, propertyArray: PropertyArray) => {
                propertyArray.forEach((property: Property, index: number) => {
                    callback(property, index);
                });
            });
        }

        /**
         * Calls the given callback for each property with the given name.
         */
        forEachProperty(propertyName: string, callback: (property: Property, index?: number) => void) {
            var array = this.getPropertyArray(propertyName);
            if (array) {
                array.forEach(callback);
            }
        }

        public equals(o: any): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PropertySet)) {
                return false;
            }

            var other = <PropertySet>o;

            if (!api.ObjectHelper.mapEquals(this.propertyArrayByName, other.propertyArrayByName)) {
                return false;
            }

            return true;
        }

        toTree(): PropertyTree {
            return new PropertyTree(this.tree.getIdProvider(), this);
        }

        copy(destinationTree: PropertyTree, generateNewPropertyIds: boolean = false): PropertySet {

            var copy = new PropertySet(destinationTree);

            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, propertyArray: PropertyArray) => {
                var propertyArrayCopy = propertyArray.copy(copy, generateNewPropertyIds);
                copy.addPropertyArray(propertyArrayCopy);

            });

            return copy;
        }

        toJson(): PropertyArrayJson[] {
            var jsonArray: PropertyArrayJson[] = [];

            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, propertyArray: PropertyArray) => {
                jsonArray.push(propertyArray.toJson());
            });

            return jsonArray;
        }

        // PropertySet methods

        newSet(): PropertySet {
            return this.tree.newSet();
        }

        addSet(name: string, value?: PropertySet): PropertySet {
            if (!value) {
                value = this.tree.newSet();
            }
            this.addProperty(name, new Value(value, ValueTypes.DATA));
            return value;
        }

        getSet(identifier: any, index?: number): PropertySet {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getSet();
        }


        // string methods

        addString(name: string, value: string): Property {
            return this.addProperty(name, new Value(value, ValueTypes.STRING));
        }

        addStrings(name: string, values: string[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: string) => {
                properties.push(this.addString(name, value));
            });
            return properties;
        }

        setString(name: string, index: number, value: string): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.STRING));
        }

        setStringByPath(path: any, value: string): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.STRING))
        }

        getString(identifier: string, index?: number): string {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getString();
        }
    }
}