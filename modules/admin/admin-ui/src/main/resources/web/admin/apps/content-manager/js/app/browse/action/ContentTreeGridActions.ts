module app.browse.action {

    import Action = api.ui.Action;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import Content = api.content.Content;
    import PermissionHelper = api.security.acl.PermissionHelper;
    import AccessControlEntry = api.security.acl.AccessControlEntry;
    import AccessControlList = api.security.acl.AccessControlList;

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

        }

        getAllActions(): api.ui.Action[] {
            return this.actions;
        }

        updateActionsEnabledState(contentBrowseItems: BrowseItem<ContentSummaryAndCompareStatus>[]): wemQ.Promise<BrowseItem<ContentSummaryAndCompareStatus>[]> {
            this.TOGGLE_SEARCH_PANEL.setVisible(false);

            var contentSummaries: ContentSummary[] = contentBrowseItems.map((elem: BrowseItem<ContentSummaryAndCompareStatus>) => {
                return elem.getModel().getContentSummary();
            });

            var deferred = wemQ.defer<BrowseItem<ContentSummaryAndCompareStatus>[]>();

            switch (contentBrowseItems.length) {
            case 0:
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
                this.EDIT_CONTENT.setEnabled(false);
                this.DELETE_CONTENT.setEnabled(false);
                this.DUPLICATE_CONTENT.setEnabled(false);
                this.MOVE_CONTENT.setEnabled(false);
                this.SORT_CONTENT.setEnabled(false);
                this.PREVIEW_CONTENT.setEnabled(false);
                this.PUBLISH_CONTENT.setEnabled(false);

                var promise = this.updateActionsEnabledStateByPermissions(contentSummaries);

                promise.then<void>(() => {
                    deferred.resolve(contentBrowseItems);
                    return wemQ(null);
                }).done();
                break;
            case 1:
                var contentSummary = contentSummaries[0];
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
                this.EDIT_CONTENT.setEnabled(!contentSummary ? false : contentSummary.isEditable());
                this.DELETE_CONTENT.setEnabled(!contentSummary ? false : contentSummary.isDeletable());
                this.DUPLICATE_CONTENT.setEnabled(true);
                this.MOVE_CONTENT.setEnabled(true);
                this.PUBLISH_CONTENT.setEnabled(true);
                this.SORT_CONTENT.setEnabled(true);
                this.PREVIEW_CONTENT.setEnabled(false);
                var parallelPromises: wemQ.Promise<any>[] = [
                    new api.content.page.IsRenderableRequest(contentSummary.getContentId()).sendAndParse().
                        then((renderable: boolean) => {
                            this.PREVIEW_CONTENT.setEnabled(renderable);
                            if (contentBrowseItems.length > 0) {
                                contentBrowseItems[0].setRenderable(renderable);
                            }
                        }),
                    this.updateActionsEnabledStateByPermissions(contentSummaries)
                ];
                wemQ.all(parallelPromises).spread<void>(() => {
                    deferred.resolve(contentBrowseItems);
                    return wemQ(null);
                }).done();
                break;
            default:
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                this.PREVIEW_CONTENT.setEnabled(false);
                this.EDIT_CONTENT.setEnabled(this.anyEditable(contentSummaries));
                this.DELETE_CONTENT.setEnabled(this.anyDeletable(contentSummaries));
                this.DUPLICATE_CONTENT.setEnabled(false);
                this.MOVE_CONTENT.setEnabled(true);
                this.SORT_CONTENT.setEnabled(false);
                this.PUBLISH_CONTENT.setEnabled(true);
                var promise = this.updateActionsEnabledStateByPermissions(contentSummaries);

                promise.then<void>(() => {
                    deferred.resolve(contentBrowseItems);
                    return wemQ(null);
                }).done();
            }
            return deferred.promise;
        }

        private updateActionsEnabledStateByPermissions(contentSummaries: ContentSummary[]): wemQ.Promise<any> {

            return new api.security.auth.IsAuthenticatedRequest().
                sendAndParse().
                then((loginResult: api.security.auth.LoginResult) => {
                    if (contentSummaries.length == 0) {
                        new api.content.GetContentRootPermissionsRequest().
                            sendAndParse().
                            then((accessControlList: AccessControlList) => {
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
                        var hasParentCreatePermission = true;

                        var parallelPromises: wemQ.Promise<any>[] = [];
                        var nestedParallelPromises: wemQ.Promise<any>[] = [];

                        for (var i = 0; i < contentSummaries.length; i++) {

                            var contentSummary = contentSummaries[i];

                            if (contentSummaries.length == 1) { // Unnecessary request for multiple selection
                                parallelPromises.push(
                                    new api.schema.content.GetContentTypeByNameRequest(contentSummary.getType()).
                                        sendAndParse().
                                        then((contentType: api.schema.content.ContentType) => {
                                            contentTypesAllowChildren =
                                                contentTypesAllowChildren && (contentType && contentType.isAllowChildContent());
                                        }))
                            }
                            parallelPromises.push(
                                new api.content.GetContentPermissionsByIdRequest(contentSummary.getContentId()).
                                    sendAndParse().
                                    then((accessControlList: AccessControlList) => {
                                        hasCreatePermission = hasCreatePermission &&
                                                              PermissionHelper.hasPermission(api.security.acl.Permission.CREATE,
                                                                  loginResult,
                                                                  accessControlList);
                                        hasDeletePermission = hasDeletePermission &&
                                                              PermissionHelper.hasPermission(api.security.acl.Permission.DELETE,
                                                                  loginResult,
                                                                  accessControlList);
                                        hasPublishPermission = hasDeletePermission &&
                                                               PermissionHelper.hasPermission(api.security.acl.Permission.PUBLISH,
                                                                   loginResult,
                                                                   accessControlList);
                                    }))
                            if (contentSummaries.length == 1) { // Unnecessary request for multiple selection
                                if (contentSummary.hasParent()) {
                                    parallelPromises.push(
                                        new api.content.GetContentByPathRequest(contentSummary.getPath().getParentPath()).
                                            sendAndParse().
                                            then((parent: api.content.Content) => {
                                                nestedParallelPromises.push(
                                                    new api.content.GetContentPermissionsByIdRequest(parent.getContentId()).
                                                        sendAndParse().
                                                        then((accessControlList: AccessControlList) => {
                                                            hasParentCreatePermission = hasParentCreatePermission &&
                                                                                        PermissionHelper.hasPermission(api.security.acl.Permission.CREATE,
                                                                                            loginResult,
                                                                                            accessControlList);
                                                        }))
                                            }))
                                } else {
                                    parallelPromises.push(
                                        new api.content.GetContentRootPermissionsRequest().
                                            sendAndParse().
                                            then((accessControlList: AccessControlList) => {
                                                hasParentCreatePermission = hasParentCreatePermission &&
                                                                            PermissionHelper.hasPermission(api.security.acl.Permission.CREATE,
                                                                                loginResult,
                                                                                accessControlList);
                                            }))
                                }
                            }
                        }

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
                            wemQ.all(nestedParallelPromises).spread(() => {
                                if (!hasParentCreatePermission) {
                                    this.DUPLICATE_CONTENT.setEnabled(false);
                                }
                                return wemQ(null);
                            }).done();
                            return wemQ(null);
                        }).done();
                    }


                });
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
}
