module api.data {

    export class DataPath {

        public static ROOT:DataPath = new DataPath([], true);

        private static ELEMENT_DIVIDER: string = ".";

        private absolute: boolean;

        private elements: DataPathElement[];

        private refString: string;

        static fromString(s: string) {
            var absolute: boolean = s.charAt(0) == DataPath.ELEMENT_DIVIDER;
            var elements: string[] = s.split(DataPath.ELEMENT_DIVIDER);
            elements = DataPath.removeEmptyElements(elements);
            var dataPathElements: DataPathElement[] = [];
            elements.forEach((s: string) => {
                dataPathElements.push(DataPathElement.fromString(s));
            });
            return new DataPath(dataPathElements, absolute);
        }

        static fromParent(parent: DataPath, ...childElements: DataPathElement[]) {

            var elements: DataPathElement[] = parent.elements.slice(0);
            childElements.forEach((element: DataPathElement) => {
                elements.push(element);
            });

            return new DataPath(elements, parent.isAbsolute());
        }

        static fromPathElement(element: DataPathElement) {

            return new DataPath([element], true);
        }

        private static removeEmptyElements(elements: string[]): string[] {
            var filteredElements: string[] = [];
            elements.forEach((element: string) => {
                if (element.length > 0) {
                    filteredElements.push(element);
                }
            });
            return filteredElements;
        }

        constructor(elements: DataPathElement[], absolute?: boolean) {

            this.absolute = absolute == undefined ? true : absolute;
            elements.forEach((element: DataPathElement, index: number) => {
                if (element == null) {
                    throw new Error("Path element was null at index: " + index);
                }
                else if (element.getName().length == 0) {
                    throw new Error("Path element was empty string at index: " + index);
                }
            });
            this.elements = elements;
            this.refString = (this.absolute ? DataPath.ELEMENT_DIVIDER : "") + this.elements.join(DataPath.ELEMENT_DIVIDER);
        }

        newWithoutFirstElement(): DataPath {
            var arr = this.elements;
            arr.shift();
            return new DataPath(arr);
        }

        elementCount(): number {
            return this.getElements().length;
        }

        getElements(): DataPathElement[] {
            return this.elements;
        }

        getElement(index: number): DataPathElement {
            return this.elements[index];
        }

        getFirstElement(): DataPathElement {
            return this.elements[0];
        }

        getLastElement(): DataPathElement {
            return this.elements[this.elements.length - 1];
        }

        hasParent(): boolean {
            return this.elements.length > 0;
        }

        getParentPath(): DataPath {

            if (this.elements.length < 1) {
                return null;
            }
            var parentElemements: DataPathElement[] = [];
            this.elements.forEach((element: DataPathElement, index: number)=> {
                if (index < this.elements.length - 1) {
                    parentElemements.push(element);
                }
            });
            return new DataPath(parentElemements);
        }

        toString() {
            return this.refString;
        }

        isAbsolute(): boolean {
            return this.absolute;
        }

        asRelative() : DataPath {
            return new DataPath(this.elements, false);
        }

        isRoot() : boolean {
            return this.elementCount() == 0;
        }

        asNewWithoutFirstPathElement(): DataPath {
            api.util.assert(this.elementCount() > 1,
                "Cannot create new path without first path element when path does not contain more than one element");
            var elements: DataPathElement[] = [];
            this.elements.forEach((element: DataPathElement, index: number) => {
                if (index > 0) {
                    elements.push(new DataPathElement(element.getName(), element.getIndex()));
                }
            });
            return new DataPath(elements, this.isAbsolute());
        }
    }

    export class DataPathElement {

        private name: string;

        private index: number;

        constructor(name: string, index: number) {
            this.name = name;
            this.index = index;
        }

        getName(): string {
            return this.name
        }

        getIndex(): number {
            return this.index;
        }

        toDataId(): api.data.DataId {
            return new api.data.DataId(this.name, this.index);
        }

        toString(): string {
            return this.name + "[" + this.index + "]";
        }

        static fromDataId(dataId: DataId) {
            return new DataPathElement(dataId.getName(), dataId.getArrayIndex());
        }
        static fromString(str: string) {
            if (str.indexOf("[") == -1) {
                return new DataPathElement(str, 0);
            }
            var name = str.substring(0, str.indexOf("["));
            var index = parseInt(str.substring(str.indexOf("[") + 1, str.indexOf("]")));
            return new DataPathElement(name, index);
        }
    }

}