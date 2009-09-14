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
package com.sun.c1x.ir;

import com.sun.c1x.*;
import com.sun.c1x.ci.*;
import com.sun.c1x.value.*;

/**
 * The <code>Intrinsic</code> instruction represents a call to a JDK method
 * that has been made {@linkplain C1XIntrinsic intrinsic}.
 *
 * @author Ben L. Titzer
 * @see C1XIntrinsic
 */
public class Intrinsic extends StateSplit {

    final C1XIntrinsic intrinsic;
    final Value[] arguments;
    final boolean canTrap;

    /**
     * Creates a new Intrinsic instruction.
     * @param type the result type of the instruction
     * @param intrinsic the actual intrinsic
     * @param args the arguments to the call (including the receiver object)
     * @param isStatic <code>true</code> if this method is static
     * @param stateBefore the lock stack
     * @param preservesState <code>true</code> if the implementation of this intrinsic preserves register state
     * @param canTrap <code>true</code> if this intrinsic can cause a trap
     */
    public Intrinsic(CiKind type, C1XIntrinsic intrinsic, Value[] args, boolean isStatic,
                     ValueStack stateBefore, boolean preservesState, boolean canTrap) {
        super(type, stateBefore);
        this.intrinsic = intrinsic;
        this.arguments = args;
        initFlag(Flag.IsStatic, isStatic);
        // Preserves state means that the intrinsic preserves register state across all cases,
        // including slow cases--even if it causes a trap. If so, it can still be a candidate
        // for load elimination and common subexpression elimination
        initFlag(Flag.PreservesState, preservesState);
        this.canTrap = canTrap;
        if (!isStatic && args[0].isNonNull()) {
            redundantNullCheck();
        }
    }

    /**
     * Gets the intrinsic represented by this instruction.
     * @return the intrinsic
     */
    public C1XIntrinsic intrinsic() {
        return intrinsic;
    }

    /**
     * Gets the list of instructions that produce input for this instruction.
     * @return the list of instructions that produce input
     */
    public Value[] arguments() {
        return arguments;
    }

    public boolean isStatic() {
        return checkFlag(Flag.IsStatic);
    }

    /**
     * Checks whether this intrinsic has a receiver object.
     * @return <code>true</code> if this intrinsic has a receiver object
     */
    public boolean hasReceiver() {
        return !isStatic();
    }

    /**
     * Gets the instruction which produces the receiver object for this intrinsic.
     * @return the instruction producing the receiver object
     */
    public Value receiver() {
        assert !isStatic();
        return arguments[0];
    }

    /**
     * Checks whether this intrinsic preserves the state of registers across all cases.
     * @return <code>true</code> if this intrinsic always preserves register state
     */
    public boolean preservesState() {
        return checkFlag(Flag.PreservesState);
    }

    /**
     * Checks whether this intrinsic can cause a trap.
     * @return <code>true</code> if this intrinsic can cause a trap
     */
    @Override
    public boolean canTrap() {
        return canTrap;
    }

    @Override
    public boolean internalClearNullCheck() {
        return true;
    }

    /**
     * Iterates over the input values to this instruction.
     * @param closure the closure to apply
     */
    @Override
    public void inputValuesDo(ValueClosure closure) {
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = closure.apply(arguments[i]);
        }
    }

    /**
     * Implements this instruction's half of the visitor pattern.
     * @param v the visitor to accept
     */
    @Override
    public void accept(ValueVisitor v) {
        v.visitIntrinsic(this);
    }

    public Value argumentAt(int i) {
        return arguments[i];
    }

    public int numberOfArguments() {
        return arguments.length;
    }
}
