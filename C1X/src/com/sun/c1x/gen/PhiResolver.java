/*
 * Copyright (c) 2009 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to technology embodied in the product
 * that is described in this document. In particular, and without limitation, these intellectual property
 * rights may include one or more of the U.S. patents listed at http://www.sun.com/patents and one or
 * more additional patents or pending patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software. Government users are subject to the Sun
 * Microsystems, Inc. standard license agreement and applicable provisions of the FAR and its
 * supplements.
 *
 * Use is subject to license terms. Sun, Sun Microsystems, the Sun logo, Java and Solaris are trademarks or
 * registered trademarks of Sun Microsystems, Inc. in the U.S. and other countries. All SPARC trademarks
 * are used under license and are trademarks or registered trademarks of SPARC International, Inc. in the
 * U.S. and other countries.
 *
 * UNIX is a registered trademark in the U.S. and other countries, exclusively licensed through X/Open
 * Company, Ltd.
 */
package com.sun.c1x.gen;

import java.util.*;

import com.sun.c1x.lir.*;
import static com.sun.c1x.lir.LIROperand.*;

/**
 * Converts the HIR program's Phi instructions into moves.
 *
 * @author Marcelo Cintra
 * @author Thomas Wuerthinger
 */
public class PhiResolver {

    public static class PhiResolverState {

        private List<ResolveNode> virtualOperands;
        private List<ResolveNode> otherOperands;
        private ResolveNode[] varTable;

        void reset(int maxVars) {
            virtualOperands = new ArrayList<ResolveNode>(maxVars);
            otherOperands = new ArrayList<ResolveNode>(maxVars);
            varTable = new ResolveNode[maxVars];

        }
    }

    private final LIRGenerator gen;
    private final PhiResolverState state;

    private ResolveNode loop;
    private LIROperand temp;

    public PhiResolver(LIRGenerator gen, int maxVars) {
        this.gen = gen;
        state = new PhiResolverState();
        state.reset(maxVars);
        temp = IllegalLocation;
    }

    public void dispose() {
        int i;
        // resolve any cycles in moves from and to variables
        for (i = virtualOperands().size() - 1; i >= 0; i--) {
            ResolveNode node = virtualOperands().get(i);
            if (!node.visited) {
                loop = null;
                move(null, node);
                node.startNode = true;
                assert isIllegal(temp) : "moveTempTo() call missing";
            }
        }

        // generate move for move from non variable to abitrary destination
        for (i = otherOperands().size() - 1; i >= 0; i--) {
            ResolveNode node = otherOperands().get(i);
            for (int j = node.numDestinations() - 1; j >= 0; j--) {
                emitMove(node.operand, node.destinationAt(j).operand);
            }
        }
    }

    public void move(LIROperand src, LIROperand dest) {
        assert dest.isVariable() : "destination must be virtual";
        // tty.print("move "); src.print(); tty.print(" to "); dest.print(); tty.cr();
        assert isLegal(src) : "source for phi move is illegal";
        assert isLegal(dest) : "destination for phi move is illegal";
        ResolveNode source = sourceNode(src);
        source.append(destinationNode(dest));
      }

    private ResolveNode createNode(LIROperand opr, boolean source) {
        ResolveNode node;
        if (opr.isVariable()) {
            int varNum = opr.variableNumber();
            node = varTable()[varNum];
            assert node == null || node.operand == opr;
            if (node == null) {
                node = new ResolveNode(opr);
                varTable()[varNum] = node;
            }
            // Make sure that all virtual operands show up in the list when
            // they are used as the source of a move.
            if (source && !virtualOperands().contains(node)) {
                virtualOperands().add(node);
            }
        } else {
            assert source;
            node = new ResolveNode(opr);
            otherOperands().add(node);
        }
        return node;
    }

    private ResolveNode destinationNode(LIROperand opr) {
        return createNode(opr, false);
    }

    private void emitMove(LIROperand src, LIROperand dest) {
        assert isLegal(src);
        assert isLegal(dest);
        gen().lir.move(src, dest);
    }

    private LIRGenerator gen() {
        return gen;
    }

    // Traverse assignment graph in depth first order and generate moves in post order
    // ie. two assignments: b := c, a := b start with node c:
    // Call graph: move(NULL, c) -> move(c, b) -> move(b, a)
    // Generates moves in this order: move b to a and move c to b
    // ie. cycle a := b, b := a start with node a
    // Call graph: move(NULL, a) -> move(a, b) -> move(b, a)
    // Generates moves in this order: move b to temp, move a to b, move temp to a
    private void move(ResolveNode src, ResolveNode dest) {
        if (!dest.visited) {
            dest.visited = true;
            for (int i = dest.numDestinations() - 1; i >= 0; i--) {
                move(dest, dest.destinationAt(i));
            }
        } else if (!dest.startNode) {
            // cylce in graph detected
            assert loop == null : "only one loop valid!";
            loop = dest;
            moveToTemp(src.operand);
            return;
        } // else dest is a start node

        if (!dest.assigned) {
            if (loop == dest) {
                moveTempTo(dest.operand);
                dest.assigned = true;
            } else if (src != null) {
                emitMove(src.operand, dest.operand);
                dest.assigned = true;
            }
        }
    }

    private void moveTempTo(LIROperand dest) {
        assert isLegal(temp);
        emitMove(temp, dest);
        temp = IllegalLocation;
    }

    private void moveToTemp(LIROperand src) {
        assert isIllegal(temp);
        temp = gen().newRegister(src.kind);
        emitMove(src, temp);
    }

    private List<ResolveNode> otherOperands() {
        return state.otherOperands;
    }

    private ResolveNode sourceNode(LIROperand opr) {
        return createNode(opr, true);
    }

    private List<ResolveNode> virtualOperands() {
        return state.virtualOperands;
    }

    private ResolveNode[] varTable() {
        return state.varTable;
    }
}
