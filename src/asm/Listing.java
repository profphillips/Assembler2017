/*
 * The MIT License
 *
 * Copyright 2017 John Phillips.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a full listing from the given list of source code. In addition, it
 * creates a separate list of machine code that can be used by the MUCPU 2017
 * Simulator.
 *
 * @version 20170327
 * @author John Phillips
 */
public class Listing {

    List<Line> listing;             // list of parsed lines of code
    Map<String, Integer> labelMap;  // list of labels and their addresses

    /**
     * Constructor takes a source list and then with the first pass parses the
     * source code looking up the opcodes and with the second pass fills in the
     * operand label addresses.
     *
     * @param source
     */
    public Listing(List<String> source) {
        listing = new ArrayList();
        labelMap = new HashMap();
        int programCounter = 0;     // program counter starts at address 0

        // first pass to step through each line of source code
        for (String s : source) {

            // line will contain the parsed source code
            Line line = new Line(s);
            listing.add(line);

            // do the following steps if this line is not a comment
            if (!line.isComment()) {

                // set the program counter as a 2 digit hex value
                line.setProgramCounter(int2Hex(programCounter));

                // if we have a label declaration then add it to the map
                // with the current value of the program counter
                if (!line.getLabel().equals("")) {
                    labelMap.put(line.getLabel(), programCounter);
                }

                // if this is a data byte then increment pc by 1 otherwise by 2
                if (line.getCommand().equals("DB")) {
                    programCounter++;
                } else {
                    programCounter += 2;
                }
            }
        }

        // This is the second pass. We step through each line from the parsed 
        // code list and look up the operand labels in the labelMap. 
        // These address values are converted to hex and stored in each line's 
        // operandHex field.
        for (Line line : listing) {
            if (!line.isComment()) {
                String op = line.getOperand();
                if (!op.equals("")) {
                    if (op.matches("[0-9A-F]{2}")) {
                        line.setOperandHex(op);
                    } else if (labelMap.containsKey(op)) {
                        line.setOperandHex(int2Hex(labelMap.get(op)));
                    } else {
                        line.setErrors(line.getErrors() + "OPERAND FORMAT OR SPELLING ERROR (PASS 2), ");
                    }
                } else {
                    line.setErrors(line.getErrors() + "OPERAND NOT FOUND ERROR(PASS 2), ");
                }
            }
        }
    }

    public static String int2Hex(int i) {
        String myHex = Integer.toHexString(i).toUpperCase();
        if (myHex.length() == 1) {
            myHex = "0" + myHex;
        }
        return myHex;
    }

    public String getMachineCodeAsString() {
        StringBuilder sb = new StringBuilder("");
        for (Line line : listing) {
            if (!line.isComment()) {
                if (!line.getCommand().equalsIgnoreCase("DB")) {
                    sb.append(line.getOpcodeHex()).append("\n");
                }
                sb.append(line.getOperandHex()).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (Line line : listing) {
            sb.append(line);
        }
        return sb.toString();
    }

    // Short test program.
    public static void main(String[] args) {
        List<String> source = new ArrayList();
        source.add("; this is a comment.");
        source.add("LOOP1: LOD NUM1");
        source.add("       Add num2");
        source.add("LOOP2: JMP 00");
        source.add("LAST:"); // this is an error as labels have to be part of a full instruction.
        source.add("       HLT 80");
        source.add("NUM1:  DB 5A");
        source.add("NUM2:  DB ff");
        source.add("NUM3:  DB 78");

        Listing myListing = new Listing(source);

        System.out.println("Full code listing:");
        System.out.println(myListing);

        System.out.println("Machine code listing:");
        System.out.println(myListing.getMachineCodeAsString());

        System.out.println("Label Map listing:");
        myListing.labelMap.forEach((key, value) -> {
            System.out.println("Key: " + key + " Value: " + value);
        });
    }
}
