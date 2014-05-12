module api.content.page {

    export class ComponentPath2 implements api.Equitable {

        private static DIVIDER = "/";

        private static COMPONENT_INDEX = "component";

        private regionAndComponentList: ComponentPathRegionAndComponent2[];

        private refString: string;

        constructor(regionAndComponentList: ComponentPathRegionAndComponent2[]) {

            this.regionAndComponentList = regionAndComponentList;

            this.refString = "";
            this.regionAndComponentList.forEach((regionAndComponent: ComponentPathRegionAndComponent2, index: number) => {
                this.refString += regionAndComponent.toString();
                if (index < this.regionAndComponentList.length - 1) {
                    this.refString += ComponentPath2.DIVIDER;
                }
            });
        }

        numberOfLevels(): number {
            return this.regionAndComponentList.length;
        }

        getFirstLevel(): ComponentPathRegionAndComponent2 {
            return this.regionAndComponentList[0];
        }

        getLastLevel(): ComponentPathRegionAndComponent2 {
            return this.regionAndComponentList[this.regionAndComponentList.length - 1];
        }

        getLevels(): ComponentPathRegionAndComponent2 [] {
            return this.regionAndComponentList;
        }

        getComponentIndex(): number {
            return this.getLastLevel().getComponentIndex();
        }

        getRegionPath(): RegionPath {

            var regionPathAsString = "";
            this.regionAndComponentList.forEach((regionAndComponent: ComponentPathRegionAndComponent2, index: number) => {

                if (index == this.regionAndComponentList.length - 1) {
                    regionPathAsString += regionAndComponent.getRegionName();
                }
                else {
                    regionPathAsString += regionAndComponent.toString();
                    regionPathAsString += "/";
                }

            });

            return RegionPath.fromString(regionPathAsString);
        }

        public removeFirstLevel(): ComponentPath2 {
            if (this.numberOfLevels() <= 1) {
                return null;
            }

            var newRegionAndComponentList: ComponentPathRegionAndComponent2[] = [];
            for (var i = 1; i < this.regionAndComponentList.length; i++) {
                newRegionAndComponentList.push(this.regionAndComponentList[i]);
            }
            return new ComponentPath2(newRegionAndComponentList);
        }

        public toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ComponentPath2)) {
                return false;
            }

            var other = <ComponentPath2>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }

        public static fromString(str: string): ComponentPath2 {

            if (!str) {
                return null;
            }

            var elements: string[] = str.split(ComponentPath2.DIVIDER);
            elements = api.util.removeEmptyStringElements(elements);

            var regionAndComponentList: ComponentPathRegionAndComponent2[] = [];
            for (var i = 0; i < elements.length - 1; i += 2) {
                var regionName = elements[i];
                var componentIndexExpr = elements[i + 1];
                var componentIndex:number = parseInt( api.util.substringBetween( componentIndexExpr, ComponentPath2.COMPONENT_INDEX + "[", "]" ) );
                var regionAndComponent = new ComponentPathRegionAndComponent2(regionName, componentIndex);
                regionAndComponentList.push(regionAndComponent);
            }

            return new ComponentPath2(regionAndComponentList);
        }

        public static fromRegionPathAndComponentIndex(regionPath: RegionPath2, componentIndex: number): ComponentPath2 {

            var componentPathAsString = regionPath.toString() + "/" + componentIndex;
            return ComponentPath2.fromString(componentPathAsString);
        }
    }

    export class ComponentPathRegionAndComponent2 {

        private static DIVIDER = "/";

        private static COMPONENT_INDEX = "component";

        private regionName: string;

        private componentIndex: number;

        private refString: string;

        constructor(regionName: string, componentIndex: number) {
            this.regionName = regionName;
            this.componentIndex = componentIndex;
            this.refString = regionName + ComponentPathRegionAndComponent2.DIVIDER +
                             ComponentPathRegionAndComponent2.COMPONENT_INDEX + "[" + this.componentIndex + "]";
        }

        getRegionName(): string {
            return this.regionName;
        }

        getComponentIndex(): number {
            return this.componentIndex;
        }

        toString(): string {
            return this.refString;
        }
    }
}