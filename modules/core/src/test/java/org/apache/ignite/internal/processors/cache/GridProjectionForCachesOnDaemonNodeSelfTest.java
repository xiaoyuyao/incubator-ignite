/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.cache;

import org.apache.ignite.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.spi.discovery.*;
import org.apache.ignite.spi.discovery.tcp.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.*;
import org.apache.ignite.testframework.junits.common.*;

/**
 * Tests of cache related cluster projections for daemon node.
 */
public class GridProjectionForCachesOnDaemonNodeSelfTest extends GridCommonAbstractTest {
    /** Ip finder. */
    private static final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /** Daemon node. */
    private static boolean daemonNode;

    /** Daemon. */
    private static Ignite ignite;

    /** Daemon. */
    private static Ignite daemon;

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        cfg.setDiscoverySpi(discoverySpi());

        cfg.setDaemon(daemonNode);

        return cfg;
    }

    /**
     * @return Discovery SPI;
     */
    private DiscoverySpi discoverySpi() {
        TcpDiscoverySpi spi = new TcpDiscoverySpi();

        spi.setIpFinder(IP_FINDER);

        return spi;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        ignite = startGrid(0);

        daemonNode = true;

        daemon = startGrid(1);

        assert ((IgniteKernal)daemon).localNode().isDaemon();
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        stopAllGrids();
    }

    /** {@inheritDoc} */
    protected void beforeTest() throws Exception {
        ignite.getOrCreateCache((String)null);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        ignite.cache(null).close();
    }

    /**
     * @throws Exception If failed.
     */
    public void testForDataNodes() throws Exception {
        ClusterGroup grp = ignite.cluster().forDataNodes(null);

        assertFalse(grp.nodes().isEmpty());

        try {
            daemon.cluster().forDataNodes(null);
        }
        catch (IllegalStateException e) {
            return;
        }

        fail();
    }

    /**
     * @throws Exception If failed.
     */
    public void testForClientNodes() throws Exception {
        ClusterGroup grp = ignite.cluster().forClientNodes(null);

        assertTrue(grp.nodes().isEmpty());

        try {
            daemon.cluster().forClientNodes(null);
        }
        catch (IllegalStateException e) {
            return;
        }

        fail();
    }

    /**
     * @throws Exception If failed.
     */
    public void testForCacheNodes() throws Exception {
        ClusterGroup grp = ignite.cluster().forCacheNodes(null);

        assertFalse(grp.nodes().isEmpty());

        try {
            daemon.cluster().forCacheNodes(null);
        }
        catch (IllegalStateException e) {
            return;
        }

        fail();
    }
}
