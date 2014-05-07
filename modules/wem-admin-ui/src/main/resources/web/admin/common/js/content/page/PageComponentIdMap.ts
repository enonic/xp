module api.content.page {

    export class PageComponentIdMap {

        private idToPathMap: {[id: string]: ComponentPath2} = {};
        private pathToIdMap: {[path: string]: number} = {};

        add(path: ComponentPath2, id:number) {
            api.util.assert(!this.idToPathMap[id], "A PageComponentIdMap can only contain unique id's: " + id);
            this.idToPathMap[id] = path;
            this.pathToIdMap[path.toString()] = id;
        }

        getPaths(): ComponentPath2[] {
            var paths: ComponentPath2[] = [];

            for (var id in this.idToPathMap) {
                if (this.idToPathMap.hasOwnProperty(id)) {
                    paths.push(this.idToPathMap[id]);
                }
            }

            return paths;
        }

        getIdByPath(path: ComponentPath2): number {

            return this.pathToIdMap[path.toString()];
        }

    }
}