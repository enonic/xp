module api.data {

    export class PropertyTreeComparator {

        public compareTree(treeA: PropertyTree, treeB: PropertyTree) {
            if (!treeA || !treeB) {
                return;
            }
            this.compareSet(treeA.getRoot(), treeB.getRoot());
        }

        public compareSet(setA: PropertySet, setB: PropertySet) {

            // Check those existing in A, but not in B
            setA.forEach((propertyA: Property, index: number) => {

                var propertyB = setB.getProperty(propertyA.getName(), index);
                if (!propertyB) {
                    console.log("Property A[" + propertyA.getPath().toString() + "] does not exist in B[" +
                                setB.getPropertyPath().toString() + "]");
                }

                if (!propertyA.equals(propertyB)) {
                    console.log("Property A[" + propertyA.getPath().toString() + "] is not equal B[" + propertyB.getPath().toString() +
                                "]");
                }

                if (propertyA.getValue().isPropertySet()) {
                    var childSetA = propertyA.getSet();
                    var childSetB = propertyB.getSet();
                    this.compareSet(childSetA, childSetB);
                }

            });

            // Check those existing in B, but not in A
            setB.forEach((propertyB: Property, index: number) => {

                var propertyA = setA.getProperty(propertyB.getName(), index);
                if (!propertyA) {
                    console.log("Property B[" + propertyB.getPath().toString() + "] does not exist in A[" +
                                setA.getPropertyPath().toString() + "]");
                }
            });


        }
    }
}
