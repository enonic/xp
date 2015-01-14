module api.content {

    import Thumbnail = api.thumb.Thumbnail;
    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class ContentSummary extends ContentIdBaseItem {

        private id: string;

        private name: ContentName;

        private displayName: string;

        private path: ContentPath;

        private root: boolean;

        private children: boolean;

        private type: api.schema.content.ContentTypeName;

        private iconUrl: string;

        private thumbnail: Thumbnail;

        private modifier: string;

        private owner: string;

        private page: boolean;

        private draft: boolean;

        private createdTime: Date;

        private modifiedTime: Date;

        private deletable: boolean;

        private editable: boolean;

        private childOrder: ChildOrder;

        private language: string;

        constructor(builder: ContentSummaryBuilder) {
            super(builder);
            this.name = builder.name;
            this.displayName = builder.displayName;
            this.path = builder.path;
            this.root = builder.root;
            this.children = builder.children;
            this.type = builder.type;
            this.iconUrl = builder.iconUrl;
            this.thumbnail = builder.thumbnail;
            this.modifier = builder.modifier;
            this.owner = builder.owner;
            this.page = builder.page;
            this.draft = builder.draft;

            this.id = builder.id;
            this.createdTime = builder.createdTime;
            this.modifiedTime = builder.modifiedTime;
            this.deletable = builder.deletable;
            this.editable = builder.editable;
            this.childOrder = builder.childOrder;
            this.language = builder.language;
        }

        getName(): ContentName {
            return this.name;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        hasParent(): boolean {
            return this.path.hasParentContent();
        }

        getPath(): ContentPath {
            return this.path;
        }

        isRoot(): boolean {
            return this.root;
        }

        hasChildren(): boolean {
            return this.children;
        }

        getType(): api.schema.content.ContentTypeName {
            return this.type;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        hasThumbnail(): boolean {
            return !!this.thumbnail;
        }

        getThumbnail(): Thumbnail {
            return this.thumbnail;
        }

        getOwner(): string {
            return this.owner;
        }

        getModifier(): string {
            return this.modifier;
        }

        isSite(): boolean {
            return this.type.isSite();
        }

        isPage(): boolean {
            return this.page;
        }

        isPageTemplate(): boolean {
            return this.type.isPageTemplate();
        }

        isImage(): boolean {
            return this.type.isImage();
        }

        isDraft(): boolean {
            return this.draft;
        }

        getId(): string {
            return this.id;
        }

        getCreatedTime(): Date {
            return this.createdTime;
        }

        getModifiedTime(): Date {
            return this.modifiedTime;
        }

        isDeletable(): boolean {
            return this.deletable;
        }

        isEditable(): boolean {
            return this.editable;
        }

        getChildOrder(): ChildOrder {
            return this.childOrder;
        }

        getLanguage(): string {
            return this.language;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentSummary)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <ContentSummary>o;

            if (!api.ObjectHelper.stringEquals(this.id, other.id)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.name, other.name)) {
                return false;
            }
            if (!api.ObjectHelper.stringEquals(this.displayName, other.displayName)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.path, other.path)) {
                return false;
            }
            if (!api.ObjectHelper.booleanEquals(this.root, other.root)) {
                return false;
            }
            if (!api.ObjectHelper.booleanEquals(this.children, other.children)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.type, other.type)) {
                return false;
            }
            if (!api.ObjectHelper.stringEquals(this.iconUrl, other.iconUrl)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.thumbnail, other.thumbnail)) {
                return false;
            }
            if (!api.ObjectHelper.stringEquals(this.modifier, other.modifier)) {
                return false;
            }
            if (!api.ObjectHelper.stringEquals(this.owner, other.owner)) {
                return false;
            }
            if (!api.ObjectHelper.booleanEquals(this.page, other.page)) {
                return false;
            }
            if (!api.ObjectHelper.booleanEquals(this.draft, other.draft)) {
                return false;
            }
            if (!api.ObjectHelper.dateEquals(this.createdTime, other.createdTime)) {
                return false;
            }
            if (!api.ObjectHelper.dateEquals(this.modifiedTime, other.modifiedTime)) {
                return false;
            }
            if (!api.ObjectHelper.booleanEquals(this.deletable, other.deletable)) {
                return false;
            }
            if (!api.ObjectHelper.booleanEquals(this.editable, other.editable)) {
                return false;
            }
            if (!api.ObjectHelper.stringEquals(this.language, other.language)) {
                return false;
            }
            return true;
        }

        static fromJson(json: api.content.json.ContentSummaryJson, propertyIdProvider: PropertyIdProvider): ContentSummary {
            return new ContentSummaryBuilder().fromContentSummaryJson(json).build();
        }

        static fromJsonArray(jsonArray: api.content.json.ContentSummaryJson[], propertyIdProvider: PropertyIdProvider): ContentSummary[] {
            var array: ContentSummary[] = [];
            jsonArray.forEach((json: api.content.json.ContentSummaryJson) => {
                array.push(ContentSummary.fromJson(json, propertyIdProvider));
            });
            return array;
        }
    }

    export class ContentSummaryBuilder extends ContentIdBaseItemBuilder {

        id: string;

        name: ContentName;

        displayName: string;

        path: ContentPath;

        root: boolean;

        children: boolean;

        type: api.schema.content.ContentTypeName;

        iconUrl: string;

        thumbnail: Thumbnail;

        modifier: string;

        owner: string;

        page: boolean;

        draft: boolean;

        createdTime: Date;

        modifiedTime: Date;

        deletable: boolean;

        editable: boolean;

        childOrder: ChildOrder;

        language: string;

        constructor(source?: ContentSummary) {
            super(source);
            if (source) {
                this.id = source.getId();
                this.name = source.getName();
                this.displayName = source.getDisplayName();
                this.path = source.getPath();
                this.root = source.isRoot();
                this.children = source.hasChildren();
                this.type = source.getType();
                this.iconUrl = source.getIconUrl();
                this.thumbnail = source.getThumbnail();
                this.modifier = source.getModifier();
                this.owner = source.getOwner();
                this.page = source.isPage();
                this.draft = source.isDraft();
                this.createdTime = source.getCreatedTime();
                this.modifiedTime = source.getModifiedTime();
                this.deletable = source.isDeletable();
                this.editable = source.isEditable();
                this.childOrder = source.getChildOrder();
                this.language = source.getLanguage();
            }
        }


        fromContentSummaryJson(json: api.content.json.ContentSummaryJson): ContentSummaryBuilder {

            super.fromContentIdBaseItemJson(json);

            this.name = ContentName.fromString(json.name);
            this.displayName = json.displayName;
            this.path = ContentPath.fromString(json.path);
            this.root = json.isRoot;
            this.children = json.hasChildren;
            this.type = new api.schema.content.ContentTypeName(json.type);
            this.iconUrl = json.iconUrl;
            this.thumbnail = json.thumbnail ? Thumbnail.create().fromJson(json.thumbnail).build() : null;
            this.modifier = json.modifier;
            this.owner = json.owner;
            this.page = json.isPage;
            this.draft = json.draft;
            this.language = json.language;

            this.id = json.id;
            this.createdTime = json.createdTime ? new Date(Date.parse(json.createdTime)) : null;
            this.modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;

            this.deletable = json.deletable;
            this.editable = json.editable;

            this.childOrder = ChildOrder.fromJson(json.childOrder);

            return this;
        }

        setId(value: string): ContentSummaryBuilder {
            this.id = value;
            return this;
        }

        setDraft(value: boolean): ContentSummaryBuilder {
            this.draft = value;
            return this;
        }

        setName(value: ContentName): ContentSummaryBuilder {
            this.name = value;
            return this;
        }

        setType(value: api.schema.content.ContentTypeName): ContentSummaryBuilder {
            this.type = value;
            return this;
        }

        setDisplayName(value: string): ContentSummaryBuilder {
            this.displayName = value;
            return this;
        }

        setHasChildren(value: boolean): ContentSummaryBuilder {
            this.children = value;
            return this;
        }

        setDeletable(value: boolean): ContentSummaryBuilder {
            this.deletable = value;
            return this;
        }

        build(): ContentSummary {
            return new ContentSummary(this);
        }
    }
}