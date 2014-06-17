module api.content.page.layout {

    import RegionDescriptor = api.content.page.region.RegionDescriptor;
    import Region = api.content.page.region.Region;
    import RegionBuilder = api.content.page.region.RegionBuilder;

    export class LayoutRegionsMerger {

        private layoutComponentRegions: Region[];
        private targetRegionsByName: { [regionName: string]: Region; };
        private targetRegionsNameByPosition: { [key: number]: string; };
        private sourceRegionsPositionByName: { [regionName: string]: number; };

        constructor() {
        }

        /**
         * Merge the components of regions existing in a layout component, distribute them in the regions of the target layout descriptor according to following rules:
         *  - If a region with the same name exists on the target layout: move components from source into region with the same name
         *  - If a region with the same name does not exists, but exists a region with the same position (index) on target: move components to target region with the same position
         *  - If a region with the same name or position (index) cannot be found on target: move components to the last region of target
         *
         */
        merge(layoutRegions: LayoutRegions, layoutDescriptorRegions: api.content.page.region.RegionDescriptor[],
              parent: LayoutComponent): LayoutRegions {

            this.layoutComponentRegions = layoutRegions.getRegions();
            this.initLookupTables(layoutDescriptorRegions, parent);

            this.mergeExistingRegions();

            this.mergeMissingRegions(layoutDescriptorRegions);

            // return merged regions in the same order as they were in target layoutDescriptor
            var layoutRegionsBuilder = new api.content.page.layout.LayoutRegionsBuilder();
            layoutDescriptorRegions.forEach((regionDescriptor: RegionDescriptor) => {
                var layoutRegion = this.targetRegionsByName[regionDescriptor.getName()];
                layoutRegionsBuilder.addRegion(layoutRegion);
            });
            return layoutRegionsBuilder.build();
        }

        /**
         * Merge components from regions that already exist in target layout descriptor
         */
        private mergeExistingRegions() {
            this.layoutComponentRegions.forEach((region: Region) => {
                var regionName = region.getName();
                if (this.targetRegionsByName[regionName]) {
                    var targetRegion = this.targetRegionsByName[regionName];
                    var updatedRegion: Region = this.addComponents(region, targetRegion);
                    this.targetRegionsByName[regionName] = updatedRegion;
                }
            });
        }

        /**
         * Merge components from regions that are missing in target layout descriptor
         */
        private mergeMissingRegions(layoutDescriptorRegions: api.content.page.region.RegionDescriptor[]) {
            var lastRegionName = layoutDescriptorRegions[layoutDescriptorRegions.length - 1].getName();

            this.layoutComponentRegions.forEach((region: Region) => {
                var regionName = region.getName();
                if (!this.targetRegionsByName[regionName]) {
                    var sourceRegionPos: number = this.sourceRegionsPositionByName[regionName];
                    // insert region components in region with the same position in target, or in last region if there are less regions in target
                    var targetRegionName = this.targetRegionsNameByPosition[sourceRegionPos] || lastRegionName;
                    var targetRegion: Region = this.targetRegionsByName[targetRegionName];

                    var updatedRegion: Region = this.addComponents(region, targetRegion);
                    this.targetRegionsByName[targetRegionName] = updatedRegion;
                }
            });
        }

        private initLookupTables(layoutDescriptorRegions: api.content.page.region.RegionDescriptor[], parent: LayoutComponent): void {
            this.targetRegionsByName = {};
            this.targetRegionsNameByPosition = {};
            this.sourceRegionsPositionByName = {};

            layoutDescriptorRegions.forEach((regionDescriptor: RegionDescriptor, idx: number) => {
                var regionName = regionDescriptor.getName();
                var layoutRegion = new api.content.page.region.RegionBuilder().
                    setName(regionName).
                    setParent(parent).
                    build();
                this.targetRegionsByName[regionName] = layoutRegion;
                this.targetRegionsNameByPosition[idx] = regionName;
            });
            this.layoutComponentRegions.forEach((region: Region, idx: number) => {
                this.sourceRegionsPositionByName[region.getName()] = idx;
            });
        }

        private addComponents(fromRegion: Region, toRegion: Region): Region {
            if (fromRegion.getComponents().length === 0) {
                return toRegion;
            }

            var result: RegionBuilder = new RegionBuilder(toRegion);
            fromRegion.getComponents().forEach((component: PageComponent) => {
                result.addComponent(component)
            });
            return result.build();
        }

    }

}