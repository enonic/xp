module app.browse.grid {

    export class ContentGridCacheItem2 {

        private contentId:string;

        private contentPath:api.content.ContentPath;

        private contentUpdated:boolean;

        private contentExpanded:boolean;

        constructor(id:string, path:api.content.ContentPath,
                    updated:boolean = false, expanded: boolean = true) {
            this.contentId = id;
            this.contentPath = path;
            this.contentUpdated = updated;
            this.contentExpanded = expanded;
        }

        getId():string {
            return this.contentId;
        }

        getPath():api.content.ContentPath {
            return this.contentPath;
        }

        isUpdated():boolean {
            return this.contentUpdated;
        }

        setUpdated(updated:boolean = true) {
            this.contentUpdated = updated;
        }

        isExpanded():boolean {
            return this.contentExpanded;
        }

        setExpanded(expanded:boolean = true) {
            this.contentExpanded = expanded;
        }
    }
}
