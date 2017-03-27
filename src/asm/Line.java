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
 * Objects from this class hold a single line of an assembly language listing.
 * Given that this is a very simple assembler, each line of MUCPU 2017 assembly
 * language source code must follow the following rules:
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
 * @version 20170327
 * @author John Phillips
 */
public class Line {

    // Regular expression pattern to parse an instrution as follows:
    //   \\s* skip zero or more white space characters
    //   ((\\w+):)? captures an optional label declaration
    //   \\s* skip zero or more white space characters
    //   (\\w+) capture the command
    //   \\s+ skip one or more white space characters
    //   (\\w+) captures the operand
    private final static Pattern STATEMENT_PATTERN
            = Pattern.compile("\\s*((\\w+):)?\\s*(\\w+)\\s+(\\w+)",
                    Pattern.CASE_INSENSITIVE);

    private String source;          // uppercase of original source line
    private String programCounter;  // pc hex value for this line
    private String label;           // optional label declaration
    private String command;         // command mneumonic
    private String opcodeHex;       // line fills in the corresponding opcode
    private String operand;         // operand may be a label or hex value
    private String operandHex;      // Listing.java converts labels to hex
    private String errors;           // Line's errors messages are kept here.
    private boolean comment;        // True if this line is a comment

    /**
     * Constructor receives a source code line and parses it.
     *
     * @param source
     */
    public Line(String source) {
        this.source = source.toUpperCase();
        programCounter = "";
        label = "";
        command = "";
        opcodeHex = "";
        operand = "";
        operandHex = "";
        errors = "";
        comment = false;
        parse();
    }

    /**
     * Map to convert commands into opcodes; based on map code from
     * http://minborgsjavapot.blogspot.com/2014/12/java-8-initializing-maps-in-smartest-way.html
     *
     * @return
     */
    // 
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

    /**
     * Takes the source string and extracts whether it is a comment and if not
     * it extracts the other parts of the statement including an optional label,
     * the command, and the operand. The command is looked up using a Map data
     * structure allowing the opcodeHex field to be filled in.
     */
    public final void parse() {
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
                        errors += "COMMAND NOT FOUND ERROR, ";
                    }
                } else {
                    errors += "MISSING COMMAND ERROR, ";
                }
                if (m.group(4) != null) {
                    operand = m.group(4);
                } else {
                    errors += "MISSING OPERAND ERROR, ";
                }
            } else {
                errors += "INVALID LINE ERROR, ";
            }
        }
    }

    /**
     * Returns any errors along with a standard formatted line listing for this
     * line.
     *
     * @return
     */
    @Override
    public String toString() {
        String s = "";
        if (!errors.equals("")) {
            s += errors + "\n";
        }
        s += String.format("%-2s %-2s %-2s %-1s\n", programCounter, opcodeHex, operandHex, source);
        return s;
    }

    // Netbeans generated getters and setters:
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

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    // Short test program
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

}
