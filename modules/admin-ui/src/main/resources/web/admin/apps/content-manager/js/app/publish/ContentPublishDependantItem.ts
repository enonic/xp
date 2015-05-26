module app.publish {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentPath = api.content.ContentPath;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import PublishContentRequest = api.content.PublishContentRequest;
    import CompareStatus = api.content.CompareStatus;
    import GetDependantsResultJson = api.content.json.ResolveDependantsResultJson;
    import ResolvedDependantContentJson = api.content.json.ResolvedDependantContentJson
    import ContentName = api.content.ContentName;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ContentPublishDependantItem extends ContentPublishItem {

        private dependsOnContentId: string;

        constructor(builder: ContentPublishDependantItemBuilder) {
            super(builder);

            this.dependsOnContentId = builder.dependsOnContentId;
        }

        getChildrenCount(): string {
            return this.dependsOnContentId;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentPublishDependantItem)) {
                return false;
            }

            var other = <ContentPublishDependantItem>o;

            if (!super.equals(o)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.dependsOnContentId, other.dependsOnContentId)) {
                return false;
            }

            return true;
        }

        /**
         * Builds array of ContentPublishItem[] from contents that were returned as initially requsted to publish.
         * Returned array should correspond to contents with ids used for ResolvePublishDependenciesRequest.
         */
        static getDependantsResolved(json: GetDependantsResultJson): ContentPublishDependantItem[] {
            var array: ContentPublishDependantItem[] = [];
            json.dependantContents.forEach((obj: ResolvedDependantContentJson) => {
                array.push(new ContentPublishDependantItemBuilder().fromJson(obj).build());
            });
            return array;
        }


    }

    export class ContentPublishDependantItemBuilder extends ContentPublishItemBuilder {

        dependsOnContentId: string;

        fromJson(json: ResolvedDependantContentJson): ContentPublishDependantItemBuilder {
            super.fromJson(json);

            this.dependsOnContentId = json.dependsOnContentId;

            return this;
        }

        setDependsOnContentId(value: string): ContentPublishDependantItemBuilder {
            this.dependsOnContentId = value;
            return this;
        }

        build(): ContentPublishDependantItem {
            return new ContentPublishDependantItem(this);
        }
    }
}
