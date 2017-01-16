import "../../api.ts";
import {ContentPermissionsApplyEvent} from "./ContentPermissionsApplyEvent";

import Content = api.content.Content;
import AccessControlComboBox = api.ui.security.acl.AccessControlComboBox;
import AccessControlEntry = api.security.acl.AccessControlEntry;
import AccessControlList = api.security.acl.AccessControlList;
import ContentPath = api.content.ContentPath;

export class EditPermissionsDialog extends api.ui.dialog.ModalDialog {

    private contentId: ContentId;

    private contentPath: ContentPath;

    private displayName: string;

    private permissions: AccessControlList;

    private inheritPermissions: boolean;

    private overwritePermissions: boolean;

    private immediateApply: boolean;

    private parentPermissions: AccessControlEntry[];

    private originalValues: AccessControlEntry[];

    private originalInherit: boolean;

    private originalOverwrite: boolean;

    private inheritPermissionsCheck: api.ui.Checkbox;

    private overwriteChildPermissionsCheck: api.ui.Checkbox;

    private comboBox: AccessControlComboBox;

    private applyAction: api.ui.Action;

    protected header: EditPermissionsDialogHeader;

    constructor() {
        super();

        this.addClass('edit-permissions-dialog');

        this.inheritPermissionsCheck = api.ui.Checkbox.create().setLabelText('Inherit permissions').build();
        this.inheritPermissionsCheck.addClass('inherit-perm-check');
        this.appendChildToContentPanel(this.inheritPermissionsCheck);

        let section = new api.dom.SectionEl();
        this.appendChildToContentPanel(section);

        let form = new api.ui.form.Form();
        section.appendChild(form);

        this.comboBox = new AccessControlComboBox();
        this.comboBox.addClass('principal-combobox');
        form.appendChild(this.comboBox);

        let comboBoxChangeListener = () => {
            let currentEntries: AccessControlEntry[] = this.getEntries().sort();

            let permissionsModified: boolean = !api.ObjectHelper.arrayEquals(currentEntries, this.originalValues);
            let inheritCheckModified: boolean = this.inheritPermissionsCheck.isChecked() !== this.originalInherit;
            let overwriteModified: boolean = this.overwriteChildPermissionsCheck.isChecked() !== this.originalOverwrite;

            this.applyAction.setEnabled(permissionsModified || inheritCheckModified || overwriteModified);
        };

        let changeListener = () => {
            const inheritPermissions = this.inheritPermissionsCheck.isChecked();

            this.comboBox.toggleClass('disabled', inheritPermissions);
            if (inheritPermissions) {
                this.layoutInheritedPermissions();
            }
            this.comboBox.getComboBox().setVisible(!inheritPermissions);
            this.comboBox.setReadOnly(inheritPermissions);

            comboBoxChangeListener();
        };
        this.inheritPermissionsCheck.onValueChanged(changeListener);

        this.overwriteChildPermissionsCheck = api.ui.Checkbox.create().setLabelText('Overwrite child permissions').build();
        this.overwriteChildPermissionsCheck.addClass('overwrite-child-check');
        this.appendChildToContentPanel(this.overwriteChildPermissionsCheck);

        this.applyAction = new api.ui.Action('Apply');
        this.applyAction.onExecuted(() => {
            this.applyPermissions();
        });
        this.addAction(this.applyAction, true);

        api.dom.Body.get().appendChild(this);

        this.comboBox.onOptionValueChanged(comboBoxChangeListener);
        this.comboBox.onOptionSelected(comboBoxChangeListener);
        this.comboBox.onOptionDeselected(comboBoxChangeListener);
        this.overwriteChildPermissionsCheck.onValueChanged(comboBoxChangeListener);

        this.parentPermissions = [];

        api.content.event.OpenEditPermissionsDialogEvent.on((event) => {
            this.contentId = event.getContentId();
            this.contentPath = event.getContentPath();
            this.displayName = event.getDisplayName();
            this.permissions = event.getPermissions();
            this.inheritPermissions = event.isInheritPermissions();
            this.overwritePermissions = event.isOverwritePermissions();

            this.immediateApply = event.isImmediateApply();

            this.getParentPermissions().then((parentPermissions: AccessControlList) => {
                this.parentPermissions = parentPermissions.getEntries();

                this.setUpDialog();

                this.overwriteChildPermissionsCheck.setChecked(this.overwritePermissions, true);

                changeListener();

                this.open();

            }).catch(() => {
                api.notify.showWarning("Could not read inherit permissions for content '" + this.displayName + "'");
            }).done();
        });

        this.addCancelButtonToBottom();
    }

    protected createHeader(): EditPermissionsDialogHeader {
        return new EditPermissionsDialogHeader('Edit Permissions', '');
    }

    protected getHeader(): EditPermissionsDialogHeader {
        return this.header;
    }

    private applyPermissions() {
        let permissions = new AccessControlList(this.getEntries());

        if (this.immediateApply) {
            let req = new api.content.resource.ApplyContentPermissionsRequest().setId(this.contentId).setInheritPermissions(
                this.inheritPermissionsCheck.isChecked()).setPermissions(permissions).setOverwriteChildPermissions(
                this.overwriteChildPermissionsCheck.isChecked());
            let res = req.sendAndParse();

            res.done((updatedContent: Content) => {
                api.notify.showFeedback("Permissions applied to content '" + updatedContent.getDisplayName() + "'");
                this.close();
            });
        } else {
            ContentPermissionsApplyEvent.create().setContentId(this.contentId).setPermissions(
                permissions).setInheritPermissions(this.inheritPermissionsCheck.isChecked()).setOverwritePermissions(
                this.overwriteChildPermissionsCheck.isChecked()).build().fire();

            this.close();
        }
    }

    private setUpDialog() {
        this.comboBox.clearSelection(true);
        this.overwriteChildPermissionsCheck.setChecked(false);

        let contentPermissionsEntries: AccessControlEntry[] = this.permissions.getEntries();
        this.originalValues = contentPermissionsEntries.sort();
        this.originalInherit = this.inheritPermissions;
        this.originalOverwrite = this.overwritePermissions;

        this.originalValues.forEach((item) => {
            if (!this.comboBox.isSelected(item)) {
                this.comboBox.select(item);
            }
        });

        this.inheritPermissionsCheck.setChecked(this.inheritPermissions);

        this.comboBox.giveFocus();
    }

    private layoutInheritedPermissions() {
        this.comboBox.clearSelection(true);
        this.parentPermissions.forEach((item) => {
            if (!this.comboBox.isSelected(item)) {
                this.comboBox.select(item);
            }
        });
    }

    private getEntries(): AccessControlEntry[] {
        return this.comboBox.getSelectedDisplayValues();
    }

    private getParentPermissions(): wemQ.Promise<AccessControlList> {
        let deferred = wemQ.defer<AccessControlList>();

        let parentPath = this.contentPath.getParentPath();
        if (parentPath && parentPath.isNotRoot()) {
            new api.content.resource.GetContentByPathRequest(parentPath).sendAndParse().then((content: Content) => {
                deferred.resolve(content.getPermissions());
            }).catch((reason: any) => {
                deferred.reject(new Error('Inherit permissions for [' + this.contentPath.toString() +
                                          '] could not be retrieved'));
            }).done();
        } else {
            new api.content.resource.GetContentRootPermissionsRequest().sendAndParse().then((rootPermissions: AccessControlList) => {
                deferred.resolve(rootPermissions);
            }).catch((reason: any) => {
                deferred.reject(new Error('Inherit permissions for [' + this.contentPath.toString() +
                                          '] could not be retrieved'));
            }).done();
        }

        return deferred.promise;
    }

    show() {
        if (this.contentPath) {
            this.getHeader().setPath(this.contentPath.toString());
        } else {
            this.getHeader().setPath('');
        }
        super.show();

        if (this.comboBox.getComboBox().isVisible()) {
            this.comboBox.giveFocus();
        } else {
            this.inheritPermissionsCheck.giveFocus();
        }
    }
}

export class EditPermissionsDialogHeader extends api.ui.dialog.ModalDialogHeader {

    private pathEl: api.dom.PEl;

    constructor(title: string, path: string) {
        super(title);

        this.pathEl = new api.dom.PEl('path');
        this.pathEl.setHtml(path);
        this.appendChild(this.pathEl);
    }

    setPath(path: string) {
        this.pathEl.setHtml(path);
    }
}
