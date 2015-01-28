module api.data {

    class PropertyRegistrar extends PropertyVisitor {

        private tree: PropertyTree;

        constructor(tree: PropertyTree) {
            this.tree = tree;
            super();
        }

        visit(property: Property) {
            this.tree.registerProperty(property);
        }
    }

    /**
     * The PropertyTree is the root container of properties. 
     * It has a root [[PropertySet]] and [[PropertyIdProvider]] and keeps a reference to all properties 
     * in the tree in a map using the [[PropertyId]] as key. All properties in a tree must have unique ids within the tree.
     * 
     * The PropertyTree is mutable and most mutations can be observed by listening to the following events:
     * * [[PropertyAddedEvent]]
     * * [[PropertyRemovedEvent]]
     * * [[PropertyIndexChangedEvent]]
     * * [[PropertyValueChangedEvent]]
     *
     * @see [[Property]]
     * @see [[PropertyArray]]
     * @see [[PropertySet]]
     */
    export class PropertyTree implements api.Equitable {

        private idProvider: PropertyIdProvider;

        private root: PropertySet;

        private propertyById: {[s:string] : Property;} = {};

        /**
         * * To create new PropertyTree:
         * ** give no arguments or optionally with a idProvider.
         * * To create a copy of another tree: 
         * ** give the root [[PropertySet]] of the tree to copy from
         * ** and optionally a idProvider and generateNewPropertyIds
         *  
         * @param idProvider optional. If not given, a [[DefaultPropertyIdProvider]] will be created.
         * @param sourceRoot optional. If given this tree will be a copy of the given [[PropertySet]].
         * @param generateNewPropertyIds optional. Used only when sourceRoot is given. Default value is false.
         */
        constructor(idProvider?: PropertyIdProvider, sourceRoot?: PropertySet, generateNewPropertyIds?: boolean) {

            if (sourceRoot) {
                this.idProvider = idProvider;
                this.root = sourceRoot.copy(this, generateNewPropertyIds);
                // Ensure to register all properties from sourceRoot
                var propertyRegistrar = new PropertyRegistrar(this);
                propertyRegistrar.traverse(this.root);
            }
            else {
                if (!idProvider) {
                    this.idProvider = new DefaultPropertyIdProvider();
                }
                else {
                    this.idProvider = idProvider;
                }
                this.root = new PropertySet(this);
            }
        }

        getIdProvider(): PropertyIdProvider {
            return this.idProvider;
        }

        getNextId(): PropertyId {
            return this.idProvider.getNextId();
        }

        public getRoot(): PropertySet {
            return this.root;
        }

        /**
         * Not to be used outside module. 
         * 
         * An Error is thrown if a Property with same id as the given is already registered.
         * @param property the property to register
         */
        registerProperty(property: Property) {
            api.util.assertNotNull(property.getId(), "Cannot register a Property without id");
            var existing = this.propertyById[property.getId().toString()];
            if (existing) {
                throw new Error("Property with id [" + property.getId().toString() + "] already registered: " +
                                property.getPath().toString());
            }
            this.propertyById[property.getId().toString()] = property;
        }

        /**
         * Not to be used outside module.
         */
        unregisterProperty(id: PropertyId) {
            delete this.propertyById[id.toString()];
        }

        public getTotalSize(): number {
            var size = 0, key;
            for (key in this.propertyById) {
                if (this.propertyById.hasOwnProperty(key)) {
                    size++;
                }
            }
            return size;
        }

        addProperty(name: string, value: Value): Property {
            return this.root.addProperty(name, value);
        }

        setPropertyByPath(path: any, value: Value): Property {
            return this.root.setPropertyByPath(path, value);
        }

        setProperty(name: string, index: number, value: Value): Property {
            return this.root.setProperty(name, index, value);
        }

        removeProperty(name: string, index: number) {
            this.root.removeProperty(name, index);
        }

        getPropertyById(id: PropertyId): Property {
            return this.propertyById[id.toString()];
        }

        /**
         * If no arguments are given then this PropertySet's Property is returned.
         * If name and index are given then property with that name and index is returned.
         * @param identifier
         * @param index
         * @returns {*}
         */
        getProperty(identifier?: any, index?: number): Property {
            return this.root.getProperty(identifier, index);
        }

        getPropertyArray(name: string): PropertyArray {
            return this.root.getPropertyArray(name);
        }

        forEachProperty(name: string, callback: (property: Property, index?: number) => void) {

            this.root.forEachProperty(name, callback);
        }

        public equals(o: Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PropertyTree)) {
                return false;
            }

            var other = <PropertyTree>o;

            if (!api.ObjectHelper.equals(this.root, other.root)) {
                return false;
            }

            return true;
        }

        copy(generateNewPropertyIds: boolean = false): PropertyTree {
            return new PropertyTree(this.getIdProvider(), this.getRoot(), generateNewPropertyIds);
        }

        toJson(): PropertyArrayJson[] {

            return this.getRoot().toJson();
        }

        /**
         * Register a listener-function to be called when any [[PropertyEvent]] has been fired anywhere in the tree.
         * @param listener
         * @see [[PropertyEvent]]
         */
        onChanged(listener: {(event: PropertyEvent): void;}) {
            this.root.onChanged(listener);
        }

        /**
         * Deregister a listener-function.
         * @param listener
         * @see [[PropertyEvent]]
         */
        unChanged(listener: {(event: PropertyEvent): void;}) {
            this.root.unChanged(listener);
        }

        /**
         * Register a listener-function to be called when a [[Property]] has been added anywhere in the tree.
         * @param listener
         * @see [[PropertyAddedEvent]]
         */
        onPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.root.onPropertyAdded(listener);
        }

        /**
         * Deregister a listener-function.
         * @param listener
         * @see [[PropertyAddedEvent]]
         */
        unPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.root.unPropertyAdded(listener);
        }

        /**
         * Register a listener-function to be called when a [[Property]] has been removed anywhere in the tree.
         * @param listener
         * @see [[PropertyRemovedEvent]]
         */
        onPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.root.onPropertyRemoved(listener);
        }

        /**
         * Deregister a listener-function.
         * @param listener
         * @see [[PropertyRemovedEvent]]
         */
        unPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.root.unPropertyRemoved(listener);
        }

        /**
         * Register a listener-function to be called when a [[Property.index]] has changed anywhere in the tree.
         * @param listener
         * @see [[PropertyIndexChangedEvent]]
         */
        onPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.root.onPropertyIndexChanged(listener);
        }

        /**
         * Deregister a listener-function.
         * @param listener
         * @see [[PropertyIndexChangedEvent]]
         */
        unPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.root.unPropertyIndexChanged(listener);
        }

        /**
         * Register a listener-function to be called when a [[Property.value]] has changed anywhere in the tree.
         * @param listener
         * @see [[PropertyValueChangedEvent]]
         */
        onPropertyValueChanged(listener: {(event: PropertyValueChangedEvent): void;}) {
            this.root.onPropertyValueChanged(listener);
        }

        /**
         * Deregister a listener-function.
         * @param listener
         * @see [[PropertyValueChangedEvent]]
         */
        unPropertyValueChanged(listener: {(event: PropertyValueChangedEvent): void;}) {
            this.root.unPropertyValueChanged(listener);
        }

        public static fromJson(json: PropertyArrayJson[], idProvider: PropertyIdProvider): PropertyTree {

            api.util.assertNotNull(json, "a json is required");
            var tree = new PropertyTree(idProvider);

            json.forEach((propertyArrayJson: PropertyArrayJson) => {
                tree.root.addPropertyArray(PropertyArray.fromJson(propertyArrayJson, tree.root, tree))
            });

            return tree;
        }

        // PropertySet methods

        newSet(): PropertySet {
            return new PropertySet(this);
        }

        addSet(name: string, value?: PropertySet): PropertySet {
            return this.root.addSet(name, value);
        }

        getSet(identifier: any, index?: number): PropertySet {
            return this.root.getSet(name, index);
        }

        // string methods

        addString(name: string, value: string): Property {
            return this.root.addString(name, value);
        }

        addStrings(name: string, values: string[]): Property[] {
            return this.root.addStrings(name, values);
        }

        setString(name: string, index: number, value: string): Property {
            return this.root.setString(name, index, value);
        }

        setStringByPath(path: any, value: string): Property {
            return this.root.setStringByPath(path, value);
        }

        getString(identifier: any, index?: number): string {
            return this.root.getString(identifier, index);
        }
    }
}