/*
 * Copyright (c) 2007 Sun Microsystems, Inc.  All rights reserved.
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
package com.sun.max.asm.amd64;

import com.sun.max.asm.*;
import com.sun.max.lang.*;

/**
 * Abstract base class for AMD64 assemblers.
 *
 * @author Bernd Mathiske
 */
public abstract class AbstractAMD64Assembler extends LittleEndianAssembler implements Assembler64 {

    @Override
    public final InstructionSet instructionSet() {
        return InstructionSet.AMD64;
    }

    @Override
    public WordWidth wordWidth() {
        return WordWidth.BITS_64;
    }

    private long startAddress; // address of first instruction

    public AbstractAMD64Assembler(long startAddress) {
        this.startAddress = startAddress;
    }

    public AbstractAMD64Assembler() {
    }

    @Override
    public long baseAddress() {
        return startAddress;
    }

    public long startAddress() {
        return startAddress;
    }

    public void setStartAddress(long address) {
        startAddress = address;
    }

    public void fixLabel(Label label, long address) {
        fixLabel64(label, address);
    }

    public long address(Label label) throws AssemblyException {
        return address64(label);
    }
}
