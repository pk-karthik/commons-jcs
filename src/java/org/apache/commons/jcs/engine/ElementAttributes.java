package org.apache.commons.jcs.engine;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.jcs.engine.behavior.IElementAttributes;
import org.apache.commons.jcs.engine.control.event.behavior.IElementEventHandler;

/**
 * This it the element attribute descriptor class. Each element in the cache has an ElementAttribute
 * object associated with it. An ElementAttributes object can be associated with an element in 3
 * ways:
 * <ol>
 * <li>When the item is put into the cache, you can associate an element attributes object.</li>
 * <li>If not attributes object is include when the element is put into the cache, then the default
 * attributes for the region will be used.</li>
 * <li>The element attributes can be reset. This effectively results in a retrieval followed by a
 * put. Hence, this is the same as 1.</li>
 * </ol>
 */
public class ElementAttributes
    implements IElementAttributes, Serializable
{
    /** Don't change. */
    private static final long serialVersionUID = 7814990748035017441L;

    /** Can this item be flushed to disk */
    public boolean IS_SPOOL = true;

    /** Is this item laterally distributable */
    public boolean IS_LATERAL = true;

    /** Can this item be sent to the remote cache */
    public boolean IS_REMOTE = true;

    /**
     * You can turn off expiration by setting this to true. This causes the cache to bypass both max
     * life and idle time expiration.
     */
    public boolean IS_ETERNAL = true;

    /** Max life seconds */
    public long maxLifeSeconds = -1;

    /**
     * The maximum time an entry can be idle. Setting this to -1 causes the idle time check to be
     * ignored.
     */
    public long maxIdleTimeSeconds = -1;

    /** The byte size of the field. Must be manually set. */
    public int size = 0;

    /** The creation time. This is used to enforce the max life. */
    public long createTime = 0;

    /** The last access time. This is used to enforce the max idel time. */
    public long lastAccessTime = 0;

    /**
     * The list of Event handlers to use. This is transient, since the event handlers cannot usually
     * be serialized. This means that you cannot attach a post serialization event to an item.
     * <p>
     * TODO we need to check that when an item is passed to a non-local cache that if the local
     * cache had a copy with event handlers, that those handlers are used.
     */
    public transient ArrayList<IElementEventHandler> eventHandlers;

    /**
     * Constructor for the IElementAttributes object
     */
    public ElementAttributes()
    {
        this.createTime = System.currentTimeMillis();
        this.lastAccessTime = this.createTime;
    }

    /**
     * Constructor for the IElementAttributes object
     * <p>
     * @param attr
     */
    protected ElementAttributes( ElementAttributes attr )
    {
        IS_ETERNAL = attr.IS_ETERNAL;

        // waterfall onto disk, for pure disk set memory to 0
        IS_SPOOL = attr.IS_SPOOL;

        // lateral
        IS_LATERAL = attr.IS_LATERAL;

        // central rmi store
        IS_REMOTE = attr.IS_REMOTE;

        maxLifeSeconds = attr.maxLifeSeconds;
        // time-to-live
        maxIdleTimeSeconds = attr.maxIdleTimeSeconds;
        size = attr.size;
    }

    /**
     * Copies the attributes, including references to event handlers.
     * <p>
     * @return a copy of the Attributes
     */
    public IElementAttributes copy()
    {
        try
        {
            // need to make this more efficient. Just want to insure
            // a proper copy
            ElementAttributes attr = new ElementAttributes();
            attr.setIdleTime( this.getIdleTime() );
            attr.setIsEternal( this.getIsEternal() );
            attr.setIsLateral( this.getIsLateral() );
            attr.setIsRemote( this.getIsRemote() );
            attr.setIsSpool( this.getIsSpool() );
            attr.setMaxLifeSeconds( this.getMaxLifeSeconds() );
            attr.addElementEventHandlers( this.eventHandlers );
            return attr;
        }
        catch ( Exception e )
        {
            return new ElementAttributes();
        }
    }

    /**
     * Sets the maxLife attribute of the IAttributes object.
     * <p>
     * @param mls The new MaxLifeSeconds value
     */
    public void setMaxLifeSeconds( long mls )
    {
        this.maxLifeSeconds = mls;
    }

    /**
     * Sets the maxLife attribute of the IAttributes object. How many seconds it can live after
     * creation.
     * <p>
     * If this is exceeded the element will not be returned, instead it will be removed. It will be
     * removed on retrieval, or removed actively if the memory shrinker is turned on.
     * @return The MaxLifeSeconds value
     */
    public long getMaxLifeSeconds()
    {
        return this.maxLifeSeconds;
    }

    /**
     * Sets the idleTime attribute of the IAttributes object. This is the maximum time the item can
     * be idle in the cache, that is not accessed.
     * <p>
     * If this is exceeded the element will not be returned, instead it will be removed. It will be
     * removed on retrieval, or removed actively if the memory shrinker is turned on.
     * @param idle The new idleTime value
     */
    public void setIdleTime( long idle )
    {
        this.maxIdleTimeSeconds = idle;
    }

    /**
     * Size in bytes. This is not used except in the admin pages. It will be -1 by default.
     * <p>
     * @param size The new size value
     */
    public void setSize( int size )
    {
        this.size = size;
    }

    /**
     * Gets the size attribute of the IAttributes object
     * <p>
     * @return The size value
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Gets the createTime attribute of the IAttributes object.
     * <p>
     * This should be the current time in milliseconds returned by the sysutem call when the element
     * is put in the cache.
     * <p>
     * Putting an item in the cache overrides any existing items.
     * @return The createTime value
     */
    public long getCreateTime()
    {
        return createTime;
    }

    /**
     * Sets the createTime attribute of the IElementAttributes object
     */
    public void setCreateTime()
    {
        createTime = System.currentTimeMillis();
    }

    /**
     * Gets the idleTime attribute of the IAttributes object.
     * <p>
     * @return The idleTime value
     */
    public long getIdleTime()
    {
        return this.maxIdleTimeSeconds;
    }

    /**
     * Gets the time left to live of the IAttributes object.
     * <p>
     * This is the (max life + create time) - current time.
     * @return The TimeToLiveSeconds value
     */
    public long getTimeToLiveSeconds()
    {
        long now = System.currentTimeMillis();
        return ( this.getCreateTime() + this.getMaxLifeSeconds() * 1000 - now ) / 1000;
    }

    /**
     * Gets the LastAccess attribute of the IAttributes object.
     * <p>
     * @return The LastAccess value.
     */
    public long getLastAccessTime()
    {
        return this.lastAccessTime;
    }

    /**
     * Sets the LastAccessTime as now of the IElementAttributes object
     */
    public void setLastAccessTimeNow()
    {
        this.lastAccessTime = System.currentTimeMillis();
    }

    /**
     * Can this item be spooled to disk
     * <p>
     * By default this is true.
     * @return The spoolable value
     */
    public boolean getIsSpool()
    {
        return this.IS_SPOOL;
    }

    /**
     * Sets the isSpool attribute of the IElementAttributes object
     * <p>
     * By default this is true.
     * @param val The new isSpool value
     */
    public void setIsSpool( boolean val )
    {
        this.IS_SPOOL = val;
    }

    /**
     * Is this item laterally distributable. Can it be sent to auxiliaries of type lateral.
     * <p>
     * By default this is true.
     * @return The isLateral value
     */
    public boolean getIsLateral()
    {
        return this.IS_LATERAL;
    }

    /**
     * Sets the isLateral attribute of the IElementAttributes object
     * <p>
     * By default this is true.
     * @param val The new isLateral value
     */
    public void setIsLateral( boolean val )
    {
        this.IS_LATERAL = val;
    }

    /**
     * Can this item be sent to the remote cache
     * @return true if the item can be sent to a remote auxiliary
     */
    public boolean getIsRemote()
    {
        return this.IS_REMOTE;
    }

    /**
     * Sets the isRemote attribute of the ElementAttributes object
     * @param val The new isRemote value
     */
    public void setIsRemote( boolean val )
    {
        this.IS_REMOTE = val;
    }

    /**
     * You can turn off expiration by setting this to true. The max life value will be ignored.
     * <p>
     * @return true if the item cannot expire.
     */
    public boolean getIsEternal()
    {
        return this.IS_ETERNAL;
    }

    /**
     * Sets the isEternal attribute of the ElementAttributes object. True means that the item should
     * never expire. If can still be removed if it is the least recently used, and you are using the
     * LRUMemory cache. it just will not be filtered for expiration by the cache hub.
     * <p>
     * @param val The new isEternal value
     */
    public void setIsEternal( boolean val )
    {
        this.IS_ETERNAL = val;
    }

    /**
     * Adds a ElementEventHandler. Handler's can be registered for multiple events. A registered
     * handler will be called at every recognized event.
     * <p>
     * The alternative would be to register handlers for each event. Or maybe The handler interface
     * should have a method to return whether it cares about certain events.
     * <p>
     * @param eventHandler The ElementEventHandler to be added to the list.
     */
    public void addElementEventHandler( IElementEventHandler eventHandler )
    {
        // lazy here, no concurrency problems expected
        if ( this.eventHandlers == null )
        {
            this.eventHandlers = new ArrayList<IElementEventHandler>();
        }
        this.eventHandlers.add( eventHandler );
    }

    /**
     * Sets the eventHandlers of the IElementAttributes object.
     * <p>
     * This add the references to the local list. Subsequent changes in the caller's list will not
     * be reflected.
     * <p>
     * @param eventHandlers List of IElementEventHandler objects
     */
    public void addElementEventHandlers( ArrayList<IElementEventHandler> eventHandlers )
    {
        if ( eventHandlers == null )
        {
            return;
        }

        for (IElementEventHandler handler : eventHandlers)
        {
            addElementEventHandler(handler);
        }
    }

    /**
     * Gets the elementEventHandlers. Returns null if none exist. Makes checking easy.
     * <p>
     * @return The elementEventHandlers List of IElementEventHandler objects
     */
    public ArrayList<IElementEventHandler> getElementEventHandlers()
    {
        return this.eventHandlers;
    }

    /**
     * For logging and debugging the element IElementAttributes.
     * <p>
     * @return String info about the values.
     */
    @Override
    public String toString()
    {
        StringBuffer dump = new StringBuffer();

        dump.append( "[ IS_LATERAL = " ).append( IS_LATERAL );
        dump.append( ", IS_SPOOL = " ).append( IS_SPOOL );
        dump.append( ", IS_REMOTE = " ).append( IS_REMOTE );
        dump.append( ", IS_ETERNAL = " ).append( IS_ETERNAL );
        dump.append( ", MaxLifeSeconds = " ).append( this.getMaxLifeSeconds() );
        dump.append( ", IdleTime = " ).append( this.getIdleTime() );
        dump.append( ", CreateTime = " ).append( this.getCreateTime() );
        dump.append( ", LastAccessTime = " ).append( this.getLastAccessTime() );
        dump.append( ", getTimeToLiveSeconds() = " ).append( String.valueOf( getTimeToLiveSeconds() ) );
        dump.append( ", createTime = " ).append( String.valueOf( createTime ) ).append( " ]" );

        return dump.toString();
    }
}
