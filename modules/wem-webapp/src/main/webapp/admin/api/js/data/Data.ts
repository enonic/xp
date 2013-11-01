module api_data{

    export class Data {

        private name:string;

        private arrayIndex:number;

        private parent:DataSet;

        constructor(name:string) {
            this.name = name;
        }

        setArrayIndex(value:number) {
            this.arrayIndex = value;
        }

        public setParent(parent:DataSet) {
            this.parent = parent;
        }

        getId():DataId {
            return new DataId(this.name, this.arrayIndex);
        }

        getName():string {
            return this.name;
        }

        getParent():Data {
            return this.parent;
        }

        getArrayIndex():number {
            return this.arrayIndex;
        }

        toDataJson():api_data_json.DataJson {

            if (this instanceof Property) {
                return (<Property>this).toPropertyJson();
            }
            else if (this instanceof DataSet) {
                return (<DataSet>this).toDataSetJson();
            }
            else {
                throw new Error("Unsupported data: " + this);
            }
        }

        equals(data:Data):boolean {
            return this.name == data.getName() && this.arrayIndex == data.getArrayIndex();
        }
    }

}