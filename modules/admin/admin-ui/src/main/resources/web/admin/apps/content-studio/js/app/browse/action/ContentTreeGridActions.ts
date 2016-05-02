import "../../../api.ts";

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
import {ContentTreeGrid} from "../ContentTreeGrid";
import {ToggleSearchPanelAction} from "./ToggleSearchPanelAction";
import {ShowNewContentDialogAction} from "./ShowNewContentDialogAction";
import {PreviewContentAction} from "./PreviewContentAction";
import {EditContentAction} from "./EditContentAction";
import {DeleteContentAction} from "./DeleteContentAction";
import {DuplicateContentAction} from "./DuplicateContentAction";
import {MoveContentAction} from "./MoveContentAction";
import {SortContentAction} from "./SortContentAction";
import {PublishContentAction} from "./PublishContentAction";
import {ContentBrowseItem} from "../ContentBrowseItem";

export class ContentTreeGridActions implements TreeGridActions<ContentSummaryAndCompareStatus> {

    public SHOW_NEW_CONTENT_DIALOG_ACTION: Action;
    public PREVIEW_CONTENT: Action;
    public EDIT_CONTENT: Action;
    public DELETE_CONTENT: Action;
    public DUPLICATE_CONTENT: Action;
    public MOVE_CONTENT: Action;
    public SORT_CONTENT: Action;
    public PUBLISH_CONTENT: Action;
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

        this.actions.push(
            this.SHOW_NEW_CONTENT_DIALOG_ACTION,
            this.EDIT_CONTENT, this.DELETE_CONTENT,
            this.DUPLICATE_CONTENT, this.MOVE_CONTENT,
            this.SORT_CONTENT, this.PREVIEW_CONTENT,
            this.PUBLISH_CONTENT
        );

        let previewHandler = (<PreviewContentAction>this.PREVIEW_CONTENT).getPreviewHandler();

        previewHandler.onPreviewStateChanged((value) => {
            this.PREVIEW_CONTENT.setEnabled(value);
        })

    }

    getAllActions(): api.ui.Action[] {
        return this.actions;
    }

    updateActionsEnabledState(contentBrowseItems: ContentBrowseItem[],
                              changes?: BrowseItemsChanges<ContentSummaryAndCompareStatus>): wemQ.Promise<BrowseItem<ContentSummaryAndCompareStatus>[]> {

        this.TOGGLE_SEARCH_PANEL.setVisible(false);

        let contentSummaries: ContentSummary[] = contentBrowseItems.map((elem: ContentBrowseItem) => {
            return elem.getModel().getContentSummary();
        });

        let deferred = wemQ.defer<ContentBrowseItem[]>();

        let parallelPromises: wemQ.Promise<any>[];

        let previewHandler = (<PreviewContentAction>this.PREVIEW_CONTENT).getPreviewHandler();

        switch (contentBrowseItems.length) {
        case 0:
            this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
            this.EDIT_CONTENT.setEnabled(false);
            this.DELETE_CONTENT.setEnabled(false);
            this.DUPLICATE_CONTENT.setEnabled(false);
            this.MOVE_CONTENT.setEnabled(false);
            this.SORT_CONTENT.setEnabled(false);
            this.PUBLISH_CONTENT.setEnabled(false);

            parallelPromises = [
                previewHandler.updateState(contentBrowseItems, changes),
                this.updateActionsEnabledStateByPermissions(contentBrowseItems)
            ];

            wemQ.all(parallelPromises).spread<void>(() => {
                deferred.resolve(contentBrowseItems);
                return wemQ(null);
            }).catch(api.DefaultErrorHandler.handle);
            break;
        case 1:
            let contentSummary = contentSummaries[0];
            this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
            this.EDIT_CONTENT.setEnabled(!contentSummary ? false : contentSummary.isEditable());
            this.DELETE_CONTENT.setEnabled(!contentSummary ? false : contentSummary.isDeletable());
            this.DUPLICATE_CONTENT.setEnabled(true);
            this.MOVE_CONTENT.setEnabled(true);
            this.PUBLISH_CONTENT.setEnabled(true);
            this.SORT_CONTENT.setEnabled(true);
            // this.PREVIEW_CONTENT.setEnabled(false);
            parallelPromises = [
                previewHandler.updateState(contentBrowseItems, changes),
                this.updateActionsEnabledStateByPermissions(contentBrowseItems)
            ];
            wemQ.all(parallelPromises).spread<void>(() => {
                deferred.resolve(contentBrowseItems);
                return wemQ(null);
            }).catch(api.DefaultErrorHandler.handle);
            break;
        default:
            this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
            //   this.PREVIEW_CONTENT.setEnabled(false);
            this.EDIT_CONTENT.setEnabled(this.anyEditable(contentSummaries));
            this.DELETE_CONTENT.setEnabled(this.anyDeletable(contentSummaries));
            this.DUPLICATE_CONTENT.setEnabled(false);
            this.MOVE_CONTENT.setEnabled(true);
            this.SORT_CONTENT.setEnabled(false);
            this.PUBLISH_CONTENT.setEnabled(true);
            parallelPromises = [
                previewHandler.updateState(contentBrowseItems, changes),
                this.updateActionsEnabledStateByPermissions(contentBrowseItems)
            ];
            wemQ.all(parallelPromises).spread<void>(() => {
                deferred.resolve(contentBrowseItems);
                return wemQ(null);
            }).catch(api.DefaultErrorHandler.handle);
        }
        return deferred.promise;
    }

    private updateActionsEnabledStateByPermissions(contentBrowseItems: ContentBrowseItem[]): wemQ.Promise<any> {

        // use Array.reduce, remember pevious permissions
        return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
            if (contentBrowseItems.length === 0) {
                new api.content.GetContentRootPermissionsRequest().sendAndParse().then((accessControlList: AccessControlList) => {
                    var hasCreatePermission =
                        PermissionHelper.hasPermission(api.security.acl.Permission.CREATE, loginResult, accessControlList);
                    if (!hasCreatePermission) {
                        this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                    }
                })
            } else {

                var contentTypesAllowChildren = true;
                var hasCreatePermission = true;
                var hasDeletePermission = true;
                var hasPublishPermission = true;

                function updatePermissions(acl: AccessControlList) {
                    hasCreatePermission = hasCreatePermission &&
                                          PermissionHelper.hasPermission(api.security.acl.Permission.CREATE,
                                              loginResult, acl);
                    hasDeletePermission = hasDeletePermission &&
                                          PermissionHelper.hasPermission(api.security.acl.Permission.DELETE,
                                              loginResult, acl);
                    hasPublishPermission = hasPublishPermission &&
                                           PermissionHelper.hasPermission(api.security.acl.Permission.PUBLISH,
                                               loginResult, acl);
                }

                var parallelPromises: wemQ.Promise<any>[] = [];

                if (contentBrowseItems.length == 1) { // Unnecessary requests for multiple selection

                    let contentBrowseItem = contentBrowseItems[0];
                    let contentSummary = contentBrowseItem.getModel().getContentSummary();

                    this.checkIsChildrenAllowedByPermissions(contentSummary).then((result: boolean) => {
                        contentTypesAllowChildren = result;
                    });

                    this.checkIsDuplicateAllowedByPermissions(contentSummary, loginResult).then((result: boolean) => {
                        this.DUPLICATE_CONTENT.setEnabled(result);
                    });
                }

                contentBrowseItems.forEach((contentBrowseItem: ContentBrowseItem, index: number) => {
                    let accessControlList = contentBrowseItem.getAccessControlList();
                    if (accessControlList) {
                        updatePermissions(accessControlList);
                    } else {
                        let contentSummary = contentBrowseItem.getModel();

                        parallelPromises.push(
                            new api.content.GetContentPermissionsByIdRequest(contentSummary.getContentId()).sendAndParse().then(
                                (accessControlList: AccessControlList) => {
                                    contentBrowseItem.setAccessControlList(accessControlList);
                                    updatePermissions(accessControlList);
                                }).catch(api.DefaultErrorHandler.handle));
                    }
                });

                wemQ.all(parallelPromises).spread(() => {
                    if (!contentTypesAllowChildren || !hasCreatePermission) {
                        this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                        this.SORT_CONTENT.setEnabled(false);
                    }
                    if (!hasDeletePermission) {
                        this.DELETE_CONTENT.setEnabled(false);
                        this.MOVE_CONTENT.setEnabled(false);
                    }
                    if (!hasPublishPermission) {
                        this.PUBLISH_CONTENT.setEnabled(false);
                    }
                    return wemQ(null);
                }).done();
            }


        });
    }

    private checkIsChildrenAllowedByPermissions(contentSummary: ContentSummary): wemQ.Promise<Boolean> {
        var deferred = wemQ.defer<boolean>();

        new api.schema.content.GetContentTypeByNameRequest(contentSummary.getType()).sendAndParse().then(
            (contentType: api.schema.content.ContentType) => {
                return deferred.resolve(contentType && contentType.isAllowChildContent());
            });

        return deferred.promise;
    }

    private checkIsDuplicateAllowedByPermissions(contentSummary: ContentSummary,
                                                 loginResult: api.security.auth.LoginResult): wemQ.Promise<Boolean> {
        var deferred = wemQ.defer<boolean>();

        if (contentSummary.hasParent()) {
            new api.content.GetContentByPathRequest(contentSummary.getPath().getParentPath()).sendAndParse().then(
                (parent: api.content.Content) => {

                    deferred.resolve(PermissionHelper.hasPermission(api.security.acl.Permission.CREATE,
                        loginResult,
                        parent.getPermissions()));
                })
        } else {
            new api.content.GetContentRootPermissionsRequest().sendAndParse().then((accessControlList: AccessControlList) => {

                deferred.resolve(PermissionHelper.hasPermission(api.security.acl.Permission.CREATE,
                    loginResult,
                    accessControlList));
            })
        }

        return deferred.promise;
    }

    private anyEditable(contentSummaries: api.content.ContentSummary[]): boolean {
        for (var i = 0; i < contentSummaries.length; i++) {
            var content: api.content.ContentSummary = contentSummaries[i];
            if (!!content && content.isEditable()) {
                return true;
            }
        }
        return false;
    }

    private anyDeletable(contentSummaries: api.content.ContentSummary[]): boolean {
        for (var i = 0; i < contentSummaries.length; i++) {
            var content: api.content.ContentSummary = contentSummaries[i];
            if (!!content && content.isDeletable()) {
                return true;
            }
        }
        return false;
    }
}
