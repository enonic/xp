module api_data{

    export class DataPath {

        private static DEFAULT_ELEMENT_DIVIDER:string = ".";

        private elementDivider:string;

        private absolute:boolean;

        private elements:DataPathElement[];

        private refString:string;

        static fromString(s:string, elementDivider?:string) {
            if (elementDivider == null) {
                elementDivider = DataPath.DEFAULT_ELEMENT_DIVIDER;
            }
            var absolute:boolean = s.charAt(0) == elementDivider;
            var elements:string[] = s.split(elementDivider);
            elements = DataPath.removeEmptyElements(elements);
            var dataPathElements:DataPathElement[] = [];
            elements.forEach((s:string) => {
                dataPathElements.push(DataPathElement.fromString(s));
            });
            return new DataPath(dataPathElements, elementDivider, absolute);
        }

        static fromParent(parent:DataPath, ...childElements:DataPathElement[]) {

            var elements:DataPathElement[] = parent.elements.slice(0);
            childElements.forEach((element:DataPathElement) => {
                elements.push(element);
            });

            return new DataPath(elements, parent.elementDivider, parent.isAbsolute());
        }

        private static removeEmptyElements(elements:string[]):string[] {
            var filteredElements:string[] = [];
            elements.forEach((element:string) => {
                if (element.length > 0) {
                    filteredElements.push(element);
                }
            });
            return filteredElements;
        }

        constructor(elements:DataPathElement[], elementDivider?:string, absolute?:boolean) {
            this.elementDivider = elementDivider != null ? elementDivider : DataPath.DEFAULT_ELEMENT_DIVIDER;
            this.absolute = absolute == undefined ? true : absolute;
            elements.forEach((element:DataPathElement, index:number) => {
                if (element == null) {
                    throw new Error("Path element was null at index: " + index);
                }
                else if (element.getName().length == 0) {
                    throw new Error("Path element was empty string at index: " + index);
                }
            });
            this.elements = elements;
            this.refString = (this.absolute ? this.elementDivider : "") + this.elements.join(this.elementDivider);
        }

        newWithoutFirstElement():DataPath {
            //console.log("splicing array", this.getElements().splice(0, 1));
            var arr = this.elements;
            arr.shift();
            return new DataPath(arr);
        }

        elementCount():number {
            return this.getElements().length;
        }

        getElements():DataPathElement[] {
            return this.elements;
        }

        getElement(index:number):DataPathElement {
            return this.elements[index];
        }

        getFirstElement():DataPathElement {
            return this.elements[0];
        }

        getLastElement():DataPathElement {
            return this.elements[this.elements.length-1];
        }

        hasParent():boolean {
            return this.elements.length > 0;
        }

        getParentPath():DataPath {

            if (this.elements.length < 1) {
                return null;
            }
            var parentElemements:DataPathElement[] = [];
            this.elements.forEach((element:DataPathElement, index:number)=> {
                if (index < this.elements.length - 1) {
                    parentElemements.push(element);
                }
            });
            return new DataPath(parentElemements);
        }

        toString() {
            return this.refString;
        }

        isAbsolute():boolean {
            return this.absolute;
        }
    }

    export class DataPathElement {

        private name:string;

        private index:number;

        constructor(name:string, index:number) {
            this.name = name;
            this.index = index;
        }

        getName():string {
            return this.name
        }

        getIndex():number {
            return this.index;
        }

        toDataId():api_data.DataId {
            return new api_data.DataId( this.name, this.index );
        }

        toString():string {
            return this.name + "[" + this.index + "]";
        }

        static fromString(str:string) {
            if (str.indexOf("[") == -1) {
                return new DataPathElement(str, 0);
            }
            var name = str.substring(0, str.indexOf("["));
            var index = parseInt(str.substring(str.indexOf("[") + 1, str.indexOf("]")));
            return new DataPathElement(name, index);
        }
    }

}