StartTest(function (t) {
    t.diag("Sanity test, loading classes on demand and verifying they were indeed loaded.");

    t.ok(Ext, 'ExtJS is here');

    t.requireOk('Admin.controller.account.BrowseToolbarController');
    t.requireOk('Admin.controller.account.Controller');
    t.requireOk('Admin.controller.account.DetailPanelController');
    t.requireOk('Admin.controller.account.EditUserPanelController');
    t.requireOk('Admin.controller.account.FilterPanelController');
    t.requireOk('Admin.controller.account.GridPanelController');
    t.requireOk('Admin.controller.account.GroupController');
    t.requireOk('Admin.controller.account.GroupPreviewController');
    t.requireOk('Admin.controller.account.GroupWizardController');
    t.requireOk('Admin.controller.account.UserController');
    t.requireOk('Admin.controller.account.UserPreviewController');
    t.requireOk('Admin.controller.account.UserWizardController');

    t.requireOk('Admin.model.account.AccountModel');
    t.requireOk('Admin.model.account.CallingCodeModel');
    t.requireOk('Admin.model.account.CountryModel');
    t.requireOk('Admin.model.account.GroupModel');
    t.requireOk('Admin.model.account.LanguageModel');
    t.requireOk('Admin.model.account.LocaleModel');
    t.requireOk('Admin.model.account.RegionModel');
    t.requireOk('Admin.model.account.TimezoneModel');
    t.requireOk('Admin.model.account.UserFieldModel');
    t.requireOk('Admin.model.account.UserstoreConfigModel');

    t.requireOk('Admin.store.account.AccountStore');
    t.requireOk('Admin.store.account.CountryStore');
    t.requireOk('Admin.store.account.GroupStore');
    t.requireOk('Admin.store.account.LanguageStore');
    t.requireOk('Admin.store.account.LocaleStore');
    t.requireOk('Admin.store.account.TimezoneStore');
    t.requireOk('Admin.store.account.UserstoreConfigStore');

    t.requireOk('Admin.view.account.preview.group.GroupPreviewPanel');
    t.requireOk('Admin.view.account.preview.group.GroupPreviewToolbar');

    t.requireOk('Admin.view.account.preview.user.UserPreviewPanel');
    t.requireOk('Admin.view.account.preview.user.UserPreviewToolbar');

    t.requireOk('Admin.view.account.wizard.group.GroupWizardPanel');
    t.requireOk('Admin.view.account.wizard.group.GroupWizardToolbar');
    t.requireOk('Admin.view.account.wizard.group.WizardStepGeneralPanel');
    t.requireOk('Admin.view.account.wizard.group.WizardStepMembersPanel');

    t.requireOk('Admin.view.account.wizard.user.UserStoreListPanel');
    t.requireOk('Admin.view.account.wizard.user.UserWizardPanel');
    t.requireOk('Admin.view.account.wizard.user.UserWizardToolbar');
    t.requireOk('Admin.view.account.wizard.user.WizardStepLoginInfoPanel');
    t.requireOk('Admin.view.account.wizard.user.WizardStepMembershipPanel');

});
