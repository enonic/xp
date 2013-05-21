module API_content_data {
    class DataId {
        private name;
        private arrayIndex;
        private refString;
        constructor(name: string, arrayIndex: number);
        public getName(): string;
        public getArrayIndex(): number;
        public toString(): string;
        static from(str: string): DataId;
    }
}
module API_content_data {
    class Data {
        private name;
        private arrayIndex;
        private parent;
        constructor(name: string);
        public setArrayIndex(value: number): void;
        public setParent(parent: DataSet): void;
        public getName(): string;
        public getParent(): Data;
        public getArrayIndex(): number;
    }
}
module API_content_data {
    class Property extends Data {
        private value;
        private type;
        static from(json): Property;
        constructor(name: string, value: string, type: string);
        public getValue(): string;
        public getType(): string;
    }
}
module API_content_data {
    class DataSet extends Data {
        private dataById;
        constructor(name: string);
        public dataCount(name: string): number;
        public addData(data: Data): void;
        public getData(dataId: string): Data;
    }
}
