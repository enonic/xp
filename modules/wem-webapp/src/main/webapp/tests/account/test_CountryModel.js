function testCountry(t, store) {

    t.is(store.getCount(), 1, 'Store has 1 country');

    var country = store.getAt(0);

    t.diag('Checking country mandatory fields');
    t.is(country.get('code'), 'NO', 'Country code ok');
    t.is(country.get('englishName'), 'NORWAY', 'Country english name ok');
    t.is(country.get('localName'), 'NORGE', 'Country local name ok');
    t.is(country.get('regionsEnglishName'), 'County', 'Regions english name ok');
    t.is(country.get('regionsLocalName'), 'Fylke', 'Regions local name ok');

    t.is(country.regions().getCount(), 19, 'Country got 19 regions');
    t.is(country.callingCodes().getCount(), 1, 'Country got 1 calling code');

    var region = country.regions().getAt(0);

    t.diag('Checking region mandatory fields');
    t.is(region.get('regionCode'), '01', 'Region code ok');
    t.is(region.get('englishName'), '\u00d8stfold', 'Region english name ok');
    t.is(region.get('localName'), '\u00d8stfold', 'Region local name ok');

    var callingCode = country.callingCodes().getAt(0);

    t.diag('Checking calling code mandatory fields');
    t.is(callingCode.get('callingCode'), '47', 'Calling code ok');
    t.is(callingCode.get('englishName'), 'Code 47', 'Calling code english name ok');
    t.is(callingCode.get('localName'), 'Kode 47', 'Calling code local name ok');

    t.done();
}


StartTest(function (t) {

    t.requireOk('Admin.model.account.CountryModel', function () {

        var store = Ext.create('Ext.data.Store', {
            model: 'Admin.model.account.CountryModel',
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: 'account/json/CountriesData.json',
                reader: {
                    type: 'json',
                    root: 'countries',
                    totalProperty: 'total'
                }
            },
            listeners: {
                load: function (me, records, success, opts) {

                    testCountry(t, me);

                }
            }
        });

    });
});
