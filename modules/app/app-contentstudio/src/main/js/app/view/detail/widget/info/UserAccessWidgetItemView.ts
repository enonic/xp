import '../../../../../api.ts';
import {WidgetItemView} from '../../WidgetItemView';

import Content = api.content.Content;
import ContentId = api.content.ContentId;
import Access = api.ui.security.acl.Access;
import AccessControlEntryView = api.ui.security.acl.AccessControlEntryView;
import UserAccessListView = api.ui.security.acl.UserAccessListView;
import UserAccessListItemView = api.ui.security.acl.UserAccessListItemView;
import Permission = api.security.acl.Permission;
import User = api.security.User;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import OpenEditPermissionsDialogEvent = api.content.event.OpenEditPermissionsDialogEvent;
import LoginResult = api.security.auth.LoginResult;
import i18n = api.util.i18n;

export class UserAccessWidgetItemView
    extends WidgetItemView {

    private contentId: ContentId;

    private accessListView: UserAccessListView;

    private headerEl: api.dom.SpanEl;

    private bottomEl: api.dom.AEl;

    private loginResult: LoginResult;// TODO: need to implement caching for current user value;

    private everyoneAccessValue: Access;

    public static debug: boolean = false;

    private static OPTIONS: any[] = [
        {value: Access.FULL, name: i18n('field.access.full')},
        {value: Access.PUBLISH, name: i18n('field.access.publish')},
        {value: Access.WRITE, name: i18n('field.access.write')},
        {value: Access.READ, name: i18n('field.access.read')},
        {value: Access.CUSTOM, name: i18n('field.access.custom')}
    ];

    constructor() {
        super('user-access-widget-item-view');
        this.accessListView = new UserAccessListView();
    }

    public setContentAndUpdateView(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        let contentId = item.getContentId();
        if (UserAccessWidgetItemView.debug) {
            console.debug('UserAccessWidgetItemView.setContentId: ', contentId);
        }
        this.contentId = contentId;
        return this.layout();
    }

    private layoutHeader(content: Content) {
        let entry = content.getPermissions().getEntry(api.security.RoleKeys.EVERYONE);
        this.everyoneAccessValue = null;

        if (this.hasChild(this.headerEl)) {
            this.removeChild(this.headerEl);
        }

        if (entry) {

            this.everyoneAccessValue = AccessControlEntryView.getAccessValueFromEntry(entry);
            let headerStr = `${entry.getPrincipalDisplayName()} ${this.getOptionName(this.everyoneAccessValue)} ${i18n(
                'field.access.item')}`;
            let headerStrEl = new api.dom.SpanEl('header-string').setHtml(headerStr);

            this.headerEl = new api.dom.DivEl('user-access-widget-header');

            this.headerEl.appendChild(new api.dom.DivEl('icon-menu4'));
            this.headerEl.appendChild(headerStrEl);
            this.prependChild(this.headerEl);
        }
    }

    private layoutBottom(content: Content) {

        if (this.hasChild(this.bottomEl)) {
            this.removeChild(this.bottomEl);
        }

        if (!content.isAnyPrincipalAllowed(this.loginResult.getPrincipals(),
                api.security.acl.Permission.WRITE_PERMISSIONS)) {
            return;
        }

        this.bottomEl = new api.dom.AEl('edit-permissions-link');
        this.bottomEl.setHtml(i18n('action.editPermissions'));

        this.appendChild(this.bottomEl);

        this.bottomEl.onClicked((event: MouseEvent) => {

            OpenEditPermissionsDialogEvent.create().applyContent(content).build().fire();

            event.stopPropagation();
            event.preventDefault();
            return false;
        });

    }

    private layoutList(content: Content): wemQ.Promise<boolean> {

        let deferred = wemQ.defer<boolean>();

        let request = new api.content.resource.GetEffectivePermissionsRequest(content.getContentId());

        request.sendAndParse().then((results: api.ui.security.acl.EffectivePermission[]) => {

            if (this.hasChild(this.accessListView)) {
                this.removeChild(this.accessListView);
            }
            let userAccessList = this.getUserAccessList(results);

            this.accessListView = new UserAccessListView();
            this.accessListView.setItemViews(userAccessList);
            this.appendChild(this.accessListView);

            deferred.resolve(true);
        }).done();

        return deferred.promise;

    }

    public layout(): wemQ.Promise<any> {
        if (UserAccessWidgetItemView.debug) {
            console.debug('UserAccessWidgetItemView.layout');
        }

        return super.layout().then(this.layoutUserAccess.bind(this));
    }

    private layoutUserAccess(): wemQ.Promise<any> {
        return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {

            this.loginResult = loginResult;
            if (this.contentId) {
                return new api.content.resource.GetContentByIdRequest(this.contentId).sendAndParse().then((content: Content) => {
                    if (content) {
                        this.layoutHeader(content);
                        return this.layoutList(content).then(() => {
                            this.layoutBottom(content);
                        });
                    }
                });
            }
        });
    }

    private getUserAccessList(results: api.ui.security.acl.EffectivePermission[]): UserAccessListItemView[] {

        return results.filter(item => item.getAccess() !== this.everyoneAccessValue &&
                                      item.getPermissionAccess().getCount() > 0).map((item: api.ui.security.acl.EffectivePermission) => {
            let view = new UserAccessListItemView();
            view.setObject(item);
            view.setCurrentUser(this.loginResult.getUser());
            return view;
        });
    }

    private getOptionName(access: Access): string {
        let currentOption = UserAccessWidgetItemView.OPTIONS.filter(option => {
            return option.value === access;
        });
        if (currentOption && currentOption.length > 0) {
            return currentOption[0].name;
        }
    }
}
