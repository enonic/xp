module api.content.page {

    export class RegionPath {

        private static DIVIDER = "/";

        private parentComponentPath: ComponentPath;

        private regionName: string;

        private refString: string;

        constructor(parentComponentPath: ComponentPath, regionName: string) {

            this.parentComponentPath = parentComponentPath;
            this.regionName = regionName;
            if (parentComponentPath != null) {
                this.refString = parentComponentPath + "/" + regionName;
            }
            else {
                this.refString = regionName;
            }
        }

        public hasParentComponentPath(): boolean {

            return this.parentComponentPath != null;
        }

        public getParentComponentPath(): ComponentPath {
            return this.parentComponentPath;
        }

        public getRegionName(): string {
            return this.regionName;
        }

        public toString(): string {
            return this.refString;
        }

        public static fromString(str: string): RegionPath {

            var lastDivider = str.lastIndexOf(RegionPath.DIVIDER);
            if (lastDivider == -1) {
                return new RegionPath(null, str);
            }

            var regionNameStart = lastDivider + 1;

            var regionName = str.substring(regionNameStart, str.length);
            var coponentPathAsString = str.substring(0, regionNameStart);
            var parentPath = ComponentPath.fromString(coponentPathAsString);
            return new RegionPath(parentPath, regionName);
        }
    }
}