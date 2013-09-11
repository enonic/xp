module api_rest {

    export class Path {

        private static DEFAULT_ELEMENT_DIVIDER:string = "/";

        private elementDivider:string;

        private absolute:boolean;

        private elements:string[];

        private refString:string;

        public static fromString(s:string, elementDivider?:string) {
            if( elementDivider == null ){
                elementDivider = Path.DEFAULT_ELEMENT_DIVIDER;
            }
            var absolute:boolean = s.charAt( 0) == elementDivider;
            var elements:string[] = s.split(elementDivider);
            return new Path(elements, elementDivider, absolute);
        }

        public static fromParent(parent:Path, childElement:string) {

            var elements:string[] = parent.elements.slice(0);
            elements.push(childElement);
            return new Path(elements, parent.elementDivider);
        }

        constructor(elements:string[], elementDivider?:string, absolute?:boolean) {
            this.elementDivider = elementDivider != null ? elementDivider : Path.DEFAULT_ELEMENT_DIVIDER;
            this.absolute = absolute == undefined ? true : absolute;
            this.elements = elements;
            this.refString = this.elementDivider + this.elements.join(this.elementDivider);
        }

        getElements():string[] {
            return this.elements;
        }

        hasParent():boolean {
            return this.elements.length > 0;
        }

        getParentPath():Path {

            if (this.elements.length < 1) {
                return null;
            }
            var parentElemements:string[] = [];
            this.elements.forEach((element:string, index:number)=> {
                if (index < this.elements.length - 1) {
                    parentElemements.push(element);
                }
            });
            return new Path(parentElemements);
        }

        toString() {
            return this.refString;
        }
    }
}
