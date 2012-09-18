function getFormData() {
    return {
        birthday: '2012-10-01',
        description: 'dummy description',
        fax: '325252525',
        firstName: 'John',
        gender: 'male',
        htmlEmail: true,
        homePage: 'http://localhost:8080',
        initials: 'J.J.',
        lastName: 'Doe',
        memberId: '007',
        middleName: 'Martin',
        mobile: '73232323232',
        nickName: 'looser',
        organization: 'IBA',
        phone: '563636363',
        prefix: 'Mr.',
        suffix: 'san',
        title: 'doctor',
        personalId: 'bond'
    };
}

function createEditUserFormPanelWithConfig(userstoreStore, additionalConfig) {
    var config = {
        renderTo: Ext.getBody(),
        enableToolbar: false,
        store: userstoreStore
    };
    config = Ext.apply(config, additionalConfig);
    var eufp = Ext.create('Admin.view.account.EditUserFormPanel', config);
    eufp.renderUserForm({userStore: "teststore"});
    return eufp;
}

function createEditUserFormPanel(userstoreStore) {
    return createEditUserFormPanelWithConfig(userstoreStore, {
        validationUrls: {
            username: 'account/json/UniqueUserNameResponse.json',
            email: 'account/json/UniqueEmailResponse.json'
        }
    });
}

function testEditUserFormPanel(t, userstoreStore, callback) {

    t.diag('Basic edit user form test');

    var eufp = createEditUserFormPanel(userstoreStore);
    eufp.renderUserForm({userStore: "teststore"});
    // Test fields from userstore config
    t.ok(eufp.down('#birthday'), 'Birthday field is present');
    t.ok(eufp.down('#description'), 'Description field is present');
    t.ok(eufp.down('#fax'), 'Fax field is present');
    t.ok(eufp.down('#firstName'), 'First name field is present');
    t.ok(eufp.down('#gender'), 'Gender field is present');
    t.ok(eufp.down('#homePage'), 'Home page field is present');
    t.ok(eufp.down('#htmlEmail'), 'HTML email field is present');
    t.ok(eufp.down('#initials'), 'Initials field is present');
    t.ok(eufp.down('#lastName'), 'Last name field is present');
    t.ok(eufp.down('#memberId'), 'Member ID field is present');
    t.ok(eufp.down('#middleName'), 'Middle name field is present');
    t.ok(eufp.down('#nickName'), 'Nick name field is present');
    t.ok(eufp.down('#organization'), 'Organization field is present');
    t.ok(eufp.down('#personalId'), 'Personal ID field is present');
    t.ok(eufp.down('#phone'), 'Phone field is present');
    t.ok(eufp.down('#prefix'), 'Prefix field is present');
    t.ok(eufp.down('#suffix'), 'Suffix field is present');
    t.ok(eufp.down('#title'), 'Title field is present');
    //Test fields that are excluded by default
    t.notOk(eufp.down('#username'), 'Username field is excluded');
    t.notOk(eufp.down('#email'), 'E-Mail field is excluded');
    t.notOk(eufp.down('#country'), 'Country field is excluded');
    t.notOk(eufp.down('#globalPosition'), 'Global position field is excluded');
    t.notOk(eufp.down('#locale'), 'Locale field is excluded');
    t.notOk(eufp.down('#photo'), 'Photo field is excluded');
    t.notOk(eufp.down('#password'), 'Password field is excluded');
    t.notOk(eufp.down('#timezone'), 'Time zone field is excluded');
    Ext.destroy(eufp);
    callback();
}

function testEditUserFormPanelWithIncludedFields(t, userstoreStore, callback) {

    t.diag('Test included fields');

    var eufp = createEditUserFormPanelWithConfig(userstoreStore, {
        includedFields: ['username', 'email', 'password',
            'country', 'locale', 'timezone', 'globalPosition'],
        validationUrls: {
            username: 'account/json/UniqueUserNameResponse.json',
            email: 'account/json/UniqueEmailResponse.json'
        }
    });
    //Test fields that are explicitly included in user form
    t.ok(eufp.down('#username'), 'Username field is present');
    t.ok(eufp.down('#email'), 'Email field is present');
    t.ok(eufp.down('#password'), 'Password field is present');
    t.ok(eufp.down('#country'), 'Country field is present');
    t.ok(eufp.down('#locale'), 'Locale field is present');
    t.ok(eufp.down('#timezone'), 'Timezone field is present');
    t.ok(eufp.down('#globalPosition'), 'Global position field is present');
    Ext.destroy(eufp);
    callback();
}

function testEditUserFormPanelWithExcludedFields(t, userstoreStore, callback) {

    t.diag('Test excluded fields');

    var eufp = createEditUserFormPanelWithConfig(userstoreStore, {
        excludedFields: [ 'address', 'birthday', 'country', 'firstName', 'lastName', 'fax'],
        validationUrls: {
            username: 'account/json/UniqueUserNameResponse.json',
            email: 'account/json/UniqueEmailResponse.json'
        }
    });
    //Test fields that are explicitly excluded from user form
    t.notOk(eufp.down('#address'), 'Address field should be excluded');
    t.notOk(eufp.down('#birthday'), 'Birthday field should be excluded');
    t.notOk(eufp.down('#country'), 'Country field should be excluded');
    t.notOk(eufp.down('#firstName'), 'First name field should be excluded');
    t.notOk(eufp.down('#lastName'), 'Last name field should be excluded');
    t.notOk(eufp.down('#fax'), 'Fax field should be excluded');
    //Test fields that are remain in user form
    t.ok(eufp.down('#description'), 'Description field should be included');
    t.ok(eufp.down('#gender'), 'Gender field should be included');
    t.ok(eufp.down('#globalPosition'), 'Global position field should be included');
    t.ok(eufp.down('#homePage'), 'Home page field should be included');
    t.ok(eufp.down('#htmlEmail'), 'Html email field should be included');
    t.ok(eufp.down('#initials'), 'Initials field should be included');
    t.ok(eufp.down('#locale'), 'Locale field should be included');
    t.ok(eufp.down('#memberId'), 'Member ID field should be included');
    t.ok(eufp.down('#middleName'), 'Middle name field should be included');
    t.ok(eufp.down('#mobile'), 'Mobile field should be included');
    t.ok(eufp.down('#nickName'), 'Nick name field should be included');
    t.ok(eufp.down('#organization'), 'Organization field should be included');
    t.ok(eufp.down('#personalId'), 'Personal ID field should be included');
    t.ok(eufp.down('#phone'), 'Phone field should be included');
    t.ok(eufp.down('#prefix'), 'Prefix field should be included');
    t.ok(eufp.down('#suffix'), 'Suffix field should be included');
    t.ok(eufp.down('#timezone'), 'Time zone field should be included');
    t.ok(eufp.down('#title'), 'Title field should be included');
    Ext.destroy(eufp);
    callback();
}

function testEditUserFormPanelGetData(t, userstoreStore, callback) {

    t.diag('Test get data');

    var eufp = createEditUserFormPanel(userstoreStore);
    var testData = getFormData();
    t.chain(
        {
            action: 'click',
            target: eufp.down('#htmlEmail')
        },
        {
            action: 'type',
            target: eufp.down('#birthday'),
            text: testData.birthday
        },
        {
            action: 'type',
            target: eufp.down('#description'),
            text: testData.description
        },
        {
            action: 'type',
            target: eufp.down('#fax'),
            text: testData.fax
        },
        {
            action: 'type',
            target: eufp.down('#firstName'),
            text: testData.firstName
        },
        {
            action: 'type',
            target: eufp.down('#gender'),
            text: testData.gender
        },
        {
            action: 'type',
            target: eufp.down('#homePage'),
            text: testData.homePage
        },
        {
            action: 'type',
            target: eufp.down('#initials'),
            text: testData.initials
        },
        {
            action: 'type',
            target: eufp.down('#lastName'),
            text: testData.lastName
        },
        {
            action: 'type',
            target: eufp.down('#memberId'),
            text: testData.memberId
        },
        {
            action: 'type',
            target: eufp.down('#middleName'),
            text: testData.middleName
        },
        {
            action: 'type',
            target: eufp.down('#mobile'),
            text: testData.mobile
        },
        {
            action: 'type',
            target: eufp.down('#nickName'),
            text: testData.nickName
        },
        {
            action: 'type',
            target: eufp.down('#organization'),
            text: testData.organization
        },
        {
            action: 'type',
            target: eufp.down('#personalId'),
            text: testData.personalId
        },
        {
            action: 'type',
            target: eufp.down('#phone'),
            text: testData.phone
        },
        {
            action: 'type',
            target: eufp.down('#prefix'),
            text: testData.prefix
        },
        {
            action: 'type',
            target: eufp.down('#suffix'),
            text: testData.suffix
        },
        {
            action: 'type',
            target: eufp.down('#title'),
            text: testData.title
        },
        function (next) {
            var formData = eufp.getData();
            t.ok(formData, 'Form data is valid');
            var info = formData.profile;
            t.ok(info, 'User info section presents');
            t.is(new Date(info.birthday).toDateString(), new Date(testData.birthday).toDateString(),
                'Birthday value is right');
            t.is(info.description, testData.description, 'Description value is right');
            t.is(info.fax, testData.fax, 'Fax value is right');
            t.is(info.firstName, testData.firstName, 'First name value is right');
            t.is(info.gender, testData.gender, 'Gender value is right');
            t.is(info.homePage, testData.homePage, 'Home page value is right');
            t.is(info.htmlEmail, testData.htmlEmail, 'HTML email value is right');
            t.is(info.initials, testData.initials, 'Initials value is right');
            t.is(info.lastName, testData.lastName, 'Last name value is right');
            t.is(info.memberId, testData.memberId, 'Member ID value is right');
            t.is(info.middleName, testData.middleName, 'Middle name value is right');
            t.is(info.mobile, testData.mobile, 'Mobile value is right');
            t.is(info.nickName, testData.nickName, 'Nick name value is right');
            t.is(info.organization, testData.organization, 'Organization value is right');
            t.is(info.personalId, testData.personalId, 'Personal ID value is right');
            t.is(info.prefix, testData.prefix, 'Prefix value is right');
            t.is(info.suffix, testData.suffix, 'Suffix value is right');
            t.is(info.title, testData.title, 'Title value is right');
            t.is(info.phone, testData.phone, 'Phone value is right');
            Ext.destroy(eufp);
            callback();
        }
    );
}

function testEditUserFormValidation(t, userstoreStore, callback) {

    t.diag('Test field validation');

    var eufp = createEditUserFormPanel(userstoreStore);
    t.is(eufp.getForm().isValid(), false, 'Form is invalid');
    t.chain(
        {
            action: 'type',
            target: eufp.down('#firstName'),
            text: 'John'
        },
        {
            action: 'type',
            target: eufp.down('#homePage'),
            text: 'http://localhost:8080'
        },
        function (next) {
            t.is(eufp.getForm().isValid(), false, 'Form is still invalid');
            next();
        },
        function (next) {
            t.waitForEvent(eufp.getForm(), 'validitychange', next, this, 3000);
            t.type(eufp.down('#lastName'), 'Doe');
        },
        function (next) {
            t.is(eufp.getForm().isValid(), true, 'Form is valid');
            Ext.destroy(eufp);
            callback();
        }
    );

}

function testEditUserFormRemoteValidation(t, userstoreStore, callback) {

    t.diag('Test remote validation');

    var eufp = createEditUserFormPanelWithConfig(userstoreStore, {
        includedFields: ['username', 'email'],
        validationUrls: {
            username: 'account/json/NonUniqueUserNameResponse.json',
            email: 'account/json/NonUniqueEmailResponse.json'
        }
    });
    t.is(eufp.getForm().isValid(), false, 'Created non unique form. Initially not valid.');
    t.chain(
        {
            action: 'type',
            target: eufp.down('#username'),
            text: 'newname'
        },
        {
            action: 'type',
            target: eufp.down('#email'),
            text: 'test@email.com'
        },
        function (next) {
            t.is(eufp.getForm().isValid(), false, 'Form is not valid after all filling values. That\'s ok');
            Ext.destroy(eufp);

            eufp = createEditUserFormPanelWithConfig(userstoreStore, {
                includedFields: ['username', 'email'],
                validationUrls: {
                    username: 'account/json/UniqueUserNameResponse.json',
                    email: 'account/json/UniqueEmailResponse.json'
                }
            });
            t.is(eufp.getForm().isValid(), false, 'Created unique form. Initially not valid.');
            next();
        },
        function (next) {
            eufp.down('#username').setValue('newname');
            eufp.down('#email').setValue('test@email.com');
            t.waitForEvent(eufp.getForm(), 'validitychange', next, this, 3000);
        },
        function (next) {
            t.is(eufp.getForm().isValid(), true, 'Form is valid. Remote validation succeded.');
            Ext.destroy(eufp);
            callback();
        }
    );
}

StartTest(function (t) {
    t.requireOk(
        [
            'Admin.view.account.EditUserFormPanel',
            'Test.account.store.UserstoreConfigStore',
            'Admin.model.account.CallingCodeModel'
        ],
        function () {
            var userstoreStore = Ext.create('Test.account.store.UserstoreConfigStore', {});
            t.loadStoresAndThen(userstoreStore, function () {
                t.chain(
                    function (next) {
                        testEditUserFormPanel(t, userstoreStore, next);
                    },
                    function (next) {
                        testEditUserFormPanelWithIncludedFields(t, userstoreStore, next);
                    },
                    function (next) {
                        testEditUserFormPanelWithExcludedFields(t, userstoreStore, next);
                    },
                    function (next) {
                        testEditUserFormPanelGetData(t, userstoreStore, next);
                    },
                    function (next) {
                        testEditUserFormValidation(t, userstoreStore, next);
                    },
                    function (next) {
                        testEditUserFormRemoteValidation(t, userstoreStore, next);
                    }
                );
            });
        }
    );

});
