module api.form {

    export class ValidationRecordingPath {

        private parentDataSet: api.data.DataPath;

        private dataName: string;

        private refString;

        constructor(parentDataSet: api.data.DataPath, dataName: string) {
            this.parentDataSet = parentDataSet != null ? parentDataSet.asRelative() : null;
            this.dataName = dataName;
            this.refString = this.resolveRefString();
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

        getParentDataSet(): api.data.DataPath {
            return this.parentDataSet;
        }

        getDataName(): string {
            return this.dataName;
        }

        toString(): string {
            return this.refString;
        }


    }
}
