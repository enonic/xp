module api.content.page.region {

    export class LayoutRegionsMerger {

        private layoutComponentRegions: Region[];
        private targetRegionsByName: { [regionName: string]: Region; };
        private targetRegionsNameByPosition: { [key: number]: string; };
        private sourceRegionsPositionByName: { [regionName: string]: number; };

        /**
         * Merge the components of regions existing in a layout component, distribute them
         * in the regions of the target layout descriptor according to following rules:
         *  - If a region with the same name exists on the target layout:
         *    move components from source into region with the same name
         *  - If a region with the same name does not exists, but exists a region
         *    with the same position (index) on target: move components to target region with the same position
         *  - If a region with the same name or position (index) cannot be found on target:
         *    move components to the last region of target
         *
         */
        merge(regions: Regions, layoutDescriptorRegions: RegionDescriptor[],
              parent: LayoutComponent): Regions {

            this.layoutComponentRegions = regions.getRegions();
            this.initLookupTables(layoutDescriptorRegions, parent);

            this.mergeExistingRegions();

            this.mergeMissingRegions(layoutDescriptorRegions);

            // return merged regions in the same order as they were in target layoutDescriptor
            let layoutRegionsBuilder = Regions.create();
            layoutDescriptorRegions.forEach((regionDescriptor: RegionDescriptor) => {
                let layoutRegion = this.targetRegionsByName[regionDescriptor.getName()];
                layoutRegionsBuilder.addRegion(layoutRegion);
            });
            return layoutRegionsBuilder.build();
        }

        /**
         * Merge components from regions that already exist in target layout descriptor
         */
        private mergeExistingRegions() {
            this.layoutComponentRegions.forEach((region: Region) => {
                let regionName = region.getName();
                if (this.targetRegionsByName[regionName]) {
                    let targetRegion = this.targetRegionsByName[regionName];
                    let updatedRegion: Region = this.addComponents(region, targetRegion);
                    this.targetRegionsByName[regionName] = updatedRegion;
                }
            });
        }

        /**
         * Merge components from regions that are missing in target layout descriptor
         */
        private mergeMissingRegions(layoutDescriptorRegions: RegionDescriptor[]) {
            let lastRegionName = layoutDescriptorRegions[layoutDescriptorRegions.length - 1].getName();

            this.layoutComponentRegions.forEach((region: Region) => {
                let regionName = region.getName();
                if (!this.targetRegionsByName[regionName]) {
                    let sourceRegionPos: number = this.sourceRegionsPositionByName[regionName];
                    // insert region components in region with the same position in target,
                    // or in last region if there are less regions in target
                    let targetRegionName = this.targetRegionsNameByPosition[sourceRegionPos] || lastRegionName;
                    let targetRegion: Region = this.targetRegionsByName[targetRegionName];

                    let updatedRegion: Region = this.addComponents(region, targetRegion);
                    this.targetRegionsByName[targetRegionName] = updatedRegion;
                }
            });
        }

        private initLookupTables(layoutDescriptorRegions: RegionDescriptor[], parent: LayoutComponent): void {
            this.targetRegionsByName = {};
            this.targetRegionsNameByPosition = {};
            this.sourceRegionsPositionByName = {};

            layoutDescriptorRegions.forEach((regionDescriptor: RegionDescriptor, idx: number) => {
                let regionName = regionDescriptor.getName();
                let layoutRegion = Region.create().
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

            let result = Region.create(toRegion);
            fromRegion.getComponents().forEach((component: Component) => {
                result.addComponent(component);
            });
            return result.build();
        }

    }

}