var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var getAvailableApplicationsExpected = ['app1', 'app2'];

exports.getAvailableApplications = function () {
    createProject([{'applicationKey': 'app1', config: {}}, {'applicationKey': 'app2', config: {}}]);

    var result = project.getAvailableApplications({
        id: 'myproject'
    });

    assert.assertEquals(getAvailableApplicationsExpected.length, result.length);
    assert.assertEquals(getAvailableApplicationsExpected[0], result[0]);
    assert.assertEquals(getAvailableApplicationsExpected[1], result[1]);
};

exports.getProjectWithoutApplications = function () {
    createProject(null);

    var result = project.getAvailableApplications({
        id: 'myproject'
    });

    assert.assertEquals(0, result.length);
};

exports.getProjectNotExist = function () {
    createProject([{'applicationKey': 'app1', config: {}}, {'applicationKey': 'app2', config: {}}]);

    try {
        project.getAvailableApplications({
            id: 'myproject2'
        });

        throw new Error('IllegalArgumentException should be thrown.');

    } catch (e) {
        assert.assertEquals('Project [myproject2] was not found', e.getMessage());
    }
};

exports.getProjectNull = function () {
    createProject([{'applicationKey': 'app1', config: {}}, {'applicationKey': 'app2', config: {}}]);

    try {
        project.getAvailableApplications({
            id: null
        });

        throw new Error('IllegalArgumentException should be thrown.');

    } catch (e) {
        assert.assertEquals('Project name is required', e.getMessage());
    }
};


function createProject(siteConfig) {
    project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        readAccess: {public: true},
        siteConfig: siteConfig
    });
}
