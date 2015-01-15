module api.content.page {

    import Region = api.content.page.region.Region;
    import Component = api.content.page.region.Component;
    import ComponentPath = api.content.page.region.ComponentPath;
    import ComponentPathRegionAndComponent = api.content.page.region.ComponentPathRegionAndComponent;
    import ComponentAddedEvent = api.content.page.region.ComponentAddedEvent;
    import ComponentRemovedEvent = api.content.page.region.ComponentRemovedEvent;
    import RegionPropertyValueChangedEvent = api.content.page.region.RegionPropertyValueChangedEvent;

    export class AbstractRegions implements api.Equitable {

        public debug: boolean = false;

        private regionByName: {[s:string] : region.Region;} = {};

        private changedListeners: {(event: RegionsChangedEvent):void}[] = [];

        private regionChangedListeners: {(event: RegionChangedEvent):void}[] = [];

        private regionAddedListeners: {(event: RegionAddedEvent):void}[] = [];

        private regionRemovedListeners: {(event: RegionRemovedEvent):void}[] = [];

        constructor(regions: region.Region[]) {

            regions.forEach((region: region.Region) => {
                if (this.regionByName[region.getName()] != undefined) {
                    throw new Error("Regions in a Page must be unique by name, duplicate found: " + region.getName());
                }

                this.addRegion(region);
            });
        }

        addRegion(region: Region) {

            this.regionByName[region.getName()] = region;

            this.notifyRegionAdded(region.getPath());
            this.registerRegionListeners(region);
        }

        private registerRegionListeners(region: Region) {

            if (this.handleRegionChanged.bind) {
                region.onChanged(this.handleRegionChanged.bind(this));
            }
            else {
                // PhantomJS does not support bind
                region.onChanged((event) => {
                    this.handleRegionChanged(event);
                });
            }
        }

        private unregisterRegionListeners(region: Region) {
            region.unChanged(this.handleRegionChanged);
        }

        private handleRegionChanged(event: api.content.page.region.RegionChangedEvent) {
            this.notifyRegionChanged(event.getPath());
        }

        removeRegions(regions: Region[]) {
            regions.forEach((region: Region) => {
                delete this.regionByName[region.getName()];

                this.notifyRegionRemoved(region.getPath());
                this.unregisterRegionListeners(region);
            });
        }

        getRegions(): region.Region[] {
            var regions = [];
            for (var i in this.regionByName) {
                var region = this.regionByName[i];
                regions.push(region);
            }
            return regions;
        }

        getRegionByName(name: string): region.Region {

            return this.regionByName[name];
        }

        getComponent(path: ComponentPath): Component {

            var first: ComponentPathRegionAndComponent = path.getFirstLevel();
            var region = this.getRegionByName(first.getRegionName());
            var component = region.getComponentByIndex(first.getComponentIndex());

            if (path.numberOfLevels() == 1) {
                return component;
            }
            else {
                if (!api.ObjectHelper.iFrameSafeInstanceOf(component, api.content.page.region.LayoutComponent)) {
                    throw new Error("Expected component to be a LayoutComponent: " + api.ClassHelper.getClassName(component));
                }

                var layoutComponent = <api.content.page.region.LayoutComponent> component;
                return layoutComponent.getComponent(path.removeFirstLevel());
            }
        }

        public toJson(): region.RegionJson[] {

            var regionJsons: region.RegionJson[] = [];
            this.getRegions().forEach((region: region.Region) => {
                regionJsons.push(region.toJson());
            });
            return regionJsons;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, AbstractRegions)) {
                return false;
            }

            var other = <AbstractRegions>o;


            var thisRegions = this.getRegions();
            var otherRegions = other.getRegions();

            if (!api.ObjectHelper.arrayEquals(thisRegions, otherRegions)) {
                return false;
            }

            return true;
        }

        onChanged(listener: (event: RegionChangedEvent)=>void) {
            this.changedListeners.push(listener);
        }

        unChanged(listener: (event: RegionChangedEvent)=>void) {
            this.changedListeners =
            this.changedListeners.filter((curr: (event: RegionChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyChanged(event: RegionsChangedEvent) {
            if (this.debug) {
                console.debug("AbstractRegions.notifyChanged");
            }
            this.changedListeners.forEach((listener: (event: RegionsChangedEvent)=>void) => {
                listener(event);
            })
        }

        onRegionChanged(listener: (event: RegionChangedEvent)=>void) {
            this.regionChangedListeners.push(listener);
        }

        unRegionChanged(listener: (event: RegionChangedEvent)=>void) {
            this.regionChangedListeners =
            this.regionChangedListeners.filter((curr: (event: RegionChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyRegionChanged(regionPath: api.content.page.region.RegionPath) {
            var event = new RegionChangedEvent(regionPath);
            if (this.debug) {
                console.debug("AbstractRegions.notifyRegionChanged: " + event.getRegionPath().toString());
            }
            this.regionChangedListeners.forEach((listener: (event: RegionChangedEvent)=>void) => {
                listener(event);
            });
            this.notifyChanged(event);
        }

        onRegionAdded(listener: (event: RegionAddedEvent)=>void) {
            this.regionAddedListeners.push(listener);
        }

        unRegionAdded(listener: (event: RegionAddedEvent)=>void) {
            this.regionAddedListeners =
            this.regionAddedListeners.filter((curr: (event: RegionAddedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyRegionAdded(regionPath: api.content.page.region.RegionPath) {
            var event = new RegionAddedEvent(regionPath);
            if (this.debug) {
                console.debug("AbstractRegions.notifyRegionAdded: " + event.getRegionPath().toString());
            }
            this.regionAddedListeners.forEach((listener: (event: RegionAddedEvent)=>void) => {
                listener(event);
            });
            this.notifyChanged(event);
        }

        onRegionRemoved(listener: (event: RegionRemovedEvent)=>void) {
            this.regionRemovedListeners.push(listener);
        }

        unRegionRemoved(listener: (event: RegionRemovedEvent)=>void) {
            this.regionRemovedListeners =
            this.regionRemovedListeners.filter((curr: (event: RegionRemovedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyRegionRemoved(regionPath: api.content.page.region.RegionPath) {
            var event = new RegionRemovedEvent(regionPath);
            if (this.debug) {
                console.debug("AbstractRegions.notifyRegionRemoved: " + event.getRegionPath().toString());
            }
            this.regionRemovedListeners.forEach((listener: (event: RegionRemovedEvent)=>void) => {
                listener(event);
            });
            this.notifyChanged(event);
        }
    }

}