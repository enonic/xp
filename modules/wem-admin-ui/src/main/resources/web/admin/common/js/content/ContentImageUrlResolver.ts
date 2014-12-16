module api.content {

    export class ContentImageUrlResolver extends api.icon.IconUrlResolver {

        private contentId: ContentId;

        private size: string = "";

        private ts: string = null;

        setContentId(value: ContentId): ContentImageUrlResolver {
            this.contentId = value;
            return this;
        }

        setSize(value: number): ContentImageUrlResolver {
            this.size = "" + value;
            return this;
        }

        setTimestamp(value: Date): ContentImageUrlResolver {
            this.ts = "" + value.getTime();
            return this;
        }

        resolve(): string {

            var url = "content/image/" + this.contentId.toString();
            if (this.size.length > 0) {
                url = this.appendParam("size", this.size, url);
            }
            if (this.ts) {
                url = this.appendParam("ts", this.ts, url);
            }

            return api.util.UriHelper.getRestUri(url);
        }
    }
}