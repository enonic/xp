module api.content {

    import AccessControlList = api.security.acl.AccessControlList;
    import AccessControlEntry = api.security.acl.AccessControlEntry;
    import Property = api.data.Property;
    import PropertyTree = api.data.PropertyTree;
    import PropertyPath = api.data.PropertyPath;

    export class Content extends ContentSummary implements api.Equitable, api.Cloneable {

        private data: PropertyTree;

        private attachments: api.content.attachment.Attachments;

        private extraData: ExtraData[] = [];

        private pageObj: api.content.page.Page;

        private permissions: AccessControlList;

        private inheritPermissions: boolean;

        private overwritePermissions: boolean;

        constructor(builder: ContentBuilder) {
            super(builder);

            api.util.assertNotNull(builder.data, 'data is required for Content');
            this.data = builder.data;
            this.attachments = builder.attachments;
            this.extraData = builder.extraData || [];
            this.pageObj = builder.pageObj;
            this.permissions = builder.permissions || new AccessControlList();
            this.inheritPermissions = builder.inheritPermissions;
            this.overwritePermissions = builder.overwritePermissions;
        }

        getContentData(): PropertyTree {
            return this.data;
        }

        getAttachments(): api.content.attachment.Attachments {
            return this.attachments;
        }

        getExtraData(name: api.schema.mixin.MixinName): ExtraData {
            return this.extraData.filter((item: ExtraData) => item.getName().equals(name))[0];
        }

        getAllExtraData(): ExtraData[] {
            return this.extraData;
        }

        getProperty(propertyName: string): Property {
            return !!propertyName ? this.data.getProperty(propertyName) : null;
        }

        getPage(): api.content.page.Page {
            return this.pageObj;
        }

        getPermissions(): AccessControlList {
            return this.permissions;
        }

        isInheritPermissionsEnabled(): boolean {
            return this.inheritPermissions;
        }

        isOverwritePermissionsEnabled(): boolean {
            return this.overwritePermissions;
        }

        isAnyPrincipalAllowed(principalKeys: api.security.PrincipalKey[], permission: api.security.acl.Permission): boolean {

            if (principalKeys.map(key => key.toString()).indexOf(api.security.RoleKeys.ADMIN.toString()) > -1) {
                return true;
            }

            for (let i = 0; i < this.permissions.getEntries().length; i++) {
                let entry = this.permissions.getEntries()[i];

                if (entry.isAllowed(permission)) {
                    let principalInEntry = principalKeys.some((principalKey: api.security.PrincipalKey) => {
                        if (principalKey.equals(entry.getPrincipalKey())) {
                            return true;
                        }
                    });
                    if (principalInEntry) {
                        return true;
                    }
                }
            }
            return false;
        }

        private trimPropertyTree(data: PropertyTree): PropertyTree {
            let copy = data.copy();
            copy.getRoot().removeEmptyValues();
            return copy;
        }

        private trimExtraData(extraData: ExtraData): ExtraData {
            let copy = extraData.clone();
            copy.getData().getRoot().removeEmptyValues();
            return copy;
        }

        public containsChildContentId(contentId: ContentId): wemQ.Promise<boolean> {
            const page = this.getPage();

            if (page) {
                if (page.doesFragmentContainId(contentId)) {
                    return wemQ(true);
                }

                return page.doRegionComponentsContainId(contentId);
            }

            return wemQ(false);
        }

        dataEquals(other: PropertyTree, ignoreEmptyValues: boolean = false): boolean {
            let data;
            let otherData;
            if (ignoreEmptyValues) {
                data = this.trimPropertyTree(this.data);
                otherData = this.trimPropertyTree(other);
            } else {
                data = this.data;
                otherData = other;
            }
            return api.ObjectHelper.equals(data, otherData);
        }

        extraDataEquals(other: ExtraData[], ignoreEmptyValues: boolean = false): boolean {
            let extraData;
            let otherExtraData;
            if (ignoreEmptyValues) {
                extraData = this.extraData.map((m) => this.trimExtraData(m)).filter((m) => !m.getData().isEmpty());
                otherExtraData = other.map((m) => this.trimExtraData(m)).filter((m) => !m.getData().isEmpty());
            } else {
                extraData = this.extraData;
                otherExtraData = other;
            }
            let comparator = new api.content.util.ExtraDataByMixinNameComparator();

            return api.ObjectHelper.arrayEquals(extraData.sort(comparator.compare), otherExtraData.sort(comparator.compare));
        }

        equals(o: api.Equitable, ignoreEmptyValues: boolean = false, shallow: boolean = false): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Content)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            let other = <Content>o;

            if (!shallow) {
                if (!this.dataEquals(other.getContentData(), ignoreEmptyValues)) {
                    return false;
                }

                if (!this.extraDataEquals(other.getAllExtraData(), ignoreEmptyValues)) {
                    return false;
                }
            }

            if (!api.ObjectHelper.equals(this.pageObj, other.pageObj)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.permissions, other.permissions)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.attachments, other.attachments)) {
                return false;
            }

            if (this.inheritPermissions !== other.inheritPermissions) {
                return false;
            }

            return true;
        }

        clone(): Content {
            return this.newBuilder().build();
        }

        newBuilder(): ContentBuilder {
            return new ContentBuilder(this);
        }

        static fromJson(json: api.content.json.ContentJson): Content {

            let type = new api.schema.content.ContentTypeName(json.type);

            if (type.isSite()) {
                return new site.SiteBuilder().fromContentJson(json).build();
            } else if (type.isPageTemplate()) {
                return new page.PageTemplateBuilder().fromContentJson(json).build();
            }
            return new ContentBuilder().fromContentJson(json).build();
        }

        static fromJsonArray(jsonArray: api.content.json.ContentJson[]): Content[] {
            let array: Content[] = [];
            jsonArray.forEach((json: api.content.json.ContentJson) => {
                array.push(Content.fromJson(json));
            });
            return array;
        }
    }

    export class ContentBuilder extends ContentSummaryBuilder {

        data: PropertyTree;

        attachments: api.content.attachment.Attachments;

        extraData: ExtraData[];

        pageObj: api.content.page.Page;

        permissions: AccessControlList;

        inheritPermissions: boolean = true;

        overwritePermissions: boolean = false;

        constructor(source?: Content) {
            super(source);
            if (source) {

                this.data = source.getContentData() ? source.getContentData().copy() : null;
                this.attachments = source.getAttachments();
                this.extraData = source.getAllExtraData() ? source.getAllExtraData().map((extraData: ExtraData) => extraData.clone()) : [];
                this.pageObj = source.getPage() ? source.getPage().clone() : null;
                this.permissions = source.getPermissions(); // TODO clone?
                this.inheritPermissions = source.isInheritPermissionsEnabled();
                this.overwritePermissions = source.isOverwritePermissionsEnabled();
            }
        }

        fromContentJson(json: api.content.json.ContentJson): ContentBuilder {

            super.fromContentSummaryJson(json);

            this.data = PropertyTree.fromJson(json.data);
            this.attachments = new api.content.attachment.AttachmentsBuilder().fromJson(json.attachments).build();
            this.extraData = [];
            json.meta.forEach((extraDataJson: api.content.json.ExtraDataJson) => {
                this.extraData.push(ExtraData.fromJson(extraDataJson));
            });

            if (this.page) {
                this.pageObj = new api.content.page.PageBuilder().fromJson(json.page).build();
                this.page = true;
            }
            if (json.permissions) {
                this.permissions = AccessControlList.fromJson(json);
            }
            if (typeof json.inheritPermissions !== 'undefined') {
                this.inheritPermissions = json.inheritPermissions;
            }

            this.overwritePermissions = false;

            return this;
        }

        setData(value: PropertyTree): ContentBuilder {
            this.data = value;
            return this;
        }

        setAttachments(value: api.content.attachment.Attachments): ContentBuilder {
            this.attachments = value;
            return this;
        }

        setPage(value: api.content.page.Page): ContentBuilder {
            this.pageObj = value;
            this.page = value ? true : false;
            return this;
        }

        setExtraData(extraData: ExtraData[]): ContentBuilder {
            this.extraData = extraData;
            return this;
        }

        setPermissions(value: AccessControlList): ContentBuilder {
            this.permissions = value;
            return this;
        }

        setInheritPermissionsEnabled(value: boolean): ContentBuilder {
            this.inheritPermissions = value;
            return this;
        }

        setOverwritePermissionsEnabled(value: boolean): ContentBuilder {
            this.overwritePermissions = value;
            return this;
        }

        build(): Content {
            return new Content(this);
        }
    }
}
