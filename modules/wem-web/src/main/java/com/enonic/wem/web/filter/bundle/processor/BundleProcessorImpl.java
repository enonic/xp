package com.enonic.wem.web.filter.bundle.processor;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.filter.bundle.BundleModel;
import com.enonic.wem.web.filter.bundle.BundleRequest;
import com.enonic.wem.web.filter.bundle.compressor.ScriptCompressor;
import com.enonic.wem.web.filter.bundle.generator.ScriptGenerator;
import com.enonic.wem.web.json.ObjectMapperHelper;

@Component
public final class BundleProcessorImpl
    implements BundleProcessor
{
    private final BundleCache cache;

    private final ObjectMapper mapper;

    private ScriptGenerator generator;

    private ScriptCompressor compressor;

    public BundleProcessorImpl()
    {
        this.cache = new BundleCache();
        this.mapper = ObjectMapperHelper.create();
    }

    @Override
    public String process( final BundleRequest req )
        throws Exception
    {
        final String cacheKey = req.getCacheKey();

        if ( cacheKey != null )
        {
            final String content = this.cache.get( cacheKey );
            if ( content != null )
            {
                return content;
            }
        }

        final String content = doProcess( req );
        if ( cacheKey != null )
        {
            this.cache.put( cacheKey, content );
        }

        return content;
    }

    private String doProcess( final BundleRequest req )
        throws Exception
    {
        req.setBundleModel( readBundleModel( req ) );
        String content = this.generator.generate( req );
        if ( req.shouldCompress() )
        {
            content = this.compressor.compress( content );
        }

        return content;
    }

    private BundleModel readBundleModel( final BundleRequest req )
        throws Exception
    {
        return this.mapper.reader( BundleModel.class ).readValue( req.getBundleJsonUrl() );
    }

    @Autowired
    public void setGenerator( final ScriptGenerator generator )
    {
        this.generator = generator;
    }

    @Autowired
    public void setCompressor( final ScriptCompressor compressor )
    {
        this.compressor = compressor;
    }
}
