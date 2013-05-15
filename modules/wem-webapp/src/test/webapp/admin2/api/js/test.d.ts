module API.content.data {
    class Data {
        private name;
        private arrayIndex;
        constructor(name: string);
        public setArrayIndex(value: number): void;
        public getName(): string;
        public getArrayIndex(): number;
    }
}
module API.content.data {
    class Property extends Data {
        private value;
        private type;
        static from(json): Property;
        constructor(name: string, value: string, type: string);
        public getValue(): string;
        public getType(): string;
    }
}
module API.content.data {
    class DataSet extends Data {
        private dataById;
        constructor(json);
        public addData(data: Data): void;
        public getData(dataId: string): Data;
    }
}
