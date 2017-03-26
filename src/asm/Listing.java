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
 *
 * @author John Phillips
 */
public class Listing {

    List<Line> listing;
    Map<String, Integer> labelMap;
    
    public Listing(List<String> source){
        listing = new ArrayList();
        int pc = 0;

        labelMap = new HashMap();

        for (String s : source) {
            Line line = new Line(s.toUpperCase());
            listing.add(line);

            if (!line.isComment()) {
                line.setProgramCounter(int2Hex(pc));
                if (!line.getLabel().equals("")) {
                    labelMap.put(line.getLabel(), pc);
                }
                if (line.getCommand().equals("DB")) {
                    pc++;
                } else {
                    pc += 2;
                }
            }
        }

        // fixup labels
        for (Line line : listing) {
            if (!line.isComment()) {
                String op = line.getOperand();
                if (!op.equals("")) {
                    if (op.matches("[0-9A-F]{2}")) {
                        line.setOperandHex(op);
                    } else if (labelMap.containsKey(op)) {
                        line.setOperandHex(int2Hex(labelMap.get(op)));
                    } else {
                        System.out.println("OPERAND FORMAT OR SPELLING ERROR: " + line);
                    }
                } else {
                    System.out.println("OPERAND NOT FOUND ERROR: " + line);
                }
            }
        }
    }

    public String int2Hex(int i) {
        String myHex = Integer.toHexString(i).toUpperCase();
        if (myHex.length() == 1) {
            myHex = "0" + myHex;
        }
        return myHex;
    }

    public List<Line> getListingAsList() {
        return null;
    }

    public String getListingAsString() {
        return this.toString();
    }

    public List<String> getMachineCodeAsList() {
        return null;
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

    public static void main(String[] args) {
        
        List<String> source = new ArrayList();

        source.add("; this is a comment.");
        source.add("LOOP1: LOD NUM1");
        source.add("       Add num2");
        source.add("LOOP2: JMP 00");
//        source.add("LAST:");
        source.add("       HLT 80");
        source.add("NUM1:  DB 5A");
        source.add("NUM2:  DB ff");
        source.add("NUM3:  DB 78");
        Listing myListing = new Listing(source);
        System.out.println(myListing);
        System.out.println(myListing.getMachineCodeAsString());
//        myListing.labelMap.forEach((key, value) -> {
//            System.out.println("Key: " + key + " Value: " + value);
//        });

    }
}
