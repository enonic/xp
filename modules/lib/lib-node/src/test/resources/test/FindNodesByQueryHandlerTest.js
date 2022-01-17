var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

exports.invalid = function () {

    try {
        repo.query({
            'start': 0,
            'count': 100,
            'query':
                [{
                    boolean: {
                        must: [{
                            term: {
                                name: 'type',
                                value: 'article'
                            }

                        }, {
                            fulltext: {
                                field: 'displayName',
                                query: 'fisk',
                                operator: 'AND'
                            }
                        }]
                    }
                }, {
                    boolean: {
                        must: {
                            term: {
                                name: 'type',
                                value: 'non-article'
                            }

                        }
                    }
                }]
        });
    } catch (e) {
        assert.assertEquals(e.message, 'query must be a String or JSON object');
        return;
    }

    throw {message: 'Expected exception'};
};

var expectedEmptyJson = {
    'total': 0,
    'count': 0,
    'hits': []
};

exports.queryEmpty = function () {
    var result = repo.query({
        'start': 0,
        'count': 100,
        'query': {}
    });
    assert.assertJsonEquals(expectedEmptyJson, result);
};

exports.sort = function () {
    var result = repo.query({
        'start': 0,
        'count': 100,
        'query': {},
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
        ]
    });
    assert.assertJsonEquals(expectedEmptyJson, result);
};

exports.sortInvalid = function () {
    try {
        repo.query({
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

