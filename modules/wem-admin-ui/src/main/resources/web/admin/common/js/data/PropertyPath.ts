module api.data {

    export class PropertyPath implements api.Equitable {

        private static ELEMENT_DIVIDER: string = ".";

        public static ROOT: PropertyPath = new PropertyPath([], true);

        private absolute: boolean;

        private elements: PropertyPathElement[];

        private refString: string;

        static fromString(s: string) {
            var absolute: boolean = s.charAt(0) == PropertyPath.ELEMENT_DIVIDER;
            var dataPathElements = s.split(PropertyPath.ELEMENT_DIVIDER).
                filter((element: string) => !!element).                         // filter empty elements
                map((element: string) => PropertyPathElement.fromString(element));  // map string to DataPathElement
            return new PropertyPath(dataPathElements, absolute);
        }

        static fromParent(parent: PropertyPath, ...childElements: PropertyPathElement[]) {

            var elements: PropertyPathElement[] = parent.elements.slice(0).concat(childElements);
            return new PropertyPath(elements, parent.isAbsolute());
        }

        static fromPathElement(element: PropertyPathElement) {

            return new PropertyPath([element], true);
        }

        constructor(elements: PropertyPathElement[], absolute: boolean = true) {

            this.absolute = absolute;
            elements.forEach((element: PropertyPathElement, index: number) => {
                if (element == null) {
                    throw new Error("Path element was null at index: " + index);
                }
                else if (element.getName().length == 0) {
                    throw new Error("Path element was empty string at index: " + index);
                }
            });
            this.elements = elements;
            this.refString = (this.absolute ? PropertyPath.ELEMENT_DIVIDER : "") + this.elements.join(PropertyPath.ELEMENT_DIVIDER);
        }

        removeFirstPathElement(): PropertyPath {
            api.util.assert(this.elements.length > 1,
                "Cannot create new path without first path element when path does not contain more than one element");
            return new PropertyPath(this.elements.slice(1), this.absolute);
        }

        elementCount(): number {
            return this.getElements().length;
        }

        getElements(): PropertyPathElement[] {
            return this.elements;
        }

        getElement(index: number): PropertyPathElement {
            return this.elements[index];
        }

        getFirstElement(): PropertyPathElement {
            return this.elements[0];
        }

        getLastElement(): PropertyPathElement {
            return this.elements[this.elements.length - 1];
        }

        hasParent(): boolean {
            return this.elements.length > 0;
        }

        getParentPath(): PropertyPath {

            if (this.elements.length < 1) {
                return null;
            }
            return new PropertyPath(this.elements.slice(0, -1));
        }

        toString() {
            return this.refString;
        }

        isAbsolute(): boolean {
            return this.absolute;
        }

        asRelative(): PropertyPath {
            return new PropertyPath(this.elements, false);
        }

        isRoot(): boolean {
            return this.elementCount() == 0;
        }

        equals(o: PropertyPath): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PropertyPath)) {
                return false;
            }

            var other = <PropertyPath>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }
    }

    export class PropertyPathElement {

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

        toString(): string {
            if (this.index == 0) {
                return this.name;
            }
            else {
                return this.name + "[" + this.index + "]";
            }
        }

        static fromString(str: string) {
            if (str.indexOf("[") == -1) {
                return new PropertyPathElement(str, 0);
            }
            var name = str.substring(0, str.indexOf("["));
            var index = parseInt(str.substring(str.indexOf("[") + 1, str.indexOf("]")));
            return new PropertyPathElement(name, index);
        }
    }
}