module api.data {

    import Reference = api.util.Reference;
    import BinaryReference = api.util.BinaryReference;
    import GeoPoint = api.util.GeoPoint;
    import LocalTime = api.util.LocalTime;

    /**
     * A PropertySet manages a set of properties. The properties are grouped in arrays by name ([[Property.name]]).
     *
     * The PropertySet provides several functions for both creation, updating and getting property values of a certain type (see [[ValueTypes]]).
     * Instead of repeating the documentation for each type, here is an overview of the functions which exists for each [[ValueType]]
     * (replace Xxx with one of the value types).
     *
     * * addXxx(name, value) : Property
     * > Creates a new property with the given name and value, and adds it to this PropertySet.
     * Returns the added property.
     *
     * * addXxxs(name: string, values:Xxx[]) : Property[]
     * > Creates new properties with the given name and values, and adds them to this PropertySet.
     * Returns an array of the added properties.
     *
     * * setXxx(name: string, value: Xxx, index: number) : Property
     * > On the root PropertySet: In this PropertySet; creates a new property with given name, index and value or updates existing with given value.
     * Returns the created or updated property.
     *
     * * setXxxByPath(path: any, value: Xxx) : Property
     * > Creates a new property at given path (relative to this PropertySet) with given value or updates existing with given value. path can either be a string or [[PropertyPath]].
     * Returns the created or updated property.
     *
     * * getXxx(identifier: string, index: number): Xxx
     * > Gets a property value of type Xxx with given identifier and optional index. If index is given, then the identifier is understood
     *  as the name of the property and it will be retrieved from this PropertySet. If the index is omitted the identifier is understood
     *  as a relative path (to this PropertySet) of the property.
     *
     * * getXxxs(name: string): Xxx[]
     * > Gets property values of type Xxx with the given name. Returns an array of type Xxx.
     *
     *
     * @see [[PropertyArray]]
     * @see [[Property]]
     */
    export class PropertySet implements api.Equitable {

        public static debug: boolean = false;

        private tree: PropertyTree = null;

        /**
         * The property that this PropertySet is the value of.
         * Required to be set, except for the root PropertySet of a PropertyTree where it will always be null.
         */
        private property: Property = null;

        private propertyArrayByName: {[s:string] : PropertyArray;} = {};

        /**
         * If true, do not add property if it's value is null.
         */
        private _ifNotNull: boolean = false;

        private changedListeners: {(event: PropertyEvent):void}[] = [];

        private propertyAddedListeners: {(event: PropertyAddedEvent):void}[] = [];

        private propertyRemovedListeners: {(event: PropertyRemovedEvent):void}[] = [];

        private propertyIndexChangedListeners: {(event: PropertyIndexChangedEvent):void}[] = [];

        private propertyValueChangedListeners: {(event: PropertyValueChangedEvent):void}[] = [];

        private propertyAddedEventHandler: (event: PropertyAddedEvent) => void;

        private propertyRemovedEventHandler: (event: PropertyRemovedEvent) => void;

        private propertyIndexChangedEventHandler: (event: PropertyIndexChangedEvent) => void;

        private propertyValueChangedEventHandler: (event: PropertyValueChangedEvent) => void;

        constructor(tree?: PropertyTree) {
            this.tree = tree;

            this.propertyAddedEventHandler = (event) => {
                this.forwardPropertyAddedEvent(event)
            };
            this.propertyRemovedEventHandler = (event) => {
                this.forwardPropertyRemovedEvent(event)
            };
            this.propertyIndexChangedEventHandler = (event) => {
                this.forwardPropertyIndexChangedEvent(event)
            };
            this.propertyValueChangedEventHandler = (event) => {
                this.forwardPropertyValueChangedEvent(event)
            };
        }

        /**
         * Application protected. Not to be used outside module.
         */
        setContainerProperty(value: Property) {
            this.property = value;
        }

        /**
         * Whether this PropertySet is attached to a [[PropertyTree]] or not.
         * @returns {boolean} true if it's not attached to a [[PropertyTree]].
         */
        isDetached(): boolean {
            return !this.tree;
        }

        getTree(): PropertyTree {
            return this.tree;
        }

        /**
         * Application protected. Not to be used outside module.
         */
        attachToTree(tree: PropertyTree) {
            this.tree = tree;

            this.forEach((property: Property) => {
                if (property.hasNonNullValue() && property.getType().equals(ValueTypes.DATA)) {
                    property.getPropertySet().attachToTree(tree);
                }
            });
        }

        addPropertyArray(array: PropertyArray) {
            api.util.assertState(this.tree === array.getTree(),
                "Added PropertyArray must be attached to the same PropertyTree as this PropertySet");
            api.util.assert(this == array.getParent(), "propertyArray must have this PropertySet as parent");
            this.propertyArrayByName[array.getName()] = array;

            this.registerPropertyArrayListeners(array);
        }

        addProperty(name: string, value: Value): Property {

            if (this._ifNotNull && value.isNull()) {
                this._ifNotNull = false;
                return null;
            }

            var array = this.getOrCreatePropertyArray(name, value.getType());
            var property = array.add(value);
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
                var newSet = this.tree ? new PropertySet(this.tree) : new PropertySet();
                this.setProperty(name, index, new Value(newSet, ValueTypes.DATA));
                return newSet;
            }
            else {
                return existingProperty.getPropertySet();
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
                this.registerPropertyArrayListeners(array);
            }
            return array;
        }

        removeProperties(properties: Property[]) {
            properties.forEach((property) => {
                this.removeProperty(property.getName(), property.getIndex());
            });
        }

        removeProperty(name: string, index: number) {
            var array: PropertyArray = this.propertyArrayByName[name];
            if (array) {
                array.remove(index);
            }
            if (!array || array.isEmpty()) {
                delete this.propertyArrayByName[name];
            }
        }

        isEmpty(): boolean {
            var isEmpty: boolean = true;
            for (var name in this.propertyArrayByName) {
                if (!isEmpty) {
                    return isEmpty;
                }
                if (this.propertyArrayByName.hasOwnProperty(name)) {
                    var propertyArray: PropertyArray = this.propertyArrayByName[name];
                    propertyArray.forEach((property: Property) => {
                        if (!isEmpty) {
                            return;
                        }
                        var type = property.getType();
                        if (property.hasNullValue()) {
                            return;
                        }
                        if (type.equals(api.data.ValueTypes.STRING) && property.getValue().getString() === '') {
                            return;
                        }
                        if (type.equals(api.data.ValueTypes.BOOLEAN) && property.getValue().getBoolean() === false) {
                            return;
                        }
                        if (type.equals(api.data.ValueTypes.DATA) && !property.getValue().getPropertySet().isEmpty()) {
                            return;
                        }
                        isEmpty = false;
                    });
                }
            }
            return isEmpty;
        }

        removeEmptyValues() {
            this.doRemoveEmptyValues(this);
        }

        private doRemoveEmptyValues(propertySet: api.data.PropertySet) {
            var toRemove = [];
            propertySet.forEach((property) => {
                var type = property.getType();
                if (property.hasNullValue()) {
                    toRemove.push(property);
                }
                else if (type.equals(api.data.ValueTypes.STRING) && (property.getValue().getString() === '')) {
                    toRemove.push(property);
                }
                else if (type.equals(api.data.ValueTypes.DATA)) {
                    var propertySetValue = property.getValue().getPropertySet();
                    this.doRemoveEmptyValues(propertySetValue);
                    if (propertySetValue.isEmpty()) {
                        toRemove.push(property);
                    }
                } else if (type.equals(api.data.ValueTypes.BOOLEAN) && (property.getValue().getBoolean() == false)) {
                    toRemove.push(property);
                }
            });
            this.removeProperties(toRemove);
            this.removeEmptyArrays();
        }

        private removeEmptyArrays() {
            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, propertyArray: PropertyArray) => {
                if (propertyArray.isEmpty()) {
                    delete this.propertyArrayByName[name];
                }
            });
        }

        /**
         * Returns the number of child properties in this PropertySet (grand children and so on is not counted).
         */
        getSize(): number {
            var size = 0;
            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, propertyArray: PropertyArray) => {
                size += propertyArray.getSize();
            });

            return size;
        }

        /**
         * Counts the number of child properties having the given name (grand children and so on is not counted).
         */
        countProperties(name: string): number {
            var array = this.propertyArrayByName[name];
            if (!array) {
                return 0;
            }
            return array.getSize();
        }

        /**
         * @returns {PropertyPath} The [[PropertyPath]] that this PropertySet is a value of.
         */
        getPropertyPath(): PropertyPath {
            return !this.property ? PropertyPath.ROOT : this.property.getPath();
        }

        /**
         * * getProperty() - If no arguments are given then this PropertySet's Property is returned.
         * * getProperty(name: string, index: number) - If name and index are given then property with that name and index is returned.
         * * getProperty(path: string) - If a path as string is given then property with that path is returned.
         * * getProperty(path: PropertyPath ) - If a path as [[PropertyPath]] is given then property with that path is returned.
         *
         * @param identifier
         * @param index
         * @returns {Property}
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
                var propertySet = property.getPropertySet();
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

        public isNotNull(identifier: any, index?: number): boolean {
            var property = this.getProperty(identifier, index);
            if (property == null) {
                return false;
            }

            return !property.hasNullValue();
        }

        public isNull(identifier: any, index?: number): boolean {
            return !this.isNotNull(identifier, index);
        }

        public equals(o: Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PropertySet)) {
                return false;
            }

            var other = <PropertySet>o;

            if (!api.ObjectHelper.mapEquals(this.propertyArrayByName, other.propertyArrayByName)) {
                return false;
            }

            return true;
        }

        /**
         * Copies this PropertySet (deep copy).
         * @param destinationTree The [[PropertyTree]] that the copied PropertySet will be attached to.
         * @returns {api.data.PropertySet}
         */
        copy(destinationTree: PropertyTree): PropertySet {

            var copy = new PropertySet(destinationTree);

            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, sourcePropertyArray: PropertyArray) => {
                var propertyArrayCopy = sourcePropertyArray.copy(copy);
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

        private registerPropertyArrayListeners(array: PropertyArray) {
            if (PropertySet.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].registerPropertyArrayListeners: " + array.getName())
            }

            array.onPropertyAdded(this.propertyAddedEventHandler);
            array.onPropertyRemoved(this.propertyRemovedEventHandler);
            array.onPropertyIndexChanged(this.propertyIndexChangedEventHandler);
            array.onPropertyValueChanged(this.propertyValueChangedEventHandler);
        }

        // Currently not used, because we do not remove arrays
        private unregisterPropertyArrayListeners(array: PropertyArray) {
            array.unPropertyAdded(this.propertyAddedEventHandler);
            array.unPropertyRemoved(this.propertyRemovedEventHandler);
            array.unPropertyIndexChanged(this.propertyIndexChangedEventHandler);
            array.unPropertyValueChanged(this.propertyValueChangedEventHandler);
        }

        onChanged(listener: {(event: PropertyEvent): void;}) {
            this.changedListeners.push(listener);
        }

        unChanged(listener: {(event: PropertyEvent): void;}) {
            this.changedListeners = this.changedListeners.filter((curr) => (curr != listener));
        }

        private notifyChangedListeners(event: PropertyEvent) {
            if (PropertySet.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].notifyChangedListeners: " +
                              event.toString());
            }
            this.changedListeners.forEach((listener) => listener(event));
        }

        /**
         * Register a listener-function to be called when a [[Property]] has been added to this PropertySet or any below.
         * @param listener
         * @see [[PropertyAddedEvent]]
         */
        onPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.propertyAddedListeners.push(listener);
        }

        /**
         * Deregister a listener-function.
         * @param listener
         * @see [[PropertyAddedEvent]]
         */
        unPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.propertyAddedListeners = this.propertyAddedListeners.filter((curr) => (curr != listener));
        }

        private forwardPropertyAddedEvent(event: PropertyAddedEvent) {
            this.propertyAddedListeners.forEach((listener) => listener(event));
            if (PropertySet.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].forwardPropertyAddedEvent: " +
                              event.toString());
            }
            this.notifyChangedListeners(event);
        }

        /**
         * Register a listener-function to be called when a [[Property]] has been removed from this PropertySet or any below.
         * @param listener
         * @see [[PropertyRemovedEvent]]
         */
        onPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.propertyRemovedListeners.push(listener);
        }

        /**
         * Deregister a listener-function.
         * @param listener
         * @see [[PropertyRemovedEvent]]
         */
        unPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.propertyRemovedListeners = this.propertyRemovedListeners.filter((curr) => (curr != listener));
        }

        private forwardPropertyRemovedEvent(event: PropertyRemovedEvent) {
            if (PropertySet.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].forwardPropertyRemovedEvent: " +
                              event.toString());
            }
            this.propertyRemovedListeners.forEach((listener) => listener(event));
            this.notifyChangedListeners(event);
        }

        /**
         * Register a listener-function to be called when the [[Property.index]] in this this PropertySet or any below has changed.
         * @param listener
         * @see [[PropertyRemovedEvent]]
         */
        onPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.propertyIndexChangedListeners.push(listener);
        }

        /**
         * Deregister a listener-function.
         * @param listener
         * @see [[PropertyIndexChangedEvent]]
         */
        unPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.propertyIndexChangedListeners = this.propertyIndexChangedListeners.filter((curr) => (curr != listener));
        }

        private forwardPropertyIndexChangedEvent(event: PropertyIndexChangedEvent) {
            if (PropertySet.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].forwardPropertyIndexChangedEvent: " +
                              event.toString());
            }
            this.propertyIndexChangedListeners.forEach((listener) => listener(event));
            this.notifyChangedListeners(event);
        }

        /**
         * Register a listener-function to be called when the [[Property.value]] in this this PropertySet or any below has changed.
         * @param listener
         * @see [[PropertyValueChangedEvent]]
         */
        onPropertyValueChanged(listener: {(event: PropertyValueChangedEvent): void;}) {
            this.propertyValueChangedListeners.push(listener);
        }

        /**
         * Deregister a listener-function.
         * @param listener
         * @see [[PropertyValueChangedEvent]]
         */
        unPropertyValueChanged(listener: {(event: PropertyValueChangedEvent): void;}) {
            this.propertyValueChangedListeners = this.propertyValueChangedListeners.filter((curr) => (curr != listener));
        }

        private forwardPropertyValueChangedEvent(event: PropertyValueChangedEvent) {
            if (PropertySet.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].forwardPropertyValueChangedEvent: " +
                              event.toString());
            }
            this.propertyValueChangedListeners.forEach((listener) => listener(event));
            this.notifyChangedListeners(event);
        }

        // PropertySet methods

        /**
         * Creates a new PropertySet attached to the same [[PropertyTree]] as this PropertySet.
         * The PropertySet is not added to the tree.
         * @returns {PropertySet}
         */
        newSet(): PropertySet {
            if (!this.tree) {
                throw new Error("The PropertySet must be attached to a PropertyTree before this method can be invoked. Use PropertySet constructor with no arguments instead.");
            }
            return this.tree.newPropertySet();
        }

        /**
         * Creates
         * @param name
         * @param value optional
         * @returns {PropertySet}
         */
        addPropertySet(name: string, value?: PropertySet): PropertySet {
            if (!value) {
                if (!this.tree) {
                    throw new Error("The PropertySet must be attached to a PropertyTree before this method can be invoked. Use PropertySet constructor with no arguments instead.");
                }
                value = this.tree.newPropertySet();
            }
            this.addProperty(name, new Value(value, ValueTypes.DATA));
            return value;
        }

        setPropertySet(name: string, index: number, value: PropertySet): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.DATA));
        }

        setPropertySetByPath(path: any, value: PropertySet): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.DATA))
        }

        getPropertySet(identifier: any, index?: number): PropertySet {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getPropertySet();
        }

        getPropertySets(name: string): PropertySet[] {
            var values: PropertySet[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getPropertySet());
            });
            return values;
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

        getStrings(name: string): string[] {
            var values: string[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getString());
            });
            return values;
        }

        // long methods

        addLong(name: string, value: number): Property {
            return this.addProperty(name, new Value(value, ValueTypes.LONG));
        }

        addLongs(name: string, values: number[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: number) => {
                properties.push(this.addLong(name, value));
            });
            return properties;
        }

        setLong(name: string, index: number, value: number): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.LONG));
        }

        setLongByPath(path: any, value: number): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.LONG));
        }

        getLong(identifier: string, index?: number): number {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getLong();
        }

        getLongs(name: string): number[] {
            var values: number[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getLong());
            });
            return values;
        }

        // double methods

        addDouble(name: string, value: number): Property {
            return this.addProperty(name, new Value(value, ValueTypes.DOUBLE));
        }

        addDoubles(name: string, values: number[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: number) => {
                properties.push(this.addDouble(name, value));
            });
            return properties;
        }

        setDouble(name: string, index: number, value: number): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.DOUBLE));
        }

        setDoubleByPath(path: any, value: number): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.DOUBLE));
        }

        getDouble(identifier: string, index?: number): number {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getDouble();
        }

        getDoubles(name: string): number[] {
            var values: number[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getDouble());
            });
            return values;
        }

        // boolean methods

        addBoolean(name: string, value: boolean): Property {
            return this.addProperty(name, new Value(value, ValueTypes.BOOLEAN));
        }

        addBooleans(name: string, values: boolean[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: boolean) => {
                properties.push(this.addBoolean(name, value));
            });
            return properties;
        }

        setBoolean(name: string, index: number, value: boolean): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.BOOLEAN));
        }

        setBooleanByPath(path: any, value: boolean): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.BOOLEAN));
        }

        getBoolean(identifier: string, index?: number): boolean {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getBoolean();
        }

        getBooleans(name: string): boolean[] {
            var values: boolean[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getBoolean());
            });
            return values;
        }

        // reference methods

        addReference(name: string, value: Reference): Property {
            return this.addProperty(name, new Value(value, ValueTypes.REFERENCE));
        }

        addReferences(name: string, values: Reference[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: Reference) => {
                properties.push(this.addReference(name, value));
            });
            return properties;
        }

        setReference(name: string, index: number, value: Reference): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.REFERENCE));
        }

        setReferenceByPath(path: any, value: Reference): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.REFERENCE));
        }

        getReference(identifier: string, index?: number): Reference {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getReference();
        }

        getReferences(name: string): Reference[] {
            var values: Reference[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getReference());
            });
            return values;
        }

        // binary reference methods

        addBinaryReference(name: string, value: BinaryReference): Property {
            return this.addProperty(name, new Value(value, ValueTypes.BINARY_REFERENCE));
        }

        addBinaryReferences(name: string, values: BinaryReference[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: BinaryReference) => {
                properties.push(this.addBinaryReference(name, value));
            });
            return properties;
        }

        setBinaryReference(name: string, index: number, value: BinaryReference): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.BINARY_REFERENCE));
        }

        setBinaryReferenceByPath(path: any, value: BinaryReference): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.BINARY_REFERENCE));
        }

        getBinaryReference(identifier: string, index?: number): BinaryReference {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getBinaryReference();
        }

        getBinaryReferences(name: string): BinaryReference[] {
            var values: BinaryReference[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getBinaryReference());
            });
            return values;
        }

        // geo point methods

        addGeoPoint(name: string, value: GeoPoint): Property {
            return this.addProperty(name, new Value(value, ValueTypes.GEO_POINT));
        }

        addGeoPoints(name: string, values: GeoPoint[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: GeoPoint) => {
                properties.push(this.addGeoPoint(name, value));
            });
            return properties;
        }

        setGeoPoint(name: string, index: number, value: GeoPoint): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.GEO_POINT));
        }

        setGeoPointByPath(path: any, value: GeoPoint): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.GEO_POINT));
        }

        getGeoPoint(identifier: string, index?: number): GeoPoint {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getGeoPoint();
        }

        getGeoPoints(name: string): GeoPoint[] {
            var values: GeoPoint[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getGeoPoint());
            });
            return values;
        }

        // local date methods

        addLocalDate(name: string, value: api.util.LocalDate): Property {
            return this.addProperty(name, new Value(value, ValueTypes.LOCAL_DATE));
        }

        addLocalDates(name: string, values: api.util.LocalDate[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: api.util.LocalDate) => {
                properties.push(this.addLocalDate(name, value));
            });
            return properties;
        }

        setLocalDate(name: string, index: number, value: api.util.LocalDate): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.LOCAL_DATE));
        }

        setLocalDateByPath(path: any, value: api.util.LocalDate): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.LOCAL_DATE));
        }

        getLocalDate(identifier: string, index?: number): api.util.LocalDate {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getLocalDate();
        }

        getLocalDates(name: string): api.util.LocalDate[] {
            var values: api.util.LocalDate[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getLocalDate());
            });
            return values;
        }

        // local date time methods

        addLocalDateTime(name: string, value: api.util.LocalDateTime): Property {
            return this.addProperty(name, new Value(value, ValueTypes.LOCAL_DATE_TIME));
        }

        addLocalDateTimes(name: string, values: api.util.LocalDateTime[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: api.util.LocalDateTime) => {
                properties.push(this.addLocalDateTime(name, value));
            });
            return properties;
        }

        setLocalDateTime(name: string, index: number, value: api.util.LocalDateTime): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.LOCAL_DATE_TIME));
        }

        setLocalDateTimeByPath(path: any, value: api.util.LocalDateTime): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.LOCAL_DATE_TIME));
        }

        getLocalDateTime(identifier: string, index?: number): api.util.LocalDateTime {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getLocalDateTime();
        }

        getLocalDateTimes(name: string): api.util.LocalDateTime[] {
            var values: api.util.LocalDateTime[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getLocalDateTime());
            });
            return values;
        }

        // local time methods

        addLocalTime(name: string, value: LocalTime): Property {
            return this.addProperty(name, new Value(value, ValueTypes.LOCAL_TIME));
        }

        addLocalTimes(name: string, values: LocalTime[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: LocalTime) => {
                properties.push(this.addLocalTime(name, value));
            });
            return properties;
        }

        setLocalTime(name: string, index: number, value: LocalTime): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.LOCAL_TIME));
        }

        setLocalTimeByPath(path: any, value: LocalTime): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.LOCAL_TIME));
        }

        getLocalTime(identifier: string, index?: number): LocalTime {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getLocalTime();
        }

        getLocalTimes(name: string): LocalTime[] {
            var values: LocalTime[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getLocalTime());
            });
            return values;
        }

        // date time methods

        addDateTime(name: string, value: api.util.DateTime): Property {
            return this.addProperty(name, new Value(value, ValueTypes.DATE_TIME));
        }

        addDateTimes(name: string, values: api.util.DateTime[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: api.util.DateTime) => {
                properties.push(this.addDateTime(name, value));
            });
            return properties;
        }

        setDateTime(name: string, index: number, value: api.util.DateTime): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.DATE_TIME));
        }

        setDateTimeByPath(path: any, value: api.util.DateTime): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.DATE_TIME));
        }

        getDateTime(identifier: string, index?: number): api.util.DateTime {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getDateTime();
        }

        getDateTimes(name: string): api.util.DateTime[] {
            var values: api.util.DateTime[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getDateTime());
            });
            return values;
        }

        // TODO: Add methods for each type
    }
}