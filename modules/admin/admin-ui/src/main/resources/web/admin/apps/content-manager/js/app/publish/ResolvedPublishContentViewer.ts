module app.publish {

    import ContentPath = api.content.ContentPath;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import CompareStatus = api.content.CompareStatus;

    export class ResolvedPublishContentViewer<M extends ContentPublishItem> extends api.ui.NamesAndIconViewer<M> {

        constructor() {
            super("content-resolved-publish-viewer");
        }

        resolveDisplayName(object: M): string {
            var contentName = object.getName(),
                invalid = !object.isValid() || !object.getDisplayName() || contentName.isUnnamed(),
                pendingDelete = CompareStatus.PENDING_DELETE == object.getCompareStatus() ? true : false;
            this.toggleClass("invalid", invalid);
            this.toggleClass("pending-delete", pendingDelete);

            return object.getDisplayName();
        }

        resolveUnnamedDisplayName(object: M): string {
            return object.getType() ? object.getType().getLocalName() : "";
        }

        resolveSubName(object: M, relativePath: boolean = false): string {
            var contentName = object.getName();
            if (relativePath) {
                return !contentName.isUnnamed() ? object.getName().toString() :
                       api.ui.NamesAndIconViewer.EMPTY_SUB_NAME;
            } else {
                return !contentName.isUnnamed() ? object.getPath().toString() :
                       ContentPath.fromParent(object.getPath().getParentPath(),
                           api.ui.NamesAndIconViewer.EMPTY_SUB_NAME).toString();
            }
        }

        resolveSubTitle(object: M): string {
            return object.getPath().toString();
        }

        resolveIconUrl(object: any): string {
            return new ContentIconUrlResolver().setContent(object).resolve();
        }
    }

    export class ResolvedDependantContentViewer<M extends ContentPublishItem> extends api.ui.Viewer<M> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor(className?: string, size: api.app.NamesAndIconViewSize = api.app.NamesAndIconViewSize.small) {
            super(className);
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(size).build();
            this.addClass("content-resolved-dependant-viewer");
        }

        setObject(object: M, relativePath: boolean = false) {
            super.setObject(object);

            var displayName = this.resolveDisplayName(object),
                iconUrl = this.resolveIconUrl(object);

            this.namesAndIconView.setMainName(displayName);

            if (!!iconUrl) {
                this.namesAndIconView.setIconUrl(iconUrl);
            }

            this.render();
        }

        resolveDisplayName(object: M): string {
            var contentName = object.getName(),
                invalid = !object.isValid() || !object.getDisplayName() || contentName.isUnnamed(),
                pendingDelete = CompareStatus.PENDING_DELETE == object.getCompareStatus() ? true : false;
            this.toggleClass("invalid", invalid);
            this.toggleClass("pending-delete", pendingDelete);

            return object.getPath().toString();
        }

        resolveIconUrl(object: any): string {
            return new ContentIconUrlResolver().setContent(object).resolve();
        }

        getPreferredHeight(): number {
            return 50;
        }

        doRender() {
            this.removeChildren();
            this.appendChild(this.namesAndIconView);
            return true;
        }
    }
}