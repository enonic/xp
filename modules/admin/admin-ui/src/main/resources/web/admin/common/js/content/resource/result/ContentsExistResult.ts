module api.content.resource.result {

    export class ContentsExistResult {

        private contentsExistMap: Object = {};

        constructor(json: api.content.json.ContentsExistJson) {
            json.contentsExistJson.forEach(item => {
                this.contentsExistMap[item.contentId] = item.exists;
            });
        }

        contentExists(id: string): boolean {
            return this.contentsExistMap.hasOwnProperty(id) && this.contentsExistMap[id];
        }
    }
}