module app.publish {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentPath = api.content.ContentPath;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import PublishContentRequest = api.content.PublishContentRequest;
    import CompareStatus = api.content.CompareStatus;
    import ResolvedPublishRequestedContentJson = api.content.json.ResolvedPublishRequestedContentJson;
    import ResolvedPublishDependencyContentJson = api.content.json.ResolvedPublishDependencyContentJson;
    import ContentName = api.content.ContentName;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ContentPublishRequestedItem extends ContentPublishItem {

        private childrenCount: number;

        private dependantsCount: number;

        constructor(builder: ContentPublishRequestedItemBuilder) {
            super(builder);

            this.childrenCount = builder.childrenCount;
            this.dependantsCount = builder.dependantsCount;
        }

        getChildrenCount(): number {
            return this.childrenCount;
        }

        getDependantsCount(): number {
            return this.dependantsCount;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentPublishRequestedItem)) {
                return false;
            }

            var other = <ContentPublishRequestedItem>o;

            if (!super.equals(o)) {
                return false;
            }

            if (!api.ObjectHelper.numberEquals(this.childrenCount, other.childrenCount)) {
                return false;
            }
            if (!api.ObjectHelper.numberEquals(this.dependantsCount, other.dependantsCount)) {
                return false;
            }

            return true;
        }

        /**
         * Builds array of ContentPublishItem[] from contents that were returned as initially requsted to publish.
         * Returned array should correspond to contents with ids used for ResolvePublishDependenciesRequest.
         */
        static getPushRequestedContents(jsonItems: ResolvedPublishRequestedContentJson[]): ContentPublishRequestedItem[] {
            var array: ContentPublishRequestedItem[] = [];
            jsonItems.forEach((obj: ResolvedPublishRequestedContentJson) => {
                array.push(new ContentPublishRequestedItemBuilder().fromJson(obj).build());
            });
            return array;
        }

    }

    export class ContentPublishRequestedItemBuilder extends ContentPublishItemBuilder {

        childrenCount: number;

        dependantsCount: number;

        fromJson(json: ResolvedPublishRequestedContentJson): ContentPublishRequestedItemBuilder {
            super.fromJson(json);

            this.childrenCount = json.childrenCount;
            this.dependantsCount = json.dependantsCount;

            return this;
        }

        setChildrenCount(value: number): ContentPublishRequestedItemBuilder {
            this.childrenCount = value;
            return this;
        }

        setDependantsCount(value: number): ContentPublishRequestedItemBuilder {
            this.dependantsCount = value;
            return this;
        }

        build(): ContentPublishRequestedItem {
            return new ContentPublishRequestedItem(this);
        }
    }

    export class ContentPublishDependencyItem extends ContentPublishItem {

        private child: boolean;

        constructor(builder: ContentPublishDependencyItemBuilder) {
            super(builder);

            this.child = builder.child;
        }

        isChild(): boolean {
            return this.child;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentPublishDependencyItem)) {
                return false;
            }

            var other = <ContentPublishDependencyItem>o;

            if (!super.equals(o)) {
                return false;
            }

            if (!api.ObjectHelper.booleanEquals(this.child, other.child)) {
                return false;
            }

            return true;
        }

        static getPushDependenciesContents(jsonItems: ResolvedPublishDependencyContentJson[]): ContentPublishDependencyItem[] {
            var array: ContentPublishDependencyItem[] = [];
            jsonItems.forEach((obj: ResolvedPublishDependencyContentJson) => {
                array.push(new ContentPublishDependencyItemBuilder().fromJson(obj).build());
            });
            return array;
        }

    }

    export class ContentPublishDependencyItemBuilder extends ContentPublishItemBuilder {

        child: boolean;

        fromJson(json: ResolvedPublishDependencyContentJson): ContentPublishDependencyItemBuilder {
            super.fromJson(json);

            this.child = json.child;

            return this;
        }

        build(): ContentPublishDependencyItem {
            return new ContentPublishDependencyItem(this);
        }
    }
}
