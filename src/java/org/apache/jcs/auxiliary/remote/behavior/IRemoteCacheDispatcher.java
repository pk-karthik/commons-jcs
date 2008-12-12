package org.apache.jcs.auxiliary.remote.behavior;

import java.io.IOException;

import org.apache.jcs.auxiliary.remote.value.RemoteCacheRequest;
import org.apache.jcs.auxiliary.remote.value.RemoteCacheResponse;

/**
 * In the future, this can be used as a generic dispatcher abstraction.
 * <p>
 * At the time of creation, only the http remote cache uses it. The RMI remote could be converted to
 * use it as well.
 */
public interface IRemoteCacheDispatcher
{
    /**
     * All requests will go through this method. The dispatcher implementation will send the request
     * remotely.
     * <p>
     * @param remoteCacheRequest
     * @return RemoteCacheResponse
     * @throws IOException
     */
    RemoteCacheResponse dispatchRequest( RemoteCacheRequest remoteCacheRequest )
        throws IOException;
}
