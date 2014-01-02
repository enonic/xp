module api.content {

    export class ContentIconUrlResolver extends api.icon.IconUrlResolver<ContentIconUrlResolver,ContentSummary> {

        public getResourcePath(): api.rest.Path {
            return api.rest.Path.fromString("content/image");
        }

        public resolve(icon: api.icon.Icon): string {

            return this.toRestUrl(this.getResourcePath()) + "/" + icon.getBlobKey() + "?" + this.resolveQueryParams();
        }

        static default(): string {

            return api.util.getAdminUri("common/images/default.content.png");
        }
    }
}