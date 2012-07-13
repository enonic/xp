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
            username: 'tests/account/json/UniqueUserNameResponse.json',
            email: 'tests/account/json/UniqueEmailResponse.json'
        }
    });
}

function testEditUserFormPanel(t, userstoreStore) {
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
}

function testEditUserFormPanelWithIncludedFields(t, userstoreStore) {
    var eufp = createEditUserFormPanelWithConfig(userstoreStore, {
        includedFields: ['username', 'email', 'password',
            'country', 'locale', 'timezone', 'globalPosition'],
        validationUrls: {
            username: 'tests/account/json/UniqueUserNameResponse.json',
            email: 'tests/account/json/UniqueEmailResponse.json'
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
}

function testEditUserFormPanelWithExcludedFields(t, userstoreStore) {
    var eufp = createEditUserFormPanelWithConfig(userstoreStore, {
        excludedFields: [ 'address', 'birthday', 'country', 'firstName', 'lastName', 'fax'],
        validationUrls: {
            username: 'tests/account/json/UniqueUserNameResponse.json',
            email: 'tests/account/json/UniqueEmailResponse.json'
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
}

function testEditUserFormPanelGetData(t, userstoreStore) {
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
            t.ok(formData.userInfo, 'User info section presents');
            t.is(new Date(formData.userInfo.birthday).toDateString(), new Date(testData.birthday).toDateString(),
                'Birthday value is right');
            t.is(formData.userInfo.description, testData.description, 'Description value is right');
            t.is(formData.userInfo.fax, testData.fax, 'Fax value is right');
            t.is(formData.userInfo.firstName, testData.firstName, 'First name value is right');
            t.is(formData.userInfo.gender, testData.gender, 'Gender value is right');
            t.is(formData.userInfo.homePage, testData.homePage, 'Home page value is right');
            t.is(formData.userInfo.htmlEmail, testData.htmlEmail, 'HTML email value is right');
            t.is(formData.userInfo.initials, testData.initials, 'Initials value is right');
            t.is(formData.userInfo.lastName, testData.lastName, 'Last name value is right');
            t.is(formData.userInfo.memberId, testData.memberId, 'Member ID value is right');
            t.is(formData.userInfo.middleName, testData.middleName, 'Middle name value is right');
            t.is(formData.userInfo.mobile, testData.mobile, 'Mobile value is right');
            t.is(formData.userInfo.nickName, testData.nickName, 'Nick name value is right');
            t.is(formData.userInfo.organization, testData.organization, 'Organization value is right');
            t.is(formData.userInfo.personalId, testData.personalId, 'Personal ID value is right');
            t.is(formData.userInfo.prefix, testData.prefix, 'Prefix value is right');
            t.is(formData.userInfo.suffix, testData.suffix, 'Suffix value is right');
            t.is(formData.userInfo.title, testData.title, 'Title value is right');
            t.is(formData.userInfo.phone, testData.phone, 'Phone value is right');
            Ext.destroy(eufp);
        }
    );
}

function testEditUserFormValidation(t, userstoreStore) {
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
        {
            action: 'type',
            target: eufp.down('#lastName'),
            text: 'Doe'
        },
        function (next) {
            t.is(eufp.getForm().isValid(), true, 'Form is valid');
            Ext.destroy(eufp);
        }
    );

}

function testEditUserFormRemoteValidation(t, userstoreStore) {
    var eufp1 = createEditUserFormPanelWithConfig(userstoreStore, {
        includedFields: ['username', 'email'],
        validationUrls: {
            username: 'tests/account/json/NonUniqueUserNameResponse.json',
            email: 'tests/account/json/NonUniqueEmailResponse.json'
        }
    });
    t.chain(
        {
            action: 'type',
            target: eufp1.down('#username'),
            text: 'newname'
        },
        {
            action: 'type',
            target: eufp1.down('#email'),
            text: 'test@email.com'
        },
        function () {
            t.is(eufp1.getForm().isValid(), false, 'Form is not valid. That\'s ok');
            Ext.destroy(eufp1);
        }
    );

    var eufp2 = createEditUserFormPanelWithConfig(userstoreStore, {
        includedFields: ['username', 'email'],
        validationUrls: {
            username: 'tests/account/json/UniqueUserNameResponse.json',
            email: 'tests/account/json/UniqueEmailResponse.json'
        }
    });
    t.chain(
        {
            action: 'type',
            target: eufp2.down('#username'),
            text: 'newname'
        },
        {
            action: 'type',
            target: eufp2.down('#email'),
            text: 'test@email.com'
        },
        function () {
            t.is(eufp2.getForm().isValid(), true, 'Form is valid. Remote validation succeded.');
            Ext.destroy(eufp2);
        }
    );
}

StartTest(function (t) {
    t.requireOk(
        [
            'Admin.view.account.EditUserFormPanel',
            'Test.account.store.UserstoreConfigStore'
        ],
        function () {
            var userstoreStore = Ext.create('Test.account.store.UserstoreConfigStore', {});
            t.loadStoresAndThen(userstoreStore, function () {
                testEditUserFormPanel(t, userstoreStore);
                testEditUserFormPanelWithIncludedFields(t, userstoreStore);
                testEditUserFormPanelWithExcludedFields(t, userstoreStore);
                testEditUserFormPanelGetData(t, userstoreStore);
                testEditUserFormValidation(t, userstoreStore);
                testEditUserFormRemoteValidation(t, userstoreStore);
            });
        }
    );

});
