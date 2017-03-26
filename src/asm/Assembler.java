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

import java.util.List;
import java.util.Scanner;
import util.FileUtil;

/**
 * This class is the starting point when running the MUCPU 2017 Assembler.
 *
 * @author John Phillips
 */
public class Assembler {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String choice = "1";
        String filename = "source.txt";
        List<String> source;
        Listing listing = null;
        while (!choice.equals("0")) {
            System.out.println("\nJohn's MUCPU 2017 Assembler (" + filename + ")");
            System.out.println("0 = Quit");
            System.out.println("1 = Set Source Filename");
            System.out.println("2 = Load and Assemble");
            System.out.println("3 = View Listing");
            System.out.println("4 = View Machine Code");
            System.out.print("Enter choice: ");
            choice = sc.nextLine();

            if (choice.equals("1")) {
                System.out.print("\nName of yoursourcefile.txt: ");
                filename = sc.nextLine();
            } else if (choice.equals("2")) {
                if (!filename.equals("")) {
                    source = FileUtil.readAllF2L(filename);
                    listing = new Listing(source);
                    System.out.println("Assembly complete.");
                } else {
                    System.out.println("Choose option 1 to set the filename...");
                }
            } else if (choice.equals("3")) {
                if (listing != null) {
                    System.out.println("\n\nLISTING OF " + filename + "\n");
                    System.out.println(listing);
                }
            } else if (choice.equals("4")) {
                if (listing != null) {
                    System.out.println("\n\nMACHINE CODE\n");
                    System.out.println(listing.getMachineCodeAsString());
                }
            }
        }
    }
}
