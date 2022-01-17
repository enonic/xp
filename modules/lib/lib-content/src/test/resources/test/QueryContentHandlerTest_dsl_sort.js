var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

exports.sortMultiple = function () {

    var result = content.query({
            'start': 0,
            'count': 100,
            'sort': [
                {
                    'field': '_modifiedTime',
                    'direction': 'DESC'
                },
                {
                    'type': 'geodistance',
                    'field': 'myGeoPoint',
                    'location': {
                        'lat': '2.2',
                        'lon': '3.3'
                    }
                }
            ],
            'query': 'type = \'article\' AND fulltext(\'myField\', \'searching for cheese\', \'AND\') ',
        }
    );
};

exports.sortSingle = function () {

    var result = content.query({
            'start': 0,
            'count': 100,
            'sort':
                {
                    'field': 'displayName',
                    'direction': 'ASC'
                },
            'query': 'type = \'article\' AND fulltext(\'myField\', \'searching for cheese\', \'AND\') ',
        }
    );
};

exports.sortEmpty = function () {

    try {
        var result = content.query({
                'start': 0,
                'count': 100,
                'sort': {},
                'query': 'type = \'article\' AND fulltext(\'myField\', \'searching for cheese\', \'AND\') ',
            }
        );
    } catch (e) {
        assert.assertEquals(e.message, 'field must be set');
        return;
    }

    throw {message: 'Expected exception'};
};

exports.invalid = function () {

    try {
        content.query({
            'start': 0,
            'count': 100,
            'sort':
                function () {
                }
            ,
            'query': 'type = \'article\' AND fulltext(\'myField\', \'searching for cheese\', \'AND\') ',


        });
    } catch (e) {
        assert.assertEquals(e.message, 'sort must be a String, JSON object or array of JSON objects');
        return;
    }

    throw {message: 'Expected exception'};
};

