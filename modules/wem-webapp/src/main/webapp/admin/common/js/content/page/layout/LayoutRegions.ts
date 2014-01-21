module api.content.page.layout {

    export class LayoutRegions {

        private regionByName: {[s:string] : api.content.page.region.Region;} = {};

        constructor(builder: LayoutRegionsBuilder) {

            builder.regions.forEach((region: api.content.page.region.Region) => {
                if (this.regionByName[region.getName()] != undefined) {
                    throw new Error("Regions in a Page must be unique by name, duplicate found: " + region.getName());
                }

                this.regionByName[region.getName()] = region;
            });
        }

        ensureUniqueComponentNameFromTemplate(templateName: api.content.page.TemplateName): api.content.page.ComponentName {

            var wantedName = new api.content.page.ComponentName(templateName.toString());
            return this.ensureUniqueComponentName(wantedName);
        }

        ensureUniqueComponentName(wantedName: api.content.page.ComponentName): api.content.page.ComponentName {

            var region: api.content.page.region.Region;
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

        getRegions(): api.content.page.region.Region[] {
            var regions = [];
            for (var i in this.regionByName) {
                var region = this.regionByName[i];
                regions.push(region);
            }
            return regions;
        }

        getRegion(name: string): api.content.page.region.Region {
            return this.regionByName[name];
        }


        getComponent(name: api.content.page.ComponentName) {

            for (var key in this.regionByName) {
                var region: api.content.page.region.Region = this.regionByName[key];

                var component = region.getComponent(name);
                if (component != null) {
                    return component;
                }
            }

            return null;
        }

        public toJson(): api.content.page.region.json.RegionJson[] {

            var regionJsons: api.content.page.region.json.RegionJson[] = [];
            this.getRegions().forEach((region: api.content.page.region.Region) => {
                regionJsons.push(region.toJson());
            });
            return regionJsons;
        }
    }

    export class LayoutRegionsBuilder {

        regions: api.content.page.region.Region[] = [];

        fromJson(regionsJson: api.content.page.region.json.RegionJson[]): LayoutRegionsBuilder {

            regionsJson.forEach((regionJson: api.content.page.region.json.RegionJson) => {

                var regionBuilder = new api.content.page.region.RegionBuilder().
                    setName(regionJson.name);

                regionJson.components.forEach((componentJson: api.content.page.json.PageComponentTypeWrapperJson) => {
                    var pageComponent = api.content.page.PageComponentFactory.createFromJson(componentJson);
                    regionBuilder.addComponent(pageComponent);
                });


                this.addRegion(regionBuilder.build());
            });
            return this;
        }

        addRegion(value: api.content.page.region.Region): LayoutRegionsBuilder {
            this.regions.push(value);
            return this;
        }

        public build(): LayoutRegions {
            return new LayoutRegions(this);
        }
    }
}