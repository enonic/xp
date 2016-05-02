import "../../api.ts";

export class DependantView extends api.app.NamesAndIconView {

    constructor(builder: DependantViewBuilder) {
        super(builder);
        this.addClass("dependant-view");

        var iconUrl = this.resolveIconUrl(builder.object);

        this.setMainName(this.resolveDisplayName(builder.object));

        if (builder.object.getType && builder.object.getType().isImage()) {

            this.setIconClass("image");

        } else if (iconUrl) {

            this.setIconUrl(iconUrl);
        }
    }

    private resolveDisplayName(object: any): string {
        var contentName = object.getName(),
            invalid = !object.isValid() || !object.getDisplayName() || contentName.isUnnamed();

        this.toggleClass("invalid", invalid);

        var pendingDelete = false;

        if (object.getCompareStatus ) { //TODO: use one data model
            pendingDelete = (api.content.CompareStatus.PENDING_DELETE == object.getCompareStatus())
        } else if(object.getContentState) {
            pendingDelete = object.getContentState().isPendingDelete();
        }

        this.toggleClass("pending-delete", pendingDelete);
        return object.getPath().toString();
    }

    private resolveIconUrl(object: any): string {
        return new api.content.ContentIconUrlResolver().setContent(object).resolve();
    }

    static create(): DependantViewBuilder {
        return new DependantViewBuilder();
    }
}

export class DependantViewBuilder extends api.app.NamesAndIconViewBuilder {

    object: any; //TODO: to make work with one type of data

    build(): DependantView {

        return new DependantView(this);
    }

    item(item: any): DependantViewBuilder {
        this.object = item;
        return this;
    }
}
