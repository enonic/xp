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
    public TOGGLE_SEARCH_PANEL: Action;

    private actions: api.ui.Action[] = [];

    constructor(grid: ContentTreeGrid) {
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

        this.actions.push(
            this.SHOW_NEW_CONTENT_DIALOG_ACTION,
            this.EDIT_CONTENT, this.DELETE_CONTENT,
            this.DUPLICATE_CONTENT, this.MOVE_CONTENT,
            this.SORT_CONTENT, this.PREVIEW_CONTENT
        );

        let previewHandler = (<PreviewContentAction>this.PREVIEW_CONTENT).getPreviewHandler();

        previewHandler.onPreviewStateChanged((value) => {
            this.PREVIEW_CONTENT.setEnabled(value);
        });

    }

    getAllActions(): api.ui.Action[] {
        return [...this.actions, this.PUBLISH_CONTENT, this.UNPUBLISH_CONTENT];
    }

    getAllActionsNoPublish(): api.ui.Action[] {
        return this.actions;
    }

    // tslint:disable-next-line:max-line-length
    updateActionsEnabledState(contentBrowseItems: ContentBrowseItem[], changes?: BrowseItemsChanges<ContentSummaryAndCompareStatus>): wemQ.Promise<BrowseItem<ContentSummaryAndCompareStatus>[]> {

        this.TOGGLE_SEARCH_PANEL.setVisible(false);

        let deferred = wemQ.defer<ContentBrowseItem[]>();

        let previewHandler = (<PreviewContentAction>this.PREVIEW_CONTENT).getPreviewHandler();

        let parallelPromises: wemQ.Promise<any>[] = [
            previewHandler.updateState(contentBrowseItems, changes),
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

        this.PUBLISH_CONTENT.setEnabled(false);
        this.PUBLISH_CONTENT.setVisible(true);
        this.PUBLISH_TREE_CONTENT.setEnabled(false);
        this.UNPUBLISH_CONTENT.setEnabled(false);
        this.UNPUBLISH_CONTENT.setVisible(false);
    }

    private resetDefaultActionsMultipleItemsSelected(contentBrowseItems: ContentBrowseItem[]) {
        let contentSummaries: ContentSummary[] = contentBrowseItems.map((elem: ContentBrowseItem) => {
            return elem.getModel().getContentSummary();
        });

        let treePublishEnabled = true;
        let unpublishEnabled = true;

        let eachOnline = contentBrowseItems.every((browseItem) => {
            return this.isOnline(browseItem.getModel().getCompareStatus());
        });

        let anyPublished = contentBrowseItems.some((browseItem) => {
            return this.isPublished(browseItem.getModel().getCompareStatus());
        });

        const publishEnabled = !eachOnline;
        if (this.isEveryLeaf(contentSummaries)) {
            treePublishEnabled = false;
            unpublishEnabled = anyPublished;
        } else if (this.isOneNonLeaf(contentSummaries)) {
            unpublishEnabled = anyPublished;
        } else if (this.isNonLeafInMany(contentSummaries)) {
            unpublishEnabled = anyPublished;
        }

        this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(contentSummaries.length < 2);
        this.EDIT_CONTENT.setEnabled(this.anyEditable(contentSummaries));
        this.DELETE_CONTENT.setEnabled(this.anyDeletable(contentSummaries));
        this.DUPLICATE_CONTENT.setEnabled(false);
        this.MOVE_CONTENT.setEnabled(true);
        this.SORT_CONTENT.setEnabled(contentSummaries.length === 1);

        this.PUBLISH_CONTENT.setEnabled(publishEnabled);
        this.PUBLISH_TREE_CONTENT.setEnabled(treePublishEnabled);
        this.UNPUBLISH_CONTENT.setEnabled(unpublishEnabled);
        this.PUBLISH_CONTENT.setVisible(publishEnabled);
        this.UNPUBLISH_CONTENT.setVisible(!publishEnabled);
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

    private isPublished(status: api.content.CompareStatus): boolean {
        return status !== api.content.CompareStatus.NEW && status !== api.content.CompareStatus.UNKNOWN;
    }

    private isOnline(status: api.content.CompareStatus): boolean {
        return status === api.content.CompareStatus.EQUAL;
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
        return new api.content.resource.GetPermittedActionsRequest().addPermissionsToBeChecked(Permission.CREATE).sendAndParse().
            then((allowedPermissions: Permission[]) => {
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
}
