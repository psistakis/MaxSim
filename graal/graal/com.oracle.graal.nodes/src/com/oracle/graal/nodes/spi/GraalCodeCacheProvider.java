/*
 * Copyright (c) 2011, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.graal.nodes.spi;

import com.oracle.graal.api.code.*;
import com.oracle.graal.api.meta.*;
import com.oracle.graal.graph.*;
import com.oracle.graal.nodes.*;
import com.oracle.graal.nodes.extended.*;

/**
 * Graal-specific extensions for the code cache provider interface.
 */
public interface GraalCodeCacheProvider extends CodeCacheProvider {

    /**
     * Adds the given compilation result as an implementation of the given method without making it
     * the default implementation. The graph might be inlined later on.
     * 
     * @param method a method to which the executable code is begin added
     * @param compResult the compilation result to be added
     * @param graph the graph that represents the method
     * @return a reference to the compiled and ready-to-run code or null if the code installation
     *         failed
     */
    InstalledCode addMethod(ResolvedJavaMethod method, CompilationResult compResult, Graph graph);

    void lower(Node n, LoweringTool tool);

    /**
     * Reconstructs the array index from a location node that was created as a lowering of an indexed
     * access to an array.
     * 
     * @param location a location pointing to an element in an array
     * @return a node that gives the index of the element
     */
    ValueNode reconstructArrayIndex(LocationNode location);
}
