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
