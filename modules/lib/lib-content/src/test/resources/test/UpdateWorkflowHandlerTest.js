const assert = require('/lib/xp/testing.js');
const content = require('/lib/xp/content.js');

exports.updateWorkflowState = () => {
    let result = content.updateWorkflow({
        key: '123456',
        editor: w => {
            w.state = 'READY';
            return w;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('READY', result.content.workflow.state);
};

exports.updateWorkflowChecks = () => {
    let result = content.updateWorkflow({
        key: '123456',
        editor: w => {
            w.checks = {
                'Review': 'APPROVED',
                'Final check': 'PENDING'
            };
            return w;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('APPROVED', result.content.workflow.checks['Review']);
    assert.assertEquals('PENDING', result.content.workflow.checks['Final check']);
};

exports.updateWorkflowStateAndChecks = () => {
    let result = content.updateWorkflow({
        key: '123456',
        editor: w => {
            w.state = 'PENDING_APPROVAL';
            w.checks = {
                'Legal review': 'PENDING',
                'Manager approval': 'APPROVED'
            };
            return w;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('PENDING_APPROVAL', result.content.workflow.state);
    assert.assertEquals('PENDING', result.content.workflow.checks['Legal review']);
    assert.assertEquals('APPROVED', result.content.workflow.checks['Manager approval']);
};
