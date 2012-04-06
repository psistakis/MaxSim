/*
 * Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.graal.nodes;

import com.oracle.max.cri.ci.*;
import com.oracle.graal.graph.*;
import com.oracle.graal.nodes.spi.*;
import com.oracle.graal.nodes.type.*;

public final class ReturnNode extends FixedNode implements LIRLowerable, Node.IterableNodeType {

    @Input private ValueNode result;

    public ValueNode result() {
        return result;
    }

    /**
     * Constructs a new Return instruction.
     * @param result the instruction producing the result for this return; {@code null} if this is a void return
     */
    public ReturnNode(ValueNode result) {
        super(StampFactory.forKind(result == null ? CiKind.Void : result.kind()));
        this.result = result;
    }

    @Override
    public void generate(LIRGeneratorTool gen) {
        gen.visitReturn(this);
    }
}
