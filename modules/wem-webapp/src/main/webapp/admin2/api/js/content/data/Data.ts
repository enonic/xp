module API.content.data{

    export class Data {

        private name:string;

        private arrayIndex:number;

        constructor(name:string) {
            this.name = name;
        }

        setArrayIndex(value:number) {
            this.arrayIndex = value;
        }

        getName():string {
            return this.name;
        }

        getArrayIndex():number {
            return this.arrayIndex;
        }
    }

}