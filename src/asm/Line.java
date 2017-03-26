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

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Each line of MUCPU 2017 assembly language code must follow the following
 * rules:
 *
 * 1. No blank lines allowed.
 *
 * 2. Comments must begin in the first column with a semicolon (;). No partial
 * line comments are allowed.
 *
 * 3. Label declarations must appear on the left side of the statement and
 * consist of letters and numbers and end with a colon (:). Each label may only
 * be declared once (left side of statement with colon) but can be used in
 * multiple places (right side of statement without colon). Labels cannot be
 * declared on their own line but must be part of an instruction.
 *
 * 4. The program counter will start at zero. All instructions will take 2
 * bytes. The HLT command will take 2 bytes and mark the end of the code
 * section. The data section follows the code section. Single byte DB (data
 * byte) values may be placed immediately after the HLT command. As we only have
 * one general purpose register (A), the register name is omitted from each
 * command. Also, we only have one kind of addressing for each instruction.
 * Therefore, commands can be simplified to take the form LOD 20 to load a value
 * from address 20h instead of the more common LOD A, [20].
 *
 * 5. All numeric values are in hexadecimal and consist of exactly 2 characters.
 * For example, zero would be 00.
 *
 * @author John Phillips
 */
public class Line {

    private final static Pattern STATEMENT_PATTERN
            = Pattern.compile("\\s*?((\\w+):)?\\s*(\\w+)\\s+(\\w+)",
                    Pattern.CASE_INSENSITIVE);

    private String source;
    private String programCounter;
    private String label;
    private String command;
    private String opcodeHex;
    private String operand;
    private String operandHex;
    private boolean comment;

    public Line(String source) {
        this.source = source;
        programCounter = "";
        label = "";
        command = "";
        opcodeHex = "";
        operand = "";
        operandHex = "";
        comment = false;
        parse();
    }

    // Map to convert commands into opcodes; based on map code from
    // http://minborgsjavapot.blogspot.com/2014/12/java-8-initializing-maps-in-smartest-way.html
    protected static Map<String, String> commandMap() {
        return Collections.unmodifiableMap(Stream.of(
                new SimpleEntry<>("LOD", "01"),
                new SimpleEntry<>("STO", "02"),
                new SimpleEntry<>("OUT", "04"),
                new SimpleEntry<>("ADD", "08"),
                new SimpleEntry<>("ADC", "10"),
                new SimpleEntry<>("JNZ", "20"),
                new SimpleEntry<>("JMP", "40"),
                new SimpleEntry<>("HLT", "80"),
                new SimpleEntry<>("DB", "  "))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    }

    private void parse() {
        if (source.startsWith(";")) {
            comment = true;
        } else {
            Matcher m = STATEMENT_PATTERN.matcher(source.toUpperCase());
            if (m.matches()) {
                if (m.group(2) != null) {
                    label = m.group(2);
                }
                if (m.group(3) != null) {
                    command = m.group(3);
                    if (commandMap().containsKey(command)) {
                        opcodeHex = commandMap().get(command);
                    } else {
                        System.out.println("COMMAND NOT FOUND ERROR: " + source);
                    }
                } else {
                    System.out.println("MISSING COMMAND ERROR: " + source);
                }
                if (m.group(4) != null) {
                    operand = m.group(4);
                } else {
                    System.out.println("MISSING OPERAND ERROR: " + source);
                }
            } else {
                System.out.println("INVALID LINE ERROR: " + source);
            }
        }
    }

    public static void main(String[] args) {
        Line l1 = new Line("; this is a comment.");
        System.out.println(l1);
        Line l2 = new Line(" LOOP1: LOD 20");
        System.out.println(l2);
        Line l3 = new Line("  STO 30");
        System.out.println(l3);
        Line l4 = new Line(" DB 15");
        System.out.println(l4);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(String programCounter) {
        this.programCounter = programCounter;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOpcodeHex() {
        return opcodeHex;
    }

    public void setOpcodeHex(String opcodeHex) {
        this.opcodeHex = opcodeHex;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public String getOperandHex() {
        return operandHex;
    }

    public void setOperandHex(String operandHex) {
        this.operandHex = operandHex;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
//        return "Line{" + "source=" + source + ", programCounter=" + programCounter + ", label=" + label + ", operation=" + command + ", opcodeHex=" + opcodeHex + ", operand=" + operand + ", operandHex=" + operandHex + ", comment=" + comment + '}';
        return String.format("%-2s %-2s %-2s %-1s\n", programCounter, opcodeHex, operandHex, source);
    }

}
