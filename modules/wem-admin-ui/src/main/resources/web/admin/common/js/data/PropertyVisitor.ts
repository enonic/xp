module api.data {

    import ValueType = api.data.type.ValueType;

    export class PropertyVisitor {

        private valueType: ValueType;

        public restrictType(value: ValueType): PropertyVisitor {
            this.valueType = value;
            return this;
        }

        public traverse(datas: Data[]) {

            datas.forEach((data: Data) => {
                if (api.ObjectHelper.iFrameSafeInstanceOf(data, Property)) {
                    var property = <Property>data;
                    if (this.valueType == null || this.valueType == property.getValue().getType()) {
                        this.visit(property);
                    }
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(data, DataSet)) {
                    var dataSet = <DataSet>data;
                    this.traverse(dataSet.getDataArray());
                }
            });
        }

        public visit(property: Property) {
            throw new Error("Must be implemented by inheritor");
        }
    }
}
