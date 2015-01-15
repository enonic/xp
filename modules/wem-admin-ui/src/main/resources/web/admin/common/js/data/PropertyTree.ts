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

    export class PropertyTree implements api.Equitable {

        private idProvider: PropertyIdProvider;

        private root: PropertySet;

        private propertyById: {[s:string] : Property;} = {};

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

        public equals(o: any): boolean {

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

        onChanged(listener: {(event: PropertyEvent): void;}) {
            this.root.onChanged(listener);
        }

        unChanged(listener: {(event: PropertyEvent): void;}) {
            this.root.unChanged(listener);
        }

        onPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.root.onPropertyAdded(listener);
        }

        unPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.root.unPropertyAdded(listener);
        }

        onPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.root.onPropertyRemoved(listener);
        }

        unPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.root.unPropertyRemoved(listener);
        }

        onPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.root.onPropertyIndexChanged(listener);
        }

        unPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.root.unPropertyIndexChanged(listener);
        }

        onPropertyValueChanged(listener: {(event: PropertyValueChangedEvent): void;}) {
            this.root.onPropertyValueChanged(listener);
        }

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