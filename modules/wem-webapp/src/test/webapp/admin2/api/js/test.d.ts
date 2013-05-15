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
        constructor(json);
        public getValue(): string;
        public getType(): string;
    }
}
