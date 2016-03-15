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
                       api.content.ContentUnnamed.prettifyUnnamed();
            } else {
                return !contentName.isUnnamed() ? object.getPath().toString() :
                       ContentPath.fromParent(object.getPath().getParentPath(),
                           api.content.ContentUnnamed.prettifyUnnamed()).toString();
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

        private namesAndIconView: DependantView;

        private size: api.app.NamesAndIconViewSize;

        constructor(className?: string, size: api.app.NamesAndIconViewSize = api.app.NamesAndIconViewSize.small) {
            super(className);
            this.size = size;
            this.addClass("content-resolved-dependant-viewer");
        }

        setObject(object: M, relativePath: boolean = false) {
            super.setObject(object);

            var displayName = this.resolveDisplayName(object),
                iconUrl = this.resolveIconUrl(object);

            if (!!object.getType() && !!object.getType().isImage()) {
                this.namesAndIconView = new DependantView(this.size, true);
            } else if (!!iconUrl) {
                this.namesAndIconView = new DependantView(this.size);
                this.namesAndIconView.setIconUrl(iconUrl);
            }

            this.namesAndIconView.setMainName(displayName);

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

    export class DependantView extends api.dom.DivEl {

        private wrapperDivEl: api.dom.DivEl;

        private iconImageEl: api.dom.ImgEl;

        private iconDivEl: api.dom.DivEl;

        private namesView: api.app.NamesView;

        constructor(size?: api.app.NamesAndIconViewSize, isForImageContent: boolean = false) {
            super("names-and-icon-view");
            var sizeClassName: string = api.app.NamesAndIconViewSize[size];
            if (size) {
                this.addClass(sizeClassName);
            }

            this.wrapperDivEl = new api.dom.DivEl("wrapper");
            this.appendChild(this.wrapperDivEl);

            if (!isForImageContent) {
                this.iconImageEl = new api.dom.ImgEl(null, "font-icon-default");
                this.wrapperDivEl.appendChild(this.iconImageEl);
            } else {
                this.iconDivEl = new api.dom.DivEl("font-icon-default image");
                this.wrapperDivEl.appendChild(this.iconDivEl);
            }

            this.namesView = new api.app.NamesView(false);
            this.appendChild(this.namesView);
        }

        setMainName(value: string): DependantView {
            this.namesView.setMainName(value);
            return this;
        }

        setIconUrl(value: string): DependantView {
            this.iconImageEl.setSrc(value);
            return this;
        }
    }
}
