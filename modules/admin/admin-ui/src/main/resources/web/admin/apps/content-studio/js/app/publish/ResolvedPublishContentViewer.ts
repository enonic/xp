import "../../api.ts";

import ContentPath = api.content.ContentPath;
import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
import CompareStatus = api.content.CompareStatus;
import {ContentPublishItem} from "./ContentPublishItem";
import {DependantView} from "../view/DependantView";

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

    private dependantView: DependantView;

    private size: api.app.NamesAndIconViewSize;

    constructor(className?: string, size: api.app.NamesAndIconViewSize = api.app.NamesAndIconViewSize.small) {
        super(className);
        this.size = size;
        this.addClass("content-resolved-dependant-viewer");
    }

    setObject(object: M, relativePath: boolean = false) {
        super.setObject(object);

        this.dependantView = DependantView.create().
            item(object).build();
        
        this.render();
       
    }   

    getPreferredHeight(): number {
        return 50;
    }

    doRender() {
        this.removeChildren();
        this.appendChild(this.dependantView);
        return true;
    }
}
