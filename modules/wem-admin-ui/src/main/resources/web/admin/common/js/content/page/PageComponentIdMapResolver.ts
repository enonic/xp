module api.content.page {

    import Body = api.dom.Body;

    export class PageComponentIdMapResolver {

        private counter: number;

        private body: Body;

        private map: PageComponentIdMap;

        constructor(body: Body) {
            this.body = body;
        }

        resolve(): PageComponentIdMap {

            this.counter = 0;
            this.map = new PageComponentIdMap();
            this.parseRegions(this.body);

            return this.map;
        }

        private parseRegions(parent: api.dom.Element) {

            var regionIndex: number = 0;
            var children = parent.getChildren();
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (type == "region") {

                    this.parsePageComponents(element, new RegionPath2(null, regionIndex++));
                }
            });
        }

        private parsePageComponents(parent: api.dom.Element, region: RegionPath2) {

            var currCounter = this.counter;
            var componentIndex = 0;
            var children = parent.getChildren();
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (PageComponentType.byShortName(type)) {
                    var path = ComponentPath2.fromRegionPathAndComponentIndex(region, componentIndex++);
                    this.map.add(path, currCounter++);
                }
            });
            this.counter = currCounter;
        }
    }
}