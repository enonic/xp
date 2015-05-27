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

        resolveIconUrl(object: M): string {
            return new ContentIconUrlResolver().setContent(object).resolve();
        }
    }

    export class ResolvedDependantContentViewer<M extends ContentPublishItem>extends api.ui.Viewer<M> {

        private namesAndIconView: DependantNamesAndIconView;

        constructor(className?: string, size: api.app.NamesAndIconViewSize = api.app.NamesAndIconViewSize.small) {
            super(className);
            this.namesAndIconView = new DependantNamesAndIconView(size);
            this.addClass("content-resolved-dependant-viewer");
        }

        setObject(object: M, relativePath: boolean = false) {
            super.setObject(object);

            var displayName = this.resolveDisplayName(object) || this.normalizeDisplayName(this.resolveUnnamedDisplayName(object)),
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

            return object.getDisplayName();
        }

        resolveUnnamedDisplayName(object: M): string {
            return object.getType() ? object.getType().getLocalName() : "";
        }

        resolveIconUrl(object: M): string {
            return new ContentIconUrlResolver().setContent(object).resolve();
        }

        private normalizeDisplayName(displayName: string): string {
            if (api.util.StringHelper.isEmpty(displayName)) {
                return "";
            } else {
                displayName = api.util.StringHelper.capitalizeAll(displayName.replace(/-/g, " ").trim());
                return "<Unnamed " + displayName + ">";
            }
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

    export class DependantNamesAndIconView extends api.dom.DivEl {

        private wrapperDivEl: api.dom.DivEl;

        private iconImageEl: api.dom.ImgEl;

        private iconDivEl: api.dom.DivEl;

        private namesView: api.dom.SpanEl;

        private iconLabelEl: api.dom.SpanEl;

        private size: api.app.NamesAndIconViewSize

        constructor(size?: api.app.NamesAndIconViewSize) {
            super("names-and-icon-view");

            if (size) {
                this.size = size;
                this.addClass(api.app.NamesAndIconViewSize[size]);
            }

            this.wrapperDivEl = new api.dom.DivEl("wrapper");
            this.appendChild(this.wrapperDivEl);

            this.iconImageEl = new api.dom.ImgEl(null, "icon");
            this.wrapperDivEl.appendChild(this.iconImageEl);

            this.iconDivEl = new api.dom.DivEl("icon");
            this.wrapperDivEl.appendChild(this.iconDivEl);
            this.iconDivEl.hide();

            this.namesView = new api.dom.SpanEl();
            this.namesView.addClass("name-span");
            this.appendChild(this.namesView);

            this.iconLabelEl = new api.dom.SpanEl("icon-label");
            this.iconLabelEl.hide();
            this.appendChild(this.iconLabelEl);
        }

        setMainName(value: string): DependantNamesAndIconView {
            this.namesView.setHtml(value, true);
            return this;
        }

        setIconClass(value: string): DependantNamesAndIconView {
            this.iconDivEl.setClass("icon " + value);
            this.iconDivEl.getEl().setDisplay('inline-block');
            this.iconImageEl.hide();
            return this;
        }

        setIconUrl(value: string): DependantNamesAndIconView {
            this.iconImageEl.setSrc(value);
            this.iconDivEl.hide();
            this.iconImageEl.show();
            return this;
        }
    }
}