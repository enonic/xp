module api.content.page {

    export class PageComponentIdMap {

        private map: {[id: string]: ComponentPath2} = {};

        add(path: ComponentPath2, id:number) {
            this.map[id] = path;
        }

        getPaths(): ComponentPath2[] {
            var paths: ComponentPath2[] = [];

            for (var id in this.map) {
                if (this.map.hasOwnProperty(id)) {
                    paths.push(this.map[id]);
                }
            }

            return paths;
        }

        getIdByPath(path: ComponentPath2): number {

            for (var id in this.map) {
                if (this.map.hasOwnProperty(id) && path.equals(this.map[id])) {
                    return parseInt(id);
                }
            }

            return null;
        }

    }
}