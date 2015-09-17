module api.data {

    export class PropertyVisitor {

        private valueType: ValueType;

        public restrictType(value: ValueType): PropertyVisitor {
            this.valueType = value;
            return this;
        }

        public traverse(propertySet: PropertySet) {

            propertySet.forEach((property: Property, index: number) => {

                if (this.valueType == null || this.valueType == property.getType()) {
                    this.visit(property);
                }

                if (property.getType().equals(ValueTypes.DATA)) {
                    if (property.hasNonNullValue()) {
                        this.traverse(property.getPropertySet());
                    }
                }
            });
        }

        public visit(property: Property) {
            throw new Error("Must be implemented by inheritor");
        }
    }
}
