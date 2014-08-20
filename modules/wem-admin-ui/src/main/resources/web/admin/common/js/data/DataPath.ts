module api.data {

    export class DataPath {

        private static ELEMENT_DIVIDER: string = ".";

        public static ROOT: DataPath = new DataPath([], true);

        private absolute: boolean;

        private elements: DataPathElement[];

        private refString: string;

        static fromString(s: string) {
            var absolute: boolean = s.charAt(0) == DataPath.ELEMENT_DIVIDER;
            var dataPathElements = s.split(DataPath.ELEMENT_DIVIDER).
                filter((element: string) => !!element).                         // filter empty elements
                map((element: string) => DataPathElement.fromString(element));  // map string to DataPathElement
            return new DataPath(dataPathElements, absolute);
        }

        static fromParent(parent: DataPath, ...childElements: DataPathElement[]) {

            var elements: DataPathElement[] = parent.elements.slice(0).concat(childElements);
            return new DataPath(elements, parent.isAbsolute());
        }

        static fromPathElement(element: DataPathElement) {

            return new DataPath([element], true);
        }

        constructor(elements: DataPathElement[], absolute: boolean = true) {

            this.absolute = absolute;
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
            return new DataPath(this.elements.slice(1));
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
            return new DataPath(this.elements.slice(0,-1));
        }

        toString() {
            return this.refString;
        }

        isAbsolute(): boolean {
            return this.absolute;
        }

        asRelative(): DataPath {
            return new DataPath(this.elements, false);
        }

        isRoot(): boolean {
            return this.elementCount() == 0;
        }

        asNewWithoutFirstPathElement(): DataPath {
            api.util.assert(this.elementCount() > 1,
                "Cannot create new path without first path element when path does not contain more than one element");
            var elements: DataPathElement[] = this.elements.slice(1).
                map((element: DataPathElement) => new DataPathElement(element.getName(), element.getIndex()));
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