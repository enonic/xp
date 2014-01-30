module api.content.page {

    export class AbstractRegions {

        private regionByName: {[s:string] : region.Region;} = {};

        constructor(regions: region.Region[]) {

            regions.forEach((region: region.Region) => {
                if (this.regionByName[region.getName()] != undefined) {
                    throw new Error("Regions in a Page must be unique by name, duplicate found: " + region.getName());
                }

                this.regionByName[region.getName()] = region;
            });
        }

        addComponent(component: PageComponent, regionName: string) {

            var region = this.getRegion(regionName);
            region.addComponent(component);
        }

        hasComponent(name: ComponentName): boolean {

            for (var key in this.regionByName) {
                var region = this.regionByName[key];
                if (region.hasComponentWithName(name)) {
                    return true;
                }
            }
            return false;
        }

        getRegionForComponent(name: ComponentName): region.Region {

            for (var key in this.regionByName) {
                var region = this.regionByName[key];
                if (region.hasComponentWithName(name)) {
                    return region;
                }
            }
            return null;
        }

        getRegions(): region.Region[] {
            var regions = [];
            for (var i in this.regionByName) {
                var region = this.regionByName[i];
                regions.push(region);
            }
            return regions;
        }

        getRegion(name: string): region.Region {
            return this.regionByName[name];
        }


        getComponent(path: ComponentPath) {

            var first: ComponentPathRegionAndComponent = path.getFirstLevel();
            var region = this.getRegion(first.getRegionName());
            var component = region.getComponent(first.getComponentName());

            if (path.numberOfLevels() == 1) {
                return component;
            }
            else {
                if (!( component instanceof api.content.page.layout.LayoutComponent )) {
                    throw new Error("Expected component to be a LayoutComponent: " + api.util.getClassName(component));
                }

                var layoutComponent = <api.content.page.layout.LayoutComponent> component;
                return layoutComponent.getComponent(path.removeFirstLevel());
            }
        }

        ensureUniqueComponentNameFromTemplate(templateName: TemplateName): ComponentName {

            var wantedName = new ComponentName(templateName.toString());
            return this.ensureUniqueComponentName(wantedName);
        }

        ensureUniqueComponentName(wantedName: ComponentName): ComponentName {

            var region: region.Region;
            var duplicates = false;
            for (var key in this.regionByName) {
                region = this.regionByName[key];

                if (region.countNumberOfDuplicates(wantedName) > 0) {
                    duplicates = true;
                    break;
                }
            }

            if (!duplicates) {
                return wantedName;
            }

            var instanceCount = 0;
            for (key in this.regionByName) {
                region = this.regionByName[key];
                instanceCount += region.countNumberOfDuplicates(wantedName);
            }

            return wantedName.createDuplicate(instanceCount);
        }

        public toJson(): region.json.RegionJson[] {

            var regionJsons: region.json.RegionJson[] = [];
            this.getRegions().forEach((region: region.Region) => {
                regionJsons.push(region.toJson());
            });
            return regionJsons;
        }
    }

}