module api.security.acl {

    import ContentsPermissionsEntryJson = api.content.json.ContentPermissionsJson;

    export class ContentAccessControlList extends AccessControlList implements api.Equitable, api.Cloneable {

        private contentId: api.content.ContentId;

        constructor(id: string, entries?: AccessControlEntry[]) {
            super(entries);

            this.contentId = new api.content.ContentId(id);
        }

        getContentId(): api.content.ContentId {
            return this.contentId;
        }

        toString(): string {
            return this.contentId.toString() + ': ' + super.toString();
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentAccessControlList)) {
                return false;
            }

            var other = <ContentAccessControlList>o;
            return this.contentId.equals(other.getContentId()) &&
                   api.ObjectHelper.arrayEquals(this.getEntries().sort(), other.getEntries().sort());
        }

        clone(): ContentAccessControlList {
            return new ContentAccessControlList(this.contentId.toString(), super.clone().getEntries());
        }

        static fromJson(json: ContentsPermissionsEntryJson): ContentAccessControlList {
            var cacl = new ContentAccessControlList(json.contentId);
            json.permissions.forEach((entryJson: api.security.acl.AccessControlEntryJson) => {
                var entry = AccessControlEntry.fromJson(entryJson);
                cacl.add(entry);
            });
            return cacl;
        }
    }
}
