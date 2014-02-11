package com.enonic.wem.portal.postprocess;

interface PostProcessInstruction
{
    public String evaluate( PostProcessEvaluator evaluator, String content );
}
