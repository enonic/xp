module api.content {

    export class ContentImageUrlResolver extends api.icon.IconUrlResolver {

        private contentId: ContentId;

        private size: string = "";

        private ts: string = null;

        private scaleWidth: string = null; // parameter states if width of the image must be preferred over its height

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

        setScaleWidth(value: boolean): ContentImageUrlResolver {
            this.scaleWidth = "" + value;
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
            if (this.scaleWidth) {
                url = this.appendParam("scaleWidth", this.scaleWidth, url);
            }

            return api.util.UriHelper.getRestUri(url);
        }
    }
}