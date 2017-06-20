import '../../../api.ts';
import {ContentTreeGrid} from '../ContentTreeGrid';
import {ToggleSearchPanelAction} from './ToggleSearchPanelAction';
import {ShowNewContentDialogAction} from './ShowNewContentDialogAction';
import {PreviewContentAction} from './PreviewContentAction';
import {EditContentAction} from './EditContentAction';
import {DeleteContentAction} from './DeleteContentAction';
import {DuplicateContentAction} from './DuplicateContentAction';
import {MoveContentAction} from './MoveContentAction';
import {SortContentAction} from './SortContentAction';
import {PublishContentAction} from './PublishContentAction';
import {PublishTreeContentAction} from './PublishTreeContentAction';
import {UnpublishContentAction} from './UnpublishContentAction';
import {ContentBrowseItem} from '../ContentBrowseItem';
import {PreviewContentHandler} from './handler/PreviewContentHandler';
import {UndoPendingDeleteContentAction} from './UndoPendingDeleteContentAction';
import {CreateIssueAction} from './CreateIssueAction';

import Action = api.ui.Action;
import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
import BrowseItem = api.app.browse.BrowseItem;
import BrowseItemsChanges = api.app.browse.BrowseItemsChanges;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import Content = api.content.Content;
import PermissionHelper = api.security.acl.PermissionHelper;
import AccessControlEntry = api.security.acl.AccessControlEntry;
import AccessControlList = api.security.acl.AccessControlList;
import ContentId = api.content.ContentId;
import ContentAccessControlList = api.security.acl.ContentAccessControlList;
import Permission = api.security.acl.Permission;
import GetContentByPathRequest = api.content.resource.GetContentByPathRequest;

export class ContentTreeGridActions implements TreeGridActions<ContentSummaryAndCompareStatus> {

    public SHOW_NEW_CONTENT_DIALOG_ACTION: Action;
    public PREVIEW_CONTENT: Action;
    public EDIT_CONTENT: Action;
    public DELETE_CONTENT: Action;
    public DUPLICATE_CONTENT: Action;
    public MOVE_CONTENT: Action;
    public SORT_CONTENT: Action;
    public PUBLISH_CONTENT: Action;
    public PUBLISH_TREE_CONTENT: Action;
    public UNPUBLISH_CONTENT: Action;
    public CREATE_ISSUE: Action;
    public TOGGLE_SEARCH_PANEL: Action;
    public UNDO_PENDING_DELETE: Action;

    private actions: api.ui.Action[] = [];

    private grid: ContentTreeGrid;

    constructor(grid: ContentTreeGrid) {
        this.grid = grid;
        this.TOGGLE_SEARCH_PANEL = new ToggleSearchPanelAction();

        this.SHOW_NEW_CONTENT_DIALOG_ACTION = new ShowNewContentDialogAction(grid);
        this.PREVIEW_CONTENT = new PreviewContentAction(grid);
        this.EDIT_CONTENT = new EditContentAction(grid);
        this.DELETE_CONTENT = new DeleteContentAction(grid);
        this.DUPLICATE_CONTENT = new DuplicateContentAction(grid);
        this.MOVE_CONTENT = new MoveContentAction(grid);
        this.SORT_CONTENT = new SortContentAction(grid);
        this.PUBLISH_CONTENT = new PublishContentAction(grid);
        this.PUBLISH_TREE_CONTENT = new PublishTreeContentAction(grid);
        this.UNPUBLISH_CONTENT = new UnpublishContentAction(grid);
        this.CREATE_ISSUE = new CreateIssueAction(grid);
        this.UNDO_PENDING_DELETE = new UndoPendingDeleteContentAction(grid);

        this.actions.push(
            this.SHOW_NEW_CONTENT_DIALOG_ACTION,
            this.EDIT_CONTENT, this.DELETE_CONTENT,
            this.DUPLICATE_CONTENT, this.MOVE_CONTENT,
            this.SORT_CONTENT, this.PREVIEW_CONTENT,
            this.UNDO_PENDING_DELETE
        );

        this.getPreviewHandler().onPreviewStateChanged((value) => {
            this.PREVIEW_CONTENT.setEnabled(value);
        });

    }

    getPreviewHandler(): PreviewContentHandler {
        return (<PreviewContentAction>this.PREVIEW_CONTENT).getPreviewHandler();
    }

    private getDefaultVisibleActions(): api.ui.Action[] {
        return this.actions.filter(action => action !== this.UNDO_PENDING_DELETE).concat(this.PUBLISH_CONTENT);
    }

    getAllActions(): api.ui.Action[] {
        return [...this.actions, this.PUBLISH_CONTENT, this.UNPUBLISH_CONTENT];
    }

    getAllActionsNoPublish(): api.ui.Action[] {
        return this.actions;
    }

    // tslint:disable-next-line:max-line-length
    updateActionsEnabledState(contentBrowseItems: ContentBrowseItem[],
                              changes?: BrowseItemsChanges<ContentSummaryAndCompareStatus>): wemQ.Promise<BrowseItem<ContentSummaryAndCompareStatus>[]> {

        let deferred = wemQ.defer<ContentBrowseItem[]>();

        if (!!changes && changes.getAdded().length == 0 && changes.getRemoved().length == 0) {
            deferred.resolve(contentBrowseItems);
            return deferred.promise;
        }

        this.TOGGLE_SEARCH_PANEL.setVisible(false);

        let parallelPromises: wemQ.Promise<any>[] = [
            this.getPreviewHandler().updateState(contentBrowseItems, changes),
            this.doUpdateActionsEnabledState(contentBrowseItems)
        ];

        wemQ.all(parallelPromises).spread<void>(() => {
            deferred.resolve(contentBrowseItems);
            return wemQ(null);
        }).catch(api.DefaultErrorHandler.handle);

        return deferred.promise;
    }

    private resetDefaultActionsNoItemsSelected() {
        this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
        this.EDIT_CONTENT.setEnabled(false);
        this.DELETE_CONTENT.setEnabled(false);
        this.DUPLICATE_CONTENT.setEnabled(false);
        this.MOVE_CONTENT.setEnabled(false);
        this.SORT_CONTENT.setEnabled(false);

        this.PUBLISH_TREE_CONTENT.setEnabled(false);
        this.PUBLISH_CONTENT.setEnabled(false);
        this.UNPUBLISH_CONTENT.setEnabled(false);

        this.UNPUBLISH_CONTENT.setVisible(false);
        this.UNDO_PENDING_DELETE.setVisible(false);

        this.CREATE_ISSUE.setEnabled(false);

        this.showDefaultActions();
    }

    private showDefaultActions() {
        this.getDefaultVisibleActions().forEach(action => action.setVisible(true));
    }

    private resetDefaultActionsMultipleItemsSelected(contentBrowseItems: ContentBrowseItem[]) {
        let contentSummaries: ContentSummary[] = contentBrowseItems.map((elem: ContentBrowseItem) => {
            return elem.getModel().getContentSummary();
        });

        let treePublishEnabled = true;
        let unpublishEnabled = true;

        let allAreOnline = contentBrowseItems.length > 0;
        let allArePendingDelete = contentBrowseItems.length > 0;
        let someArePublished = false;

        contentBrowseItems.forEach((browseItem) => {
            let content = browseItem.getModel();

            if (allAreOnline && !content.isOnline()) {
                allAreOnline = false;
            }
            if (allArePendingDelete && !content.isPendingDelete()) {
                allArePendingDelete = false;
            }
            if (!someArePublished && content.isPublished()) {
                someArePublished = true;
            }
        });

        const publishEnabled = !allAreOnline;
        if (this.isEveryLeaf(contentSummaries)) {
            treePublishEnabled = false;
            unpublishEnabled = someArePublished;
        } else if (this.isOneNonLeaf(contentSummaries)) {
            unpublishEnabled = someArePublished;
        } else if (this.isNonLeafInMany(contentSummaries)) {
            unpublishEnabled = someArePublished;
        }

        const createIssueEnabled = !allAreOnline || this.isNonLeafInMany(contentSummaries);

        this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(contentSummaries.length < 2);
        this.EDIT_CONTENT.setEnabled(this.anyEditable(contentSummaries));
        this.DELETE_CONTENT.setEnabled(this.anyDeletable(contentSummaries));
        this.DUPLICATE_CONTENT.setEnabled(contentSummaries.length === 1);
        this.MOVE_CONTENT.setEnabled(!this.isAllItemsSelected(contentBrowseItems.length));
        this.SORT_CONTENT.setEnabled(contentSummaries.length === 1 && contentSummaries[0].hasChildren());

        this.PUBLISH_CONTENT.setEnabled(publishEnabled);
        this.PUBLISH_TREE_CONTENT.setEnabled(treePublishEnabled);
        this.UNPUBLISH_CONTENT.setEnabled(unpublishEnabled);

        this.CREATE_ISSUE.setEnabled(createIssueEnabled);

        this.SHOW_NEW_CONTENT_DIALOG_ACTION.setVisible(!allArePendingDelete);
        this.MOVE_CONTENT.setVisible(!allArePendingDelete);
        this.SORT_CONTENT.setVisible(!allArePendingDelete);
        this.DELETE_CONTENT.setVisible(!allArePendingDelete);

        if (allArePendingDelete) {
            this.getAllActions().forEach(action => action.setVisible(false));
        } else {
            this.getAllActionsNoPublish().forEach(action => action.setVisible(true));
            this.UNPUBLISH_CONTENT.setVisible(unpublishEnabled);
        }
        this.PUBLISH_CONTENT.setVisible(publishEnabled);
        this.UNDO_PENDING_DELETE.setVisible(allArePendingDelete);
    }

    private isEveryLeaf(contentSummaries: ContentSummary[]): boolean {
        return contentSummaries.every((obj: ContentSummary) => !obj.hasChildren());
    }

    private isOneNonLeaf(contentSummaries: ContentSummary[]): boolean {
        return contentSummaries.length === 1 && contentSummaries[0].hasChildren();
    }

    private isNonLeafInMany(contentSummaries: ContentSummary[]): boolean {
        return contentSummaries.length > 1 && contentSummaries.some((obj: ContentSummary) => obj.hasChildren());
    }

    private doUpdateActionsEnabledState(contentBrowseItems: ContentBrowseItem[]): wemQ.Promise<any> {
        switch (contentBrowseItems.length) {
        case 0:
            return this.updateActionsByPermissionsNoItemsSelected();
        case 1:
            return this.updateActionsByPermissionsSingleItemSelected(contentBrowseItems);
        default:
            return this.updateActionsByPermissionsMultipleItemsSelected(contentBrowseItems);
        }
    }

    private updateActionsByPermissionsNoItemsSelected(): wemQ.Promise<any> {
        return new api.content.resource.GetPermittedActionsRequest().addPermissionsToBeChecked(Permission.CREATE).sendAndParse().then(
            (allowedPermissions: Permission[]) => {
                this.resetDefaultActionsNoItemsSelected();

                let canCreate = allowedPermissions.indexOf(Permission.CREATE) > -1;

                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(canCreate);
            });
    }

    private updateActionsByPermissionsSingleItemSelected(contentBrowseItems: ContentBrowseItem[]): wemQ.Promise<any> {
        let selectedItem = contentBrowseItems[0].getModel().getContentSummary();

        return this.checkIsChildrenAllowedByContentType(selectedItem).then((contentTypeAllowsChildren: boolean) => {
            return this.updateActionsByPermissionsMultipleItemsSelected(contentBrowseItems, contentTypeAllowsChildren).then(() => {
                return this.updateCanDuplicateActionSingleItemSelected(selectedItem);
            });
        });
    }

    private updateActionsByPermissionsMultipleItemsSelected(contentBrowseItems: ContentBrowseItem[],
                                                            contentTypesAllowChildren: boolean = true): wemQ.Promise<any> {
        return new api.content.resource.GetPermittedActionsRequest().
            addContentIds(...contentBrowseItems.map(contentBrowseItem => contentBrowseItem.getModel().getContentId())).
            addPermissionsToBeChecked(Permission.CREATE, Permission.DELETE, Permission.PUBLISH).
            sendAndParse().
            then((allowedPermissions: Permission[]) => {
                this.resetDefaultActionsMultipleItemsSelected(contentBrowseItems);

                let canCreate = allowedPermissions.indexOf(Permission.CREATE) > -1;

                let canDelete = allowedPermissions.indexOf(Permission.DELETE) > -1;

                let canPublish = allowedPermissions.indexOf(Permission.PUBLISH) > -1;

                if (!contentTypesAllowChildren || !canCreate) {
                    this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                    this.SORT_CONTENT.setEnabled(false);
                }

                if (!canDelete) {
                    this.DELETE_CONTENT.setEnabled(false);
                    this.MOVE_CONTENT.setEnabled(false);
                }

                if (!canPublish) {
                    this.PUBLISH_CONTENT.setEnabled(false);
                    this.PUBLISH_TREE_CONTENT.setEnabled(false);
                    this.UNPUBLISH_CONTENT.setEnabled(false);

                    this.CREATE_ISSUE.setEnabled(false);
                }
            });
    }

    private checkIsChildrenAllowedByContentType(contentSummary: ContentSummary): wemQ.Promise<Boolean> {
        let deferred = wemQ.defer<boolean>();

        new api.schema.content.GetContentTypeByNameRequest(contentSummary.getType()).sendAndParse().then(
            (contentType: api.schema.content.ContentType) => {
                return deferred.resolve(contentType && contentType.isAllowChildContent());
            });

        return deferred.promise;
    }

    private anyEditable(contentSummaries: api.content.ContentSummary[]): boolean {
        return contentSummaries.some((content) => {
            return !!content && content.isEditable();
        });
    }

    private anyDeletable(contentSummaries: api.content.ContentSummary[]): boolean {
        return contentSummaries.some((content) => {
            return !!content && content.isDeletable();
        });
    }

    private updateCanDuplicateActionSingleItemSelected(selectedItem: ContentSummary) {
        // Need to check if parent allows content creation
        new GetContentByPathRequest(selectedItem.getPath().getParentPath()).sendAndParse().then((content: Content) => {
            new api.content.resource.GetPermittedActionsRequest().addContentIds(content.getContentId()).addPermissionsToBeChecked(
                Permission.CREATE).sendAndParse().then((allowedPermissions: Permission[]) => {
                let canDuplicate = allowedPermissions.indexOf(Permission.CREATE) > -1;
                this.DUPLICATE_CONTENT.setEnabled(canDuplicate);
            });

        });
    }

    private isAllItemsSelected(items: number): boolean {
        return items === this.grid.getRoot().getDefaultRoot().treeToList(false, false).length;
    }
}
