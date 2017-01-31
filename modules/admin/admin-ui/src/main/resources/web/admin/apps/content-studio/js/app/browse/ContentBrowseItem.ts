import '../../api.ts';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import AccessControlList = api.security.acl.AccessControlList;
import ViewItem = api.app.view.ViewItem;

export class ContentBrowseItem extends api.app.browse.BrowseItem<api.content.ContentSummaryAndCompareStatus> {

    private accessControlList: api.security.acl.AccessControlList;

    constructor(model: ContentSummaryAndCompareStatus) {
        super(model);
        this.accessControlList = null;
    }

    getAccessControlList(): AccessControlList {
        return this.accessControlList;
    }

    setAccessControlList(accessControlList: AccessControlList) {
        this.accessControlList = accessControlList;
    }

    equals(o: api.Equitable): boolean {
        if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentBrowseItem)) {
            return false;
        }
        let other = <ContentBrowseItem> o;
        return super.equals(o) &&
               api.ObjectHelper.equals(this.accessControlList, other.getAccessControlList());
    }
}
