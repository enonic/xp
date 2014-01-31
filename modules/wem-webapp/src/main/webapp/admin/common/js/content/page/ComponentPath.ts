module api.content.page {

    export class ComponentPath {

        private static DIVIDER = "/";

        private regionAndComponentList: ComponentPathRegionAndComponent[];

        private refString: string;

        constructor(regionAndComponentList: ComponentPathRegionAndComponent[]) {

            this.regionAndComponentList = regionAndComponentList;

            this.refString = "";
            this.regionAndComponentList.forEach((regionAndComponent: ComponentPathRegionAndComponent, index: number) => {
                this.refString += regionAndComponent.toString();
                if (index < this.regionAndComponentList.length - 2) {
                    this.refString += ComponentPath.DIVIDER;
                }
            });
        }

        numberOfLevels(): number {
            return this.regionAndComponentList.length;
        }

        getFirstLevel(): ComponentPathRegionAndComponent {
            return this.regionAndComponentList[0];
        }

        getLastLevel(): ComponentPathRegionAndComponent {
            return this.regionAndComponentList[this.regionAndComponentList.length-1];
        }

        public removeFirstLevel(): ComponentPath {
            if (this.numberOfLevels() <= 1) {
                return null;
            }

            var newRegionAndComponentList: ComponentPathRegionAndComponent[];
            for (var i = 1; i < this.regionAndComponentList.length; i++) {
                newRegionAndComponentList.push(this.regionAndComponentList[i]);
            }
            return new ComponentPath(newRegionAndComponentList);
        }

        public toString(): string {
            return this.refString;
        }

        public static fromString(str: string): ComponentPath {

            var elements: string[] = str.split(ComponentPath.DIVIDER);
            elements = api.util.removeEmptyStringElements(elements);

            var regionAndComponentList: ComponentPathRegionAndComponent[] = [];
            for (var i = 0; i < elements.length - 1; i += 2) {
                var regionName = elements[i];
                var componentName = new ComponentName(elements[i + 1]);
                var regionAndComponent = new ComponentPathRegionAndComponent(regionName, componentName);
                regionAndComponentList.push(regionAndComponent);
            }

            return new ComponentPath(regionAndComponentList);
        }
    }

    export class ComponentPathRegionAndComponent {

        private static DIVIDER = "/";

        private regionName: string;

        private componentName: ComponentName;

        private refString: string;

        constructor(regionName: string, componentName: ComponentName) {
            this.regionName = regionName;
            this.componentName = componentName;
            this.refString = regionName + ComponentPathRegionAndComponent.DIVIDER + this.componentName.toString();
        }

        getRegionName(): string {
            return this.regionName;
        }

        getComponentName(): ComponentName {
            return this.componentName;
        }

        toString(): string {
            return this.refString;
        }
    }
}