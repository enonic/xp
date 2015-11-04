module app.wizard {

    import Content = api.content.Content;
    import AccessControlComboBox = api.ui.security.acl.AccessControlComboBox;
    import AccessControlEntry = api.security.acl.AccessControlEntry;
    import AccessControlList = api.security.acl.AccessControlList;

    export class EditPermissionsDialog extends api.ui.dialog.ModalDialog {

        private content: Content;
        private parentPermissions: AccessControlEntry[];
        private originalValues: AccessControlEntry[];
        private originalInherit: boolean;

        private dialogTitle: EditPermissionsDialogTitle;
        private inheritPermissionsCheck: api.ui.Checkbox;
        private overwriteChildPermissionsCheck: api.ui.Checkbox;
        private comboBox: AccessControlComboBox;
        private applyAction: api.ui.Action;


        constructor() {
            this.dialogTitle = new EditPermissionsDialogTitle('Edit Permissions', '');

            super({
                title: this.dialogTitle
            });

            this.addClass('edit-permissions-dialog');

            this.inheritPermissionsCheck = new api.ui.Checkbox().setLabel('Inherit permissions');
            this.inheritPermissionsCheck.addClass('inherit-perm-check');
            this.appendChildToContentPanel(this.inheritPermissionsCheck);

            var section = new api.dom.SectionEl();
            this.appendChildToContentPanel(section);

            var form = new api.ui.form.Form();
            section.appendChild(form);

            this.comboBox = new AccessControlComboBox();
            this.comboBox.addClass('principal-combobox');
            form.appendChild(this.comboBox);

            var comboBoxChangeListener = () => {
                var currentEntries: AccessControlEntry[] = this.getEntries().sort();
                var permissionsModified: boolean = !api.ObjectHelper.arrayEquals(currentEntries, this.originalValues);
                var inheritCheckModified: boolean = this.inheritPermissionsCheck.isChecked() !== this.originalInherit;
                var overwriteModified: boolean = this.overwriteChildPermissionsCheck.isChecked();
                this.applyAction.setEnabled(permissionsModified || inheritCheckModified || overwriteModified);
            };

            var changeListener = () => {
                var inheritPermissions = this.inheritPermissionsCheck.isChecked();

                this.comboBox.toggleClass('disabled', inheritPermissions);
                if (inheritPermissions) {
                    this.layoutInheritedPermissions();
                }
                this.comboBox.getComboBox().setVisible(!inheritPermissions);
                this.comboBox.setEditable(!inheritPermissions);

                comboBoxChangeListener();
            };
            this.inheritPermissionsCheck.onValueChanged(changeListener);

            this.overwriteChildPermissionsCheck = new api.ui.Checkbox().setLabel('Overwrite child permissions');
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
            OpenEditPermissionsDialogEvent.on((event) => {
                this.content = event.getContent();

                this.getParentPermissions().then((parentPermissions: AccessControlList) => {
                    this.parentPermissions = parentPermissions.getEntries();

                    this.setUpDialog();

                    this.open();

                }).catch(() => {
                    api.notify.showWarning('Could not read inherit permissions for content ' + this.content.getPath().toString());
                }).done();
            });

            this.addCancelButtonToBottom();
        }

        private applyPermissions() {
            var permissions = new AccessControlList(this.getEntries());
            var req = new api.content.ApplyContentPermissionsRequest().setId(this.content.getId()).
                setInheritPermissions(this.inheritPermissionsCheck.isChecked()).
                setPermissions(permissions).
                setOverwriteChildPermissions(this.overwriteChildPermissionsCheck.isChecked());
            var res = req.sendAndParse();

            res.done((updatedContent: Content) => {
                new ContentPermissionsAppliedEvent(updatedContent).fire();
                api.notify.showFeedback('Permissions applied to content ' + updatedContent.getPath().toString());
                this.close();
            });
        }

        private setUpDialog() {
            this.comboBox.clearSelection(true);
            this.overwriteChildPermissionsCheck.setChecked(false);

            var contentPermissions = this.content.getPermissions();
            var contentPermissionsEntries: AccessControlEntry[] = contentPermissions.getEntries();
            this.originalValues = contentPermissionsEntries.sort();
            this.originalInherit = this.content.isInheritPermissionsEnabled();

            this.originalValues.forEach((item) => {
                if (!this.comboBox.isSelected(item)) {
                    this.comboBox.select(item);
                }
            });

            this.inheritPermissionsCheck.setChecked(this.content.isInheritPermissionsEnabled());

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
            var deferred = wemQ.defer<AccessControlList>();

            var parentPath = this.content.getPath().getParentPath();
            if (parentPath && parentPath.isNotRoot()) {
                new api.content.GetContentByPathRequest(parentPath).sendAndParse().then((content: Content) => {
                    deferred.resolve(content.getPermissions());
                }).catch((reason: any) => {
                    deferred.reject(new Error("Inherit permissions for [" + this.content.getPath().toString() +
                                              "] could not be retrieved"));
                }).done();
            } else {
                new api.content.GetContentRootPermissionsRequest().sendAndParse().then((rootPermissions: AccessControlList) => {
                    deferred.resolve(rootPermissions);
                }).catch((reason: any) => {
                    deferred.reject(new Error("Inherit permissions for [" + this.content.getPath().toString() +
                                              "] could not be retrieved"));
                }).done();
            }

            return deferred.promise;
        }

        show() {
            if (this.content) {
                this.dialogTitle.setPath(this.content.getPath().toString());
            } else {
                this.dialogTitle.setPath('');
            }
            super.show();
        }
    }

    export class EditPermissionsDialogTitle extends api.ui.dialog.ModalDialogHeader {

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

}