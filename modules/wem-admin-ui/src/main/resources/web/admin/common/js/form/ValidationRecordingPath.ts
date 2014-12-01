module api.form {

    import PropertyPath = api.data.PropertyPath;

    export class ValidationRecordingPath {

        private parentDataSet: PropertyPath;

        private dataName: string;

        private refString;

        private min: number;

        private max: number;

        constructor(parentPropertySet: PropertyPath, dataName: string, min?: number, max?: number) {
            this.parentDataSet = parentPropertySet != null ? parentPropertySet.asRelative() : null;
            this.dataName = dataName;
            this.refString = this.resolveRefString();
            this.min = min;
            this.max = max;
        }

        private resolveRefString(): string {
            var s = "";
            if (this.parentDataSet && !this.parentDataSet.isRoot()) {
                s += this.parentDataSet.toString();
                if (this.dataName) {
                    s += "." + this.dataName
                }
            }
            else {
                if (this.dataName) {
                    s += this.dataName;
                }
            }

            return s;
        }

        getParentDataSet(): PropertyPath {
            return this.parentDataSet;
        }

        getDataName(): string {
            return this.dataName;
        }

        getMin(): number {
            return this.min;
        }

        getMax(): number {
            return this.max;
        }

        toString(): string {
            return this.refString;
        }


    }
}
