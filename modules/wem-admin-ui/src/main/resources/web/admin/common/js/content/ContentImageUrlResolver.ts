module api.content {

    export class ContentImageUrlResolver extends api.icon.IconUrlResolver {

        private contentId: ContentId;

        private size: string = "";

        setContentId(value: ContentId): ContentImageUrlResolver {
            this.contentId = value;
            return this;
        }

        setSize(value: number): ContentImageUrlResolver {
            this.size = "" + value;
            return this;
        }

        resolve(): string {

            var url = "content/image/" + this.contentId.toString();
            if (this.size.length > 0) {
                url = this.appendParam("size", this.size, url);
            }
            return api.util.UriHelper.getRestUri(url);
        }
    }
}