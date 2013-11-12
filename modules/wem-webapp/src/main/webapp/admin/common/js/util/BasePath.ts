module api_util {

    export class BasePath<PATH> {

        private static DEFAULT_ELEMENT_DIVIDER:string = "/";

        private elementDivider:string;

        private absolute:boolean;

        private elements:string[];

        private refString:string;

        constructor(elements:string[], elementDivider?:string, absolute?:boolean) {

            this.elementDivider = elementDivider != null ? elementDivider : BasePath.DEFAULT_ELEMENT_DIVIDER;

            this.absolute = absolute == undefined ? true : absolute;
            elements.forEach((element:string, index:number) => {
                if (element == null) {
                    throw new Error("Path element was null at index: " + index);
                }
                else if (element.length == 0) {
                    throw new Error("Path element was empty string at index: " + index);
                }
            });
            this.elements = elements;

            this.refString = (this.absolute ? this.elementDivider : "") + this.elements.join(this.elementDivider);
        }

        isAbsolute():boolean {
            return this.absolute;
        }

        getElements():string[] {
            return this.elements;
        }

        getElement(index:number):string {
            return this.elements[index];
        }

        hasParent():boolean {
            return this.elements.length > 0;
        }

        getParentPath():PATH {

            if (this.elements.length < 1) {
                return null;
            }
            var parentElemements:string[] = [];
            this.elements.forEach((element:string, index:number)=> {
                if (index < this.elements.length - 1) {
                    parentElemements.push(element);
                }
            });
            return this.newInstance(parentElemements, this.absolute);
        }

        newInstance(elements:string[], absolute:boolean):PATH {
            throw new Error("Must be implemented by inheritor");
        }

        toString() {
            return this.refString;
        }

        public static removeEmptyElements(elements:string[]):string[] {
            var filteredElements:string[] = [];
            elements.forEach((element:string) => {
                if (element.length > 0) {
                    filteredElements.push(element);
                }
            });
            return filteredElements;
        }
    }
}
